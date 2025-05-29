package com.lswmobile.app.network.repository

import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.JsonObject

/**
 * Repository for product-related operations
 */
class ProductRepository(private val api: LivestockWealthApi) {
    
    // StateFlow to observe products state
    private val _productsState = MutableStateFlow<ProductsState>(ProductsState.Idle)
    val productsState: Flow<ProductsState> = _productsState.asStateFlow()
    
    // StateFlow to observe cart state
    private val _cartState = MutableStateFlow<CartState>(CartState.Idle)
    val cartState: Flow<CartState> = _cartState.asStateFlow()
    
    /**
     * Get all products with optional category filter
     */
    suspend fun getProducts(category: String? = null, limit: Int = 20, offset: Int = 0): Result<ProductsResponse> {
        return try {
            _productsState.value = ProductsState.Loading
            val response = api.getProducts(category, limit, offset)
            _productsState.value = ProductsState.Success(response.products)
            Result.success(response)
        } catch (e: Exception) {
            _productsState.value = ProductsState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    /**
     * Get a single product by ID
     */
    suspend fun getProduct(productId: String): Result<ProductFarmland> {
        return try {
            val response = api.getProduct(productId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get current user's cart
     */
    suspend fun getCart(): Result<CartResponse> {
        return try {
            _cartState.value = CartState.Loading
            val response = api.getCart()
            _cartState.value = CartState.Success(response)
            Result.success(response)
        } catch (e: Exception) {
            _cartState.value = CartState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    /**
     * Add item to cart
     */
    suspend fun addToCart(productId: String, quantity: Int): Result<CartResponse> {
        return try {
            _cartState.value = CartState.Loading
            val addToCartBody = AddToCartBody(productId, quantity)
            val response = api.addToCart(addToCartBody)
            _cartState.value = CartState.Success(response)
            Result.success(response)
        } catch (e: Exception) {
            _cartState.value = CartState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    /**
     * Update item in cart
     */
    suspend fun updateCart(cartItemId: String, quantity: Int): Result<CartResponse> {
        return try {
            _cartState.value = CartState.Loading
            val updateCartBody = UpdateCartBody(cartItemId, quantity)
            val response = api.updateCart(updateCartBody)
            _cartState.value = CartState.Success(response)
            Result.success(response)
        } catch (e: Exception) {
            _cartState.value = CartState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    /**
     * Remove item from cart
     */
    suspend fun removeFromCart(cartItemId: String): Result<CartResponse> {
        return try {
            _cartState.value = CartState.Loading
            val response = api.removeFromCart(cartItemId)
            _cartState.value = CartState.Success(response)
            Result.success(response)
        } catch (e: Exception) {
            _cartState.value = CartState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    /**
     * Clear cart
     */
    suspend fun clearCart(): Result<JsonObject> {
        return try {
            _cartState.value = CartState.Loading
            val response = api.clearCart()
            _cartState.value = CartState.Idle // Reset state since cart is cleared
            Result.success(response)
        } catch (e: Exception) {
            _cartState.value = CartState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
}

/**
 * Products state sealed class
 */
sealed class ProductsState {
    object Idle : ProductsState()
    object Loading : ProductsState()
    data class Success(val products: List<ProductFarmland>) : ProductsState()
    data class Error(val message: String) : ProductsState()
}

/**
 * Cart state sealed class
 */
sealed class CartState {
    object Idle : CartState()
    object Loading : CartState()
    data class Success(val cart: CartResponse) : CartState()
    data class Error(val message: String) : CartState()
}
