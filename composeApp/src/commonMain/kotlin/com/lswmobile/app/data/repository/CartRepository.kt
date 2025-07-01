package com.lswmobile.app.data.repository

import com.lswmobile.app.data.model.CartItem
import com.lswmobile.app.data.model.CartItemType
import com.lswmobile.app.data.model.CartOperationResult
import com.lswmobile.app.data.model.CartSummary
import com.lswmobile.app.network.model.Farmland
import com.lswmobile.app.network.model.ProductClassic
import kotlinx.coroutines.flow.Flow

/**
 * Interface for cart repository operations
 * This allows for different implementations (SQLDelight, in-memory, etc.)
 */
interface CartRepository {
    /**
     * Get cart items by type (PRODUCT or FARMLAND)
     */
    fun getCartItemsByType(type: CartItemType): Flow<List<CartItem>>
    
    /**
     * Get cart summary (total items, unique items, total amount)
     */
    val cartSummary: Flow<CartSummary>
    
    /**
     * Add a product to cart
     */
    suspend fun addProductToCart(product: ProductClassic, quantity: Int = 1): CartOperationResult
    
    /**
     * Add a farmland to cart
     */
    suspend fun addFarmlandToCart(farmland: Farmland, quantity: Int = 1): CartOperationResult
    
    /**
     * Update cart item quantity
     */
    suspend fun updateCartItemQuantity(itemId: String, quantity: Int): CartOperationResult
    
    /**
     * Remove item from cart
     */
    suspend fun removeFromCart(itemId: String): CartOperationResult
    
    /**
     * Clear all cart items
     */
    suspend fun clearCart(): CartOperationResult
} 