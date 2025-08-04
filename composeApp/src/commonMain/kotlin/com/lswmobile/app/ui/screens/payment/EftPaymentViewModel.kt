package com.lswmobile.app.ui.screens.payment

import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.EftDetailsType
import com.lswmobile.app.network.model.OrderWithFullUser
import com.lswmobile.app.network.model.PaymentBody
import com.lswmobile.app.network.model.PaymentCompletionType
import com.lswmobile.app.network.repository.OrderRepository
import com.lswmobile.app.network.repository.WalletService
import com.lswmobile.app.utils.ErrorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for EFT payment operations
 */
class EftPaymentViewModel(
    private val api: LivestockWealthApi,
    private val orderRepository: OrderRepository,
    private val walletService: WalletService
) {
    
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    
    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isConfirming = MutableStateFlow(false)
    val isConfirming: StateFlow<Boolean> = _isConfirming.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Data state
    private val _eftDetails = MutableStateFlow<EftDetailsType?>(null)
    val eftDetails: StateFlow<EftDetailsType?> = _eftDetails.asStateFlow()
    
    private val _order = MutableStateFlow<OrderWithFullUser?>(null)
    val order: StateFlow<OrderWithFullUser?> = _order.asStateFlow()
    
    private val _walletBalance = MutableStateFlow(0.0)
    val walletBalance: StateFlow<Double> = _walletBalance.asStateFlow()
    
    /**
     * Load all required data for EFT payment
     */
    fun loadEftPaymentData(orderNumber: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Load data in parallel
                val bankDetailsJob = launch { fetchBankDetails() }
                val orderDetailsJob = launch { loadOrderDetails(orderNumber) }
                val walletBalanceJob = launch { loadWalletBalance() }
                
                // Wait for all to complete
                bankDetailsJob.join()
                orderDetailsJob.join()
                walletBalanceJob.join()
                
            } catch (e: Exception) {
                _errorMessage.value = ErrorUtils.extractErrorMessage(e, "Failed to load EFT payment data")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetch bank details for EFT
     */
    private suspend fun fetchBankDetails() {
        try {
            val response = api.getBankDetails()
            if (response.success) {
                _eftDetails.value = response.data
                println("EftPaymentViewModel: Bank details loaded successfully")
            } else {
                _errorMessage.value = "Failed to load bank details"
            }
        } catch (e: Exception) {
            _errorMessage.value = ErrorUtils.extractErrorMessage(e, "Failed to load bank details")
            println("EftPaymentViewModel: Error fetching bank details: ${e.message}")
        }
    }
    
    /**
     * Load order details
     */
    private suspend fun loadOrderDetails(orderNumber: Int) {
        try {
            orderRepository.getOrderDetails(orderNumber)
            // The order will be available through the repository's state
        } catch (e: Exception) {
            _errorMessage.value = ErrorUtils.extractErrorMessage(e, "Failed to load order details")
            println("EftPaymentViewModel: Error loading order details: ${e.message}")
        }
    }
    
    /**
     * Load wallet balance
     */
    private suspend fun loadWalletBalance() {
        try {
            val balance = walletService.getWalletBalance()
            _walletBalance.value = balance
            println("EftPaymentViewModel: Wallet balance loaded: $balance")
        } catch (e: Exception) {
            _errorMessage.value = ErrorUtils.extractErrorMessage(e, "Failed to load wallet balance")
            println("EftPaymentViewModel: Error loading wallet balance: ${e.message}")
        }
    }
    
    /**
     * Calculate payment amount based on payment type
     */
    fun calculatePaymentAmount(orderAmount: Double, walletBalance: Double, paymentType: PaymentType): Double {
        return when (paymentType) {
            PaymentType.FULL_PAYMENT -> orderAmount
            PaymentType.PARTIAL_TOPUP -> {
                val availableBalance = walletBalance
                if (availableBalance >= orderAmount) {
                    0.0 // No additional payment needed
                } else {
                    orderAmount - availableBalance
                }
            }
            PaymentType.TOPUP_ONLY -> 0.0 // Only topup, no order payment
        }
    }
    
    /**
     * Confirm EFT payment
     */
    fun confirmEftPayment(orderNumber: Int, paymentType: PaymentType, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _isConfirming.value = true
                _errorMessage.value = null
                
                println("EftPaymentViewModel: Confirming EFT payment for order #$orderNumber")
                println("EftPaymentViewModel: Payment type: $paymentType")
                
                // Create payment body with correct payment type string
                val paymentTypeString = when (paymentType) {
                    PaymentType.TOPUP_ONLY -> PaymentCompletionType.TOPUP_ONLY
                    PaymentType.PARTIAL_TOPUP -> PaymentCompletionType.PARTIAL_TOPUP
                    PaymentType.FULL_PAYMENT -> PaymentCompletionType.FULL_PAYMENT
                }
                
                val paymentBody = PaymentBody(
                    paymentMethod = "EFT",
                    paymentType = paymentTypeString
                )
                
                // Make the actual API call
                val response = api.updatePaymentMethod(orderNumber.toString(), paymentBody)
                
                println("EftPaymentViewModel: EFT payment confirmed successfully")
                println("EftPaymentViewModel: Response: $response")
                
                // Success - trigger callback
                onSuccess()
                
            } catch (e: Exception) {
                _errorMessage.value = ErrorUtils.extractErrorMessage(e, "Payment failed")
                println("EftPaymentViewModel: Error confirming EFT payment: ${e.message}")
                e.printStackTrace()
            } finally {
                _isConfirming.value = false
            }
        }
    }
    
    /**
     * Clear error messages
     */
    fun clearErrorMessages() {
        _errorMessage.value = null
    }
}

/**
 * Payment type enum
 */
enum class PaymentType {
    TOPUP_ONLY,
    PARTIAL_TOPUP,
    FULL_PAYMENT
} 