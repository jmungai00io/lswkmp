package com.lswmobile.app.network.repository

import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.PaymentBody
import com.lswmobile.app.network.model.PaymentCompletionType
import com.lswmobile.app.network.model.WalletOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Service for wallet operations
 */
class WalletService(private val api: LivestockWealthApi) {
    
    private val _walletBalance = MutableStateFlow(0.0)
    val walletBalance: Flow<Double> = _walletBalance.asStateFlow()
    
    /**
     * Get current wallet balance
     */
    suspend fun getWalletBalance(): Double {
        return try {
            val response = api.getWalletOverview()
            if (response.success) {
                // Convert negative balance to positive for UI display
                val positiveBalance = -1 * response.availableBalance
                _walletBalance.value = positiveBalance
                positiveBalance
            } else {
                0.0
            }
        } catch (e: Exception) {
            println("WalletService: Error fetching wallet balance: ${e.message}")
            0.0
        }
    }
    
    /**
     * Check if wallet has sufficient balance for payment
     */
    suspend fun hasSufficientBalance(amount: Double): Boolean {
        val balance = getWalletBalance()
        return balance >= amount
    }
    
    /**
     * Process wallet payment for an order
     */
    suspend fun processWalletPayment(orderNumber: Int, amount: Double): Boolean {
        return try {
            println("WalletService: Processing wallet payment for order #$orderNumber, amount: R$amount")
            
            // Create payment body with wallet payment type
            val paymentBody = PaymentBody(
                paymentMethod = "WALLET",
                paymentType = PaymentCompletionType.FULL_PAYMENT
            )
            
            // Make the actual API call
            val response = api.updatePaymentMethod(orderNumber.toString(), paymentBody)
            
            // If we get here without an exception, the API call was successful
            // Update wallet balance (subtract payment amount from positive balance)
            val currentBalance = _walletBalance.value
            _walletBalance.value = currentBalance - amount
            println("WalletService: Wallet payment processed successfully")
            println("WalletService: Response: $response")
            true
        } catch (e: Exception) {
            println("WalletService: Error processing wallet payment: ${e.message}")
            false
        }
    }
} 