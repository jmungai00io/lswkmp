package com.lswmobile.app.network.model

import com.lswmobile.app.data.model.CartItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Product response models
 */
@Serializable
data class ProductsResponse(
    val success: Boolean,
    @SerialName("data")
    val products: List<ProductClassic> = emptyList(),
    val count: Int = 0
)

/**
 * Product model
 */
@Serializable
data class ProductClassic(
    @SerialName("_id")
    val _id: String,
    val id: String? = null,
    val price: Double,
    val name: String? = null,
    val productName: String? = null,
    val location: String? = null,
    val images: List<String>? = null,
    val imageUrl: String? = null,
    val profitRate: Double? = null,
    val profitCycle: Int? = null,
    val isArchived: Boolean? = null,
    val infoUrl: String? = null,
    val __v: Int? = null,
    val unitCount: Int? = null,
    val productType: String? = null,
    val investmentTerm: Int? = null,
    val dividendCycle: Int? = null,
    val profitInformation: String? = null,
    val stockCount: Int? = null,
    val waitingListCount: Int? = null,
    val count: Int? = null,
    val inStock: Boolean? = null
)

/**
 * Farmland model
 */
@Serializable
data class Farmland(
    @SerialName("_id")
    val _id: String,
    val name: String,
    val price: Double,
    val location: String? = null,
    val images: List<String>? = null,
    val profitRate: Int? = null,
    val profitCycle: Int? = null,
    val isArchived: Boolean? = false,
    val infoUrl: String? = null,
    val annualDateOfEvaluation: String? = null,
    val __v: Int? = null,
    val unitCount: Int? = null,
    val productType: String? = null,
    val inStock: Boolean? = true
)

/**
 * Response for farmlands
 */
@Serializable
data class FarmlandsResponse(
    val success: Boolean,
    val data: List<Farmland>
)

/**
 * Cart models
 */
@Serializable
data class CartResponse(
    val items: List<CartItem>,
    val totalAmount: Double,
    val message: String
)



/**
 * Add to cart request
 */
@Serializable
data class AddToCartBody(
    val productId: String,
    val quantity: Int
)

/**
 * Update cart request
 */
@Serializable
data class UpdateCartBody(
    val cartItemId: String,
    val quantity: Int
)

/**
 * Preorder request body
 */
@Serializable
data class PreorderRequest(
    val productType: String
)

/**
 * Preorder response model
 */
@Serializable
data class PreorderResponse(
    val success: Boolean,
    val message: String? = null,
    val waitingListPosition: Int? = null,
    val error: String? = null
)

/**
 * Error response
 */
@Serializable
data class ErrorResponse(
    val message: String
)

/**
 * Marketplace order item model for API request
 */
@Serializable
data class MarketplaceOrderItem(
    val productType: String,  // "farmLand" or "macadamia" or other product types
    val productId: String,
    val quantity: Int
)

/**
 * Create marketplace order request body
 */
@Serializable
data class CreateMarketplaceOrderRequest(
    val items: List<MarketplaceOrderItem>,
    val amount: Double
)

/**
 * Marketplace order response model
 */
@Serializable
data class MarketplaceOrderResponse(
    val success: Boolean,
    val message: String? = null,
    val orderId: String? = null,
    val error: String? = null
)
