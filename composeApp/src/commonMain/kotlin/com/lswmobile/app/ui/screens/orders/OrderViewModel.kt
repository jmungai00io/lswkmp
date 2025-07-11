package com.lswmobile.app.ui.screens.orders

import com.lswmobile.app.network.model.OrderWithFullUser
import com.lswmobile.app.network.model.OrderWithUserId
import com.lswmobile.app.network.repository.OrderRepository
import com.lswmobile.app.network.repository.WalletService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for order management screens
 */
class OrderViewModel(
    private val repository: OrderRepository,
    private val walletService: WalletService
) {
    
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    
    // UI state for orders list
    private val _isLoadingOrders = MutableStateFlow(false)
    val isLoadingOrders: StateFlow<Boolean> = _isLoadingOrders.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _orders = MutableStateFlow<List<OrderWithUserId>>(emptyList())
    val orders: StateFlow<List<OrderWithUserId>> = _orders.asStateFlow()
    
    // UI state for order details
    private val _isLoadingOrderDetail = MutableStateFlow(false)
    val isLoadingOrderDetail: StateFlow<Boolean> = _isLoadingOrderDetail.asStateFlow()
    
    private val _orderDetailErrorMessage = MutableStateFlow<String?>(null)
    val orderDetailErrorMessage: StateFlow<String?> = _orderDetailErrorMessage.asStateFlow()
    
    private val _selectedOrder = MutableStateFlow<OrderWithFullUser?>(null)
    val selectedOrder: StateFlow<OrderWithFullUser?> = _selectedOrder.asStateFlow()
    
    // Filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _filteredOrders = MutableStateFlow<List<OrderWithUserId>>(emptyList())
    val filteredOrders: StateFlow<List<OrderWithUserId>> = _filteredOrders.asStateFlow()
    
    // Wallet state
    private val _walletBalance = MutableStateFlow(0.0)
    val walletBalance: StateFlow<Double> = _walletBalance.asStateFlow()
    
    private val _isProcessingWalletPayment = MutableStateFlow(false)
    val isProcessingWalletPayment: StateFlow<Boolean> = _isProcessingWalletPayment.asStateFlow()
    
    init {
        // Observe repository states
        viewModelScope.launch {
            repository.ordersState.collect { state ->
                when (state) {
                    is OrderRepository.OrdersState.Loading -> {
                        _isLoadingOrders.value = true
                        _errorMessage.value = null
                    }
                    is OrderRepository.OrdersState.Success -> {
                        _isLoadingOrders.value = false
                        _orders.value = state.orders
                        updateFilteredOrders()
                    }
                    is OrderRepository.OrdersState.Error -> {
                        _isLoadingOrders.value = false
                        _errorMessage.value = state.message
                    }
                    else -> {
                        // Idle state, do nothing
                    }
                }
            }
        }
        
        viewModelScope.launch {
            repository.orderDetailState.collect { state ->
                when (state) {
                    is OrderRepository.OrderDetailState.Loading -> {
                        _isLoadingOrderDetail.value = true
                        _orderDetailErrorMessage.value = null
                    }
                    is OrderRepository.OrderDetailState.Success -> {
                        _isLoadingOrderDetail.value = false
                        _selectedOrder.value = state.order
                    }
                    is OrderRepository.OrderDetailState.Error -> {
                        _isLoadingOrderDetail.value = false
                        _orderDetailErrorMessage.value = state.message
                    }
                    else -> {
                        // Idle state, do nothing
                    }
                }
            }
        }
    }
    
    /**
     * Load user's orders
     */
    fun loadOrders() {
        viewModelScope.launch {
            try {
                repository.getMyOrders()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load orders"
            }
        }
    }
    
    /**
     * Load order details
     */
    fun loadOrderDetails(orderNumber: Int) {
        viewModelScope.launch {
            try {
                repository.getOrderDetails(orderNumber)
            } catch (e: Exception) {
                _orderDetailErrorMessage.value = e.message ?: "Failed to load order details"
            }
        }
    }
    
    /**
     * Update search query
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredOrders()
    }
    
    /**
     * Update filtered orders based on search query
     */
    private fun updateFilteredOrders() {
        val query = _searchQuery.value.trim().lowercase()
        _filteredOrders.update { currentOrders ->
            if (query.isEmpty()) {
                _orders.value
            } else {
                _orders.value.filter { order ->
                    order._id?.lowercase()?.contains(query) == true ||
                    order.reference?.lowercase()?.contains(query) == true ||
                    order.orderNumber.toString().contains(query) ||
                    order.status?.lowercase()?.contains(query) == true
                }
            }
        }
    }
    
    /**
     * Load wallet balance
     */
    fun loadWalletBalance() {
        viewModelScope.launch {
            try {
                val balance = walletService.getWalletBalance()
                _walletBalance.value = balance
            } catch (e: Exception) {
                println("OrderViewModel: Error loading wallet balance: ${e.message}")
            }
        }
    }
    
    /**
     * Check if wallet has sufficient balance for payment
     */
    suspend fun hasSufficientBalance(amount: Double): Boolean {
        return walletService.hasSufficientBalance(amount)
    }
    
    /**
     * Process wallet payment
     */
    fun processWalletPayment(orderNumber: Int, amount: Double) {
        viewModelScope.launch {
            try {
                _isProcessingWalletPayment.value = true
                val success = walletService.processWalletPayment(orderNumber, amount)
                if (success) {
                    // Refresh order details after successful payment
                    loadOrderDetails(orderNumber)
                    // Refresh wallet balance
                    loadWalletBalance()
                }
            } catch (e: Exception) {
                println("OrderViewModel: Error processing wallet payment: ${e.message}")
            } finally {
                _isProcessingWalletPayment.value = false
            }
        }
    }
    
    /**
     * Clear error messages
     */
    fun clearErrorMessages() {
        _errorMessage.value = null
        _orderDetailErrorMessage.value = null
    }
    
    /**
     * Reset view model state
     */
    fun resetState() {
        _isLoadingOrders.value = false
        _isLoadingOrderDetail.value = false
        _errorMessage.value = null
        _orderDetailErrorMessage.value = null
        repository.resetOrderStates()
    }
}
