package com.lswmobile.app.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Create order request
 */
@Serializable
data class CreateOrderBody(
    val productId: String,
    val amount: Double,
    val paymentMethod: String
)

/**
 * Create order response
 */
@Serializable
data class CreateOrderResponse(
    val success: Boolean,
    val orderId: String
)

/**
 * Order item data
 */
@Serializable
data class OrderItem(
    @SerialName("_id")
    val _id: String = "",
    val productType: String = "",
    val farmLand: Farmland? = null,
    val asset: String? = null,
    val order: String? = null,
    val user: String? = null,
    val isUnallocated: Boolean = false,
    val isInvestmentMature: Boolean = false,
    val dateOfAllocation: String? = null,
    val priceOfAsset: Double = 0.0,
    val orderPaymentStatus: String? = null,
    val orderArchiveStatus: Boolean = false,
    val dividendPayoutDates: List<String> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val totalNumberOfPayments: Int = 0,
    val paymentsCount: Int = 0,
    @SerialName("assetValue")
    val assetValue: AssetValue? = null
)

@Serializable
data class AssetValue(
    val current: Double? = null,
    val lastUpdated: String? = null
)

/**
 * Order with user info
 */
@Serializable
data class OrderWithUserId (
    val user: String?,
    val orderNumber: Int?,
    val reference: String?,
    val amount: Double?,
    val status: String?,
    val paymentType: String?,
    val isArchived: Boolean,
    val isAutoReinvest: Boolean,
    val dateCreated: String?,
    val _id: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val selectedPaymentMethod: String?= ""
)

/**
 * Order with full user response
 */
@Serializable
data class OrderWithFullUser(
    val user: UserResponse?,
    val orderNumber: Int?,
    val reference: String?,
    val items: List<OrderItem> = emptyList(),
    val amount: Double,
    val status: String?,
    val paymentType: String?,
    val isArchived: Boolean,
    val isAutoReinvest: Boolean,
    val dateCreated: String?,
    val _id: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val selectedPaymentMethod: String? = ""
)

/**
 * My orders response
 */
@Serializable
data class MyOrdersResponse(
    val success: Boolean,
    val data: List<OrderWithUserId>
)

/**
 * Order details response
 */
@Serializable
data class OrderWithFullUserResponse(
    val success: Boolean,
    val data: OrderWithFullUser
)

/**
 * Orders response
 */
@Serializable
data class OrdersResponse(
    val success: Boolean,
    val data: List<OrderItem>
)

/**
 * Update draft order body
 */
@Serializable
data class UpdateDraftOrderBody(
    val action: String,
    val orderNumber: String
)

/**
 * Order response
 */
@Serializable
data class OrderResponse(
    val success: Boolean,
    val data: OrderItem
)

/**
 * Product model
 */
@Serializable
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val status: String,
    val type: String,
    val farmId: String? = null,
    val imageUrl: String? = null,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Asset model
 */
@Serializable
data class Asset(
    val id: String,
    val name: String,
    val type: String,
    val status: String,
    val orderId: String,
    val orderNumber: Int,
    val purchaseDate: String,
    val imageUrl: String? = null
)

/**
 * Notify available body
 */
@Serializable
data class NotifyAvailableBody(
    val productId: String,
    val email: String
)

/**
 * Payment completion types
 */
object PaymentCompletionType {
    const val TOPUP_ONLY = "PureTopUp"
    const val PARTIAL_TOPUP = "PartialOrderPay"
    const val FULL_PAYMENT = "CompleteOrderPay"
}

/**
 * Payment body
 */
@Serializable
data class PaymentBody(
    val paymentMethod: String,
    val paymentType: String
)

/**
 * Bank type model
 */
@Serializable
data class BankType(
    val name: String,
    val code: String
)
/**
 * Debit order body for API requests
 */
@Serializable
data class DebitOrderBody(
    val accountName: String,
    val accountNumber: String,
    val debitOrderDate: String,
    val orderNumber: String,
    val amount: String,
    val branchCode: String,
    val address: String,
    val phoneNumber: String,
    val signature: String,
    val accountType: String,
    val paymentType: String
)

/**
 * Banks response
 */
@Serializable
data class BanksResponse(
    val success: Boolean,
    val banks: List<BankType>
)
