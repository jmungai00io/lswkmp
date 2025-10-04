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
//@Serializable
//data class BankType(
//    val name: String,
//    val code: String
//) {
//    override fun toString(): String = name
//}

/**
 * Debit order request
 */
//@Serializable
//class DebitOrderBody (
//    var accountName: String,
//    var accountNumber: String,
//    var debitOrderDate: String,
//    var orderNumber: String,
//    var amount: String,
//    var branchCode: String,
//    var address: String,
//    var phoneNumber: String,
//    var signature: String,
//    var accountType: String,
//    var paymentType: String,
//    )

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
    val reference: String,
    val label: String? = null,
    val transactionType: String,
    val note: String? = null,
    val paymentRef: String? = null,
    val paymentMethod: String? = null,
    val amount: Double,
    val dateOfTransaction: String? = null,
    val balance: Double,
    val user: String? = null
)

@Serializable
data class StatementsResponse(
    val success: Boolean,
    val data: List<Statement> = emptyList(),
    val message: String? = null
)

/**
 * Dividend payout dates
 */
@Serializable
data class DividendPayoutDates(
    val _id: String?,
    val projectedPaymentDate: String?,
    val invoiceDate: String?
)

/**
 * My asset
 */
@Serializable
data class MyAsset(
    val productType: String,
    val dateOfAllocation: String?,
    val dividendAmount: Double? = null,
    val dividendPayoutDates: List<DividendPayoutDates>?,
    val valueToday: Double? = null,
    val priceOfAsset: Double,
    val dateOfDividendEvaluation: String? = null
)

/**
 * Get assets response
 */
@Serializable
data class GetAssetsResponse(
    val success: Boolean?,
    val data: List<MyAsset>?,
    val totalPages: Int?,
    val currentPage: Int?,
    val totalCount: Int?
)
