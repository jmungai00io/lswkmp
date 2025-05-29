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
 * Order item
 */
@Serializable
data class OrderItem(
    @SerialName("_id")
    val _id: String,
    val userId: String,
    val productId: String,
    val amount: Double,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Order with user info
 */
@Serializable
data class OrderWithUserId(
    @SerialName("_id")
    val _id: String,
    val userId: String,
    val productId: String,
    val amount: Double,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val user: UserResponse
)

/**
 * Order with full user response
 */
@Serializable
data class OrderWithFullUserResponse(
    val success: Boolean,
    val data: OrderWithUserId
)

/**
 * My orders response
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
 * Get assets response
 */
@Serializable
data class GetAssetsResponse(
    val assets: List<Asset>,
    val message: String
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
 * Payment body
 */
@Serializable
data class PaymentBody(
    val paymentMethod: String
)
