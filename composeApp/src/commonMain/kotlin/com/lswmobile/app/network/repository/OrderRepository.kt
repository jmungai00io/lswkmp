package com.lswmobile.app.network.repository

import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.OrderWithFullUser
import com.lswmobile.app.network.model.OrderWithUserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for order operations
 */
class OrderRepository(private val api: LivestockWealthApi) {
    
    sealed class OrdersState {
        object Idle : OrdersState()
        object Loading : OrdersState()
        data class Success(val orders: List<OrderWithUserId>) : OrdersState()
        data class Error(val message: String) : OrdersState()
    }
    
    sealed class OrderDetailState {
        object Idle : OrderDetailState()
        object Loading : OrderDetailState()
        data class Success(val order: OrderWithFullUser) : OrderDetailState()
        data class Error(val message: String) : OrderDetailState()
    }
    
    // Internal state flows
    private val _ordersState = MutableStateFlow<OrdersState>(OrdersState.Idle)
    private val _orderDetailState = MutableStateFlow<OrderDetailState>(OrderDetailState.Idle)
    
    // Exposed state flows
    val ordersState: StateFlow<OrdersState> = _ordersState.asStateFlow()
    val orderDetailState: StateFlow<OrderDetailState> = _orderDetailState.asStateFlow()
    
    // Orders cache
    private val _orders = MutableStateFlow<List<OrderWithUserId>>(emptyList())
    val orders: Flow<List<OrderWithUserId>> = _orders.asStateFlow()
    
    // Currently selected order
    private val _selectedOrder = MutableStateFlow<OrderWithFullUser?>(null)
    val selectedOrder: Flow<OrderWithFullUser?> = _selectedOrder.asStateFlow()
    
    /**
     * Fetch user's orders from the API
     */
    suspend fun getMyOrders() {
        try {
            _ordersState.value = OrdersState.Loading
            
            val response = api.getMyOrders()
            
            if (response.success) {
                val ordersList = response.data
                _orders.value = ordersList
                _ordersState.value = OrdersState.Success(ordersList)
            } else {
                _ordersState.value = OrdersState.Error("Failed to fetch orders")
            }
        } catch (e: Exception) {
            _ordersState.value = OrdersState.Error(e.message ?: "Unknown error occurred while fetching orders")
        }
    }
    
    /**
     * Get order details by order number
     */
    suspend fun getOrderDetails(orderNumber: Int) {
        try {
            _orderDetailState.value = OrderDetailState.Loading
            
            val response = api.getOrderByNumber(orderNumber)
            
            if (response.success) {
                val orderDetail = response.data

                _selectedOrder.value = orderDetail
                _orderDetailState.value = OrderDetailState.Success(orderDetail)
            } else {
                _orderDetailState.value = OrderDetailState.Error("Failed to fetch order details")
            }
        } catch (e: Exception) {
            _orderDetailState.value = OrderDetailState.Error(e.message ?: "Unknown error occurred while fetching order details")
        }
    }
    
    /**
     * Reset states
     */
    fun resetOrderStates() {
        _ordersState.value = OrdersState.Idle
        _orderDetailState.value = OrderDetailState.Idle
    }
}
