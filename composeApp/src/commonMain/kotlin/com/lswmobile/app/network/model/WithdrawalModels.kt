package com.lswmobile.app.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Withdrawal model
 */
@Serializable
data class Withdrawal(
//    @SerialName("_id")
    val _id: String,
    val accountName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val amount: String,
    val branchCode: String? = null,
    val reason: String? = null,
    val accountNumber: String? = null,
    val bank: String? = null,
    val country: String? = null,
    val _legacyId: String? = null,
    val status: String? = null,
    val withdrawalNumber: String? = null,
    val dateProcessed: String? = null,
    val dateCreated: String = "",
    val isArchived: Boolean = false,
    val statementId: String? = null,
    val withdrawalFee: Double=0.0,

    )

/**
 * Bank details for withdrawals
 */
@Serializable
data class BankDetails(
    val accountNumber: String,
    val accountHolderName: String,
    val bankName: String,
    val branchCode: String? = null,
    val branchName: String? = null,
    val swiftCode: String? = null,
    val iban: String? = null,
    val bankAddress: String? = null,
    val country: String = "ZA",
    val passportNumber: String? = null,
    val passportCountry: String? = null
)



/**
 * Local banks response
 */
@Serializable
data class LocalBanksResponse(
    val success: Boolean,
    val banks: List<BankType>,
    val withdrawalFees: WithdrawalFees
)

/**
 * Enhanced withdrawal request body
 */
@Serializable
data class WithdrawalBody(
    val amount: String? = null,
    val branchCode: String? = null,
    val reason: String? = null,
    val accountNumber: String? = null,
    val bank: String? = null,
    val country: String? = null,
    val passportNumber: String? = null,
    val passportCountry: String? = null,
    val bankBranchName: String? = null,
    val bankAddress: String? = null,
    val swiftCode: String? = null,
    val ibanNumber: String? = null,
)

/**
 * Withdrawal response
 */
@Serializable
data class GetWithdrawalResponse(
    val success: Boolean,
    val data: Withdrawal
)

/**
 * Create withdrawal response
 */
@Serializable
data class CreateWithdrawalResponse(
    val success: Boolean,
    @SerialName("_id")
    val id: String,
    val withdrawalNumber: String
)

/**
 * My withdrawals response
 */
@Serializable
data class MyWithdrawalsResponse(
    val success: Boolean,
    val data: List<Withdrawal>
)

/**
 * Withdrawal fees
 */
@Serializable
data class WithdrawalFees(
    val LOCAL: Double,
    val ITL: Double
)

@Serializable
data class WithdrawalFeesResponse(
    val success: Boolean,
    val data: WithdrawalFees
)

/**
 * Withdrawal status enum
 */
enum class WithdrawalStatus(val displayName: String) {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled")
}

/**
 * Withdrawal type enum
 */
enum class WithdrawalType(val displayName: String, val requiresInternationalFields: Boolean) {
    LOCAL("Local (South Africa)", false),
    INTERNATIONAL("International", true)
}
