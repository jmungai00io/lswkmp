package com.lswmobile.app.data.model

import kotlinx.serialization.Serializable

/**
 * Enum for cart item types
 */
enum class CartItemType {
    PRODUCT, FARMLAND
}

/**
 * Unified cart item model for database storage
 * This is agnostic to whether it's a product or farmland
 */
@Serializable
data class CartItem(
    val id: String,
    val itemId: String, // The actual product/farmland ID from API
    val itemType: CartItemType,
    val quantity: Int,
    val price: Double,
    val name: String,
    val location: String?,
    val images: List<String>?,
    val profitRate: String?,
    val profitCycle: String?,
    val unitCount: String?,
    val productType: String?,
    val investmentTerm: String?,
    val dividendCycle: String?,
    val profitInformation: String?,
    val count: String?,
    val inStock: Boolean?,
    val isArchived: Boolean?,
    val infoUrl: String?,
    val userId: String,
    val createdAt: Long
)

/**
 * Cart summary for displaying totals
 */
@Serializable
data class CartSummary(
    val itemCount: Int,
    val totalQuantity: Int,
    val totalPrice: Double
)

/**
 * Cart summary by type (for separate product/farmland displays)
 */
@Serializable
data class CartSummaryByType(
    val itemCount: Int,
    val totalQuantity: Int,
    val totalPrice: Double,
    val itemType: CartItemType
)

/**
 * Display model for cart items in the UI
 * This can be used to show cart items with additional context
 */
@Serializable
data class CartItemDisplay(
    val cartItem: CartItem,
    val subtotal: Double = cartItem.price * cartItem.quantity,
    val isProduct: Boolean = cartItem.itemType == CartItemType.PRODUCT,
    val isFarmland: Boolean = cartItem.itemType == CartItemType.FARMLAND
)

/**
 * Cart state for UI
 */
sealed class CartState {
    object Idle : CartState()
    object Loading : CartState()
    data class Success(
        val items: List<CartItem>,
        val summary: CartSummary
    ) : CartState()
    data class Error(val message: String) : CartState()
}

/**
 * Cart operation result
 */
sealed class CartOperationResult {
    object Success : CartOperationResult()
    data class Error(val message: String) : CartOperationResult()
} 