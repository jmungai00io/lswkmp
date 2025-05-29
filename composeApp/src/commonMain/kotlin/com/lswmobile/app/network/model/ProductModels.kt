package com.lswmobile.app.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Product response models
 */
@Serializable
data class ProductsResponse(
    val success: Boolean,
    val products: List<ProductFarmland>,
    val count: Int
)

/**
 * Product model
 */
@Serializable
data class ProductFarmland(
    @SerialName("_id") 
    val _id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Farmland model
 */
@Serializable
data class Farmaland(
    @SerialName("_id") 
    val _id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String
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
 * Cart item model
 */
@Serializable
data class CartItem(
    val id: String,
    val productId: String,
    val quantity: Int,
    val price: Double,
    val product: ProductFarmland
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
 * Error response
 */
@Serializable
data class ErrorResponse(
    val message: String
)
