package com.lswmobile.app.network.model

import kotlinx.serialization.Serializable

/**
 * EFT bank details response
 */
@Serializable
data class EftDetailsServerResponse(
    val success: Boolean,
    val data: EftDetailsType
)

/**
 * Bank details model
 */
@Serializable
data class EftDetailsType(
    val accountHolder: String,
    val accountNumber: String,
    val bank: String,
    val accountType: String,
    val branch: String,
    val bankAddress: String,
    val swiftCode: String,
    val branchCode: String,
//    var reference: String,
)

/**
 * Bank type
 */
@Serializable
data class BankType(
    val name: String,
    val code: String
) {
    override fun toString(): String = name
}

/**
 * Debit order request
 */
@Serializable
data class DebitOrderBody(
    val amount: Double,
    val bankAccountId: String,
    val orderId: String
)

/**
 * Payment body
 */
//@Serializable
//data class PaymentBody(
//    val amount: Double,
//    val orderId: String
//)

/**
 * Wallet overview
 */
@Serializable
data class WalletOverview(
    val success: Boolean,
    val balance: Double,
    val availableBalance: Double,
    val totalPriceOfAssetsInWaitingList: Double
)

/**
 * Statement
 */
@Serializable
data class Statement(
    val _id: String,
    val userId: String,
    val reference: String,
    val label: String,
    val dateOfTransaction: String,
    val amount: Double,
    val balance: Double,
    val transactionType: String,
    val paymentMethod: String,
    val paymentRef: String,
    val note: String,
    val user: UserResponse
)
