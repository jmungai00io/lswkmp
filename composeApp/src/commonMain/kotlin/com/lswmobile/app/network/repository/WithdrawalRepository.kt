package com.lswmobile.app.network.repository

import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.*
import com.lswmobile.app.network.model.BankType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for withdrawal-related operations
 */
class WithdrawalRepository(private val api: LivestockWealthApi) {
    
    // StateFlow to observe withdrawal state
    private val _withdrawalState = MutableStateFlow<WithdrawalState>(WithdrawalState.Idle)
    val withdrawalState: Flow<WithdrawalState> = _withdrawalState.asStateFlow()
    
    // StateFlow for local banks and fees
    private val _feesAndBanksState = MutableStateFlow<FeesAndBanksState>(FeesAndBanksState.Idle)
    val feesAndBanksState: Flow<FeesAndBanksState> = _feesAndBanksState.asStateFlow()
    
    /**
     * Get all withdrawals for the current user
     */
    suspend fun getWithdrawals(): Result<List<Withdrawal>> {
        return try {
            _withdrawalState.value = WithdrawalState.Loading
            val response = api.getWithdrawals()
            if (response.success) {
                _withdrawalState.value = WithdrawalState.Success(response.data)
                Result.success(response.data)
            } else {
                _withdrawalState.value = WithdrawalState.Error("Failed to fetch withdrawals")
                Result.failure(Exception("Failed to fetch withdrawals"))
            }
        } catch (e: Exception) {
            _withdrawalState.value = WithdrawalState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    /**
     * Get a specific withdrawal by ID
     */
    suspend fun getWithdrawal(withdrawalId: String): Result<Withdrawal> {
        return try {
            _withdrawalState.value = WithdrawalState.Loading
            val response = api.getWithdrawal(withdrawalId)
            if (response.success) {
                _withdrawalState.value = WithdrawalState.Success(listOf(response.data))
                Result.success(response.data)
            } else {
                _withdrawalState.value = WithdrawalState.Error("Failed to fetch withdrawal")
                Result.failure(Exception("Failed to fetch withdrawal"))
            }
        } catch (e: Exception) {
            _withdrawalState.value = WithdrawalState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    /**
     * Request a new withdrawal
     */
    suspend fun requestWithdrawal(
        amount: String,
        reason: String?,
        withdrawalType: WithdrawalType,
        bankDetails: BankDetails,
        agreeToTerms: Boolean,
        agreeToFees: Boolean
    ): Result<CreateWithdrawalResponse> {
        return try {
            _withdrawalState.value = WithdrawalState.Loading
            
            if (!agreeToTerms || !agreeToFees) {
                _withdrawalState.value = WithdrawalState.Error("You must agree to the terms and fees")
                return Result.failure(Exception("You must agree to the terms and fees"))
            }
            
            val withdrawalBody = WithdrawalBody(
                amount = amount,
                reason = reason,
                branchCode = bankDetails.branchCode,
                accountNumber = bankDetails.accountNumber,
                bank = bankDetails.bankName,
                country = bankDetails.country,
                passportNumber = bankDetails.passportNumber,
                passportCountry = bankDetails.passportCountry,
                bankBranchName = bankDetails.branchName,
                bankAddress = bankDetails.bankAddress,
                swiftCode = bankDetails.swiftCode,
                ibanNumber = bankDetails.iban
            )
            
            val response = api.requestWithdrawal(withdrawalBody)
            if (response.success) {
                _withdrawalState.value = WithdrawalState.Success(emptyList()) // No withdrawal data in create response
                Result.success(response)
            } else {
                _withdrawalState.value = WithdrawalState.Error("Failed to request withdrawal")
                Result.failure(Exception("Failed to request withdrawal"))
            }
        } catch (e: Exception) {
            _withdrawalState.value = WithdrawalState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    /**
     * Get withdrawal fees and local banks
     */
    suspend fun getWithdrawalFeesAndBanks(): Result<LocalBanksResponse> {
        return try {
            _feesAndBanksState.value = FeesAndBanksState.Loading

            val banksResponse = api.getLocalBanks()
            if (!banksResponse.success) {
                _feesAndBanksState.value = FeesAndBanksState.Error("Failed to fetch banks")
                return Result.failure(Exception("Failed to fetch banks"))
            }

            val fees = runCatching {
                api.getWithdrawalFees()
            }.fold(
                onSuccess = { response ->
                    if (response.success) response.data else DEFAULT_WITHDRAWAL_FEES
                },
                onFailure = { DEFAULT_WITHDRAWAL_FEES }
            )

            val localBanksResponse = LocalBanksResponse(
                success = true,
                banks = banksResponse.banks,
                withdrawalFees = fees
            )

            _feesAndBanksState.value = FeesAndBanksState.Success(localBanksResponse)
            Result.success(localBanksResponse)
        } catch (e: Exception) {
            _feesAndBanksState.value = FeesAndBanksState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    companion object {
        private val DEFAULT_WITHDRAWAL_FEES = WithdrawalFees(
            LOCAL = 49.0,
            ITL = 149.0
        )
    }
    
    /**
     * Calculate withdrawal fee based on type and amount
     */
    fun calculateFee(amount: Double, withdrawalType: WithdrawalType, fees: WithdrawalFees): Double {
        return when (withdrawalType) {
            WithdrawalType.LOCAL -> fees.LOCAL
            WithdrawalType.INTERNATIONAL -> fees.ITL
        }
    }
    
    /**
     * Calculate net amount after fees
     */
    fun calculateNetAmount(amount: Double, fee: Double): Double {
        return amount - fee
    }
    
    /**
     * Validate bank details for local withdrawal
     */
    fun validateLocalBankDetails(bankDetails: BankDetails): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (bankDetails.accountNumber.isBlank()) {
            errors.add("Account number is required")
        }
        
        if (bankDetails.accountHolderName.isBlank()) {
            errors.add("Account holder name is required")
        }
        
        if (bankDetails.bankName.isBlank()) {
            errors.add("Bank name is required")
        }
        
        if (bankDetails.branchCode.isNullOrBlank()) {
            errors.add("Branch code is required for local withdrawals")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    /**
     * Validate bank details for international withdrawal
     */
    fun validateInternationalBankDetails(bankDetails: BankDetails): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (bankDetails.accountNumber.isBlank()) {
            errors.add("Account number is required")
        }
        
        if (bankDetails.accountHolderName.isBlank()) {
            errors.add("Account holder name is required")
        }
        
        if (bankDetails.bankName.isBlank()) {
            errors.add("Bank name is required")
        }
        
        if (bankDetails.swiftCode.isNullOrBlank()) {
            errors.add("SWIFT code is required for international withdrawals")
        }
        
        if (bankDetails.iban.isNullOrBlank()) {
            errors.add("IBAN is required for international withdrawals")
        }
        
        if (bankDetails.bankAddress.isNullOrBlank()) {
            errors.add("Bank address is required for international withdrawals")
        }
        
        if (bankDetails.passportNumber.isNullOrBlank()) {
            errors.add("Passport number is required for international withdrawals")
        }
        
        if (bankDetails.passportCountry.isNullOrBlank()) {
            errors.add("Passport country is required for international withdrawals")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
}

/**
 * Withdrawal state sealed class
 */
sealed class WithdrawalState {
    object Idle : WithdrawalState()
    object Loading : WithdrawalState()
    data class Success(val withdrawals: List<Withdrawal>) : WithdrawalState()
    data class Error(val message: String) : WithdrawalState()
}

/**
 * Fees and banks state sealed class
 */
sealed class FeesAndBanksState {
    object Idle : FeesAndBanksState()
    object Loading : FeesAndBanksState()
    data class Success(val data: LocalBanksResponse) : FeesAndBanksState()
    data class Error(val message: String) : FeesAndBanksState()
}

/**
 * Validation result data class
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
) 