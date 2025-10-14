package com.lswmobile.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lswmobile.app.network.model.*
import com.lswmobile.app.network.repository.WithdrawalRepository
import com.lswmobile.app.network.model.BankType
import com.lswmobile.app.network.repository.WithdrawalState
import com.lswmobile.app.network.repository.FeesAndBanksState
import com.lswmobile.app.network.repository.ValidationResult
import com.lswmobile.app.utils.ErrorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class WithdrawalViewModel(
    private val withdrawalRepository: WithdrawalRepository,
    private val coroutineScope: CoroutineScope
) {
    
    // Form state for request withdrawal
    var amount by mutableStateOf("")
        private set
    
    var reason by mutableStateOf("")
        private set
    
    var selectedWithdrawalType by mutableStateOf(WithdrawalType.LOCAL)
        private set
    
    // Country code for withdrawal (default to South Africa)
    var selectedCountryCode by mutableStateOf("za")
        private set
    
    // Bank details
    var accountNumber by mutableStateOf("")
        private set
    
    var accountHolderName by mutableStateOf("")
        private set
    
    var bankName by mutableStateOf("")
        private set
    
    var branchCode by mutableStateOf("")
        private set
    
    var branchName by mutableStateOf("")
        private set
    
    var swiftCode by mutableStateOf("")
        private set
    
    var iban by mutableStateOf("")
        private set
    
    var bankAddress by mutableStateOf("")
        private set
    
    var passportNumber by mutableStateOf("")
        private set
    
    var passportCountry by mutableStateOf("")
        private set
    
    // SA Bank selection
    var selectedSABank by mutableStateOf<BankType?>(null)
        private set
    
    // Terms agreement
    var agreeToTerms by mutableStateOf(false)
        private set
    
    var agreeToFees by mutableStateOf(false)
        private set
    
    // UI state
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var successMessage by mutableStateOf<String?>(null)
        private set
    
    // Data state
    var withdrawals by mutableStateOf<List<Withdrawal>>(emptyList())
        private set
    
    var selectedWithdrawal by mutableStateOf<Withdrawal?>(null)
        private set
    
    var localBanks by mutableStateOf<List<BankType>>(emptyList())
        private set
    
    var withdrawalFees by mutableStateOf<WithdrawalFees?>(null)
        private set
    
    var currentFee by mutableStateOf(0.0)
        private set
    
    var netAmount by mutableStateOf(0.0)
        private set
    
    // Validation state
    var validationErrors by mutableStateOf<List<String>>(emptyList())
        private set
    
    /**
     * Initialize the ViewModel
     */
    fun initialize() {
        loadWithdrawalFeesAndBanks()
        loadWithdrawals()
    }
    
    /**
     * Load withdrawal fees and local banks
     */
    fun loadWithdrawalFeesAndBanks() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            
            try {
                val result = withdrawalRepository.getWithdrawalFeesAndBanks()
                if (result.isSuccess) {
                    val data = result.getOrNull()
                    if (data != null) {
                        localBanks = data.banks
                        withdrawalFees = data.withdrawalFees
                        updateFee()
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    val errorException = if (exception is Exception) exception else Exception(exception?.message, exception)
                    errorMessage = ErrorUtils.extractErrorMessage(errorException, "Failed to load fees and banks")
                }
            } catch (e: Throwable) {
                val errorException = if (e is Exception) e else Exception(e.message, e)
                errorMessage = ErrorUtils.extractErrorMessage(errorException, "An unexpected error occurred")
            } finally {
                isLoading = false
            }
        }
    }
    
    /**
     * Load user's withdrawals
     */
    fun loadWithdrawals() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            
            try {
                val result = withdrawalRepository.getWithdrawals()
                if (result.isSuccess) {
                    withdrawals = result.getOrNull() ?: emptyList()
                } else {
                    val exception = result.exceptionOrNull()
                    val errorException = if (exception is Exception) exception else Exception(exception?.message, exception)
                    errorMessage = ErrorUtils.extractErrorMessage(errorException, "Failed to load withdrawals")
                }
            } catch (e: Throwable) {
                val errorException = if (e is Exception) e else Exception(e.message, e)
                errorMessage = ErrorUtils.extractErrorMessage(errorException, "An unexpected error occurred")
            } finally {
                isLoading = false
            }
        }
    }
    
    /**
     * Load withdrawal details by ID
     */
    fun loadWithdrawalDetails(withdrawalId: String) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            
            try {
                val result = withdrawalRepository.getWithdrawal(withdrawalId)
                if (result.isSuccess) {
                    selectedWithdrawal = result.getOrNull()
                } else {
                    val exception = result.exceptionOrNull()
                    val errorException = if (exception is Exception) exception else Exception(exception?.message, exception)
                    errorMessage = ErrorUtils.extractErrorMessage(errorException, "Failed to load withdrawal details")
                }
            } catch (e: Throwable) {
                val errorException = if (e is Exception) e else Exception(e.message, e)
                errorMessage = ErrorUtils.extractErrorMessage(errorException, "An unexpected error occurred")
            } finally {
                isLoading = false
            }
        }
    }
    
    /**
     * Update amount and recalculate fees
     */
    fun updateAmount(value: String) {
        amount = value
        updateFee()
    }
    
    /**
     * Update reason
     */
    fun updateReason(value: String) {
        reason = value
    }
    
    /**
     * Update withdrawal type
     */
    fun updateWithdrawalType(type: WithdrawalType) {
        selectedWithdrawalType = type
        updateFee()
    }
    
    // Update country code and automatically set the withdrawal type based on it
    fun updateCountryCode(countryCode: String) {
        selectedCountryCode = countryCode.lowercase()
        // Update withdrawal type based on country code
        selectedWithdrawalType = if (selectedCountryCode == "za") {
            WithdrawalType.LOCAL
        } else {
            WithdrawalType.INTERNATIONAL
        }
        updateFee()
    }
    
    /**
     * Update account number
     */
    fun updateAccountNumber(value: String) {
        accountNumber = value
        clearValidationErrors()
    }
    
    /**
     * Update account holder name
     */
    fun updateAccountHolderName(value: String) {
        accountHolderName = value
        clearValidationErrors()
    }
    
    /**
     * Update bank name
     */
    fun updateBankName(value: String) {
        bankName = value
        clearValidationErrors()
    }
    
    /**
     * Update branch code
     */
    fun updateBranchCode(value: String) {
        branchCode = value
        clearValidationErrors()
    }
    
    /**
     * Update branch name
     */
    fun updateBranchName(value: String) {
        branchName = value
        clearValidationErrors()
    }
    
    /**
     * Update SWIFT code
     */
    fun updateSwiftCode(value: String) {
        swiftCode = value
        clearValidationErrors()
    }
    
    /**
     * Update IBAN
     */
    fun updateIban(value: String) {
        iban = value
        clearValidationErrors()
    }
    
    /**
     * Update bank address
     */
    fun updateBankAddress(value: String) {
        bankAddress = value
        clearValidationErrors()
    }
    
    /**
     * Update passport number
     */
    fun updatePassportNumber(value: String) {
        passportNumber = value
        clearValidationErrors()
    }
    
    /**
     * Update passport country
     */
    fun updatePassportCountry(value: String) {
        passportCountry = value
        clearValidationErrors()
    }
    
    /**
     * Update selected SA bank
     */
    fun updateSelectedSABank(bank: BankType) {
        selectedSABank = bank
        bankName = bank.name
        branchCode = bank.code
        clearValidationErrors()
    }
    
    /**
     * Update terms agreement
     */
    fun updateAgreeToTerms(value: Boolean) {
        agreeToTerms = value
        clearValidationErrors()
    }
    
    /**
     * Update fees agreement
     */
    fun updateAgreeToFees(value: Boolean) {
        agreeToFees = value
        clearValidationErrors()
    }
    
    /**
     * Update fee calculation
     */
    private fun updateFee() {
        val amountValue = amount.toDoubleOrNull() ?: 0.0
        val fees = withdrawalFees
        
        if (fees != null) {
            currentFee = withdrawalRepository.calculateFee(amountValue, selectedWithdrawalType, fees)
            netAmount = withdrawalRepository.calculateNetAmount(amountValue, currentFee)
        }
    }
    
    /**
     * Validate form
     */
    private fun validateForm(): Boolean {
        val errors = mutableListOf<String>()
        
        // Basic validation
        if (amount.isBlank()) {
            errors.add("Amount is required")
        } else {
            val amountValue = amount.toDoubleOrNull()
            if (amountValue == null || amountValue <= 0) {
                errors.add("Please enter a valid amount")
            }
        }
        
        if (reason.isBlank()) {
            errors.add("Reason is required")
        }
        
        // Bank details validation
        val bankDetails = prepareBankDetails()
        val validationResult = if (selectedWithdrawalType == WithdrawalType.LOCAL) {
            withdrawalRepository.validateLocalBankDetails(bankDetails)
        } else {
            withdrawalRepository.validateInternationalBankDetails(bankDetails)
        }
        
        if (!validationResult.isValid) {
            errors.addAll(validationResult.errors)
        }
        
        // Terms validation
        if (!agreeToTerms) {
            errors.add("You must agree to the terms and conditions")
        }
        
        if (!agreeToFees) {
            errors.add("You must agree to the withdrawal fees")
        }
        
        validationErrors = errors
        return errors.isEmpty()
    }
    
    /**
     * Create bank details from form data
     */
    private fun prepareBankDetails(): BankDetails {
        return BankDetails(
            accountNumber = accountNumber.trim(),
            accountHolderName = accountHolderName.trim(),
            bankName = bankName.trim(),
            branchCode = branchCode.trim().takeIf { it.isNotBlank() },
            branchName = branchName.trim().takeIf { it.isNotBlank() },
            swiftCode = swiftCode.trim().takeIf { it.isNotBlank() },
            iban = iban.trim().takeIf { it.isNotBlank() },
            bankAddress = bankAddress.trim().takeIf { it.isNotBlank() },
            country = selectedCountryCode, // Use selected country code
            passportNumber = passportNumber.trim().takeIf { it.isNotBlank() },
            passportCountry = passportCountry.trim().takeIf { it.isNotBlank() }
        )
    }
    
    /**
     * Submit withdrawal request
     */
    fun submitWithdrawal(onSuccess: () -> Unit = {}) {
        if (!validateForm()) {
            return
        }
        
        val bankDetails = prepareBankDetails()
        
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            
            try {
                val result = withdrawalRepository.requestWithdrawal(
                    amount = amount,
                    reason = reason.takeIf { it.isNotBlank() },
                    withdrawalType = selectedWithdrawalType,
                    bankDetails = bankDetails,
                    agreeToTerms = agreeToTerms,
                    agreeToFees = agreeToFees
                )
                
                if (result.isSuccess) {
                    val response = result.getOrNull()
                    successMessage = "Withdrawal request submitted successfully. Withdrawal number: ${response?.withdrawalNumber ?: "N/A"}"
                    onSuccess()
                } else {
                    val exception = result.exceptionOrNull()
                    val errorException = if (exception is Exception) exception else Exception(exception?.message, exception)
                    errorMessage = ErrorUtils.extractErrorMessage(errorException, "Failed to submit withdrawal")
                }
            } catch (e: Throwable) {
                val errorException = if (e is Exception) e else Exception(e.message, e)
                errorMessage = ErrorUtils.extractErrorMessage(errorException, "An unexpected error occurred")
            } finally {
                isLoading = false
            }
        }
    }
    
    /**
     * Clear validation errors
     */
    private fun clearValidationErrors() {
        validationErrors = emptyList()
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        errorMessage = null
    }
    
    /**
     * Clear success message
     */
    fun clearSuccess() {
        successMessage = null
    }
    
    /**
     * Reset form
     */
    fun resetForm() {
        amount = ""
        reason = ""
        selectedWithdrawalType = WithdrawalType.LOCAL
        accountNumber = ""
        accountHolderName = ""
        bankName = ""
        branchCode = ""
        branchName = ""
        swiftCode = ""
        iban = ""
        bankAddress = ""
        passportNumber = ""
        passportCountry = ""
        selectedSABank = null
        agreeToTerms = false
        agreeToFees = false
        validationErrors = emptyList()
        errorMessage = null
        successMessage = null
    }
} 