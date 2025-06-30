package com.lswmobile.app.network.repository

import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.JsonObject

/**
 * Repository for marketplace operations
 * Handles both ProductFarmland and Farmland models with their respective API calls
 */
class MarketplaceRepository(private val api: LivestockWealthApi) {
    
    // StateFlow to observe products state
    private val _productsState = MutableStateFlow<ProductsState>(ProductsState.Idle)
    val productsState: Flow<ProductsState> = _productsState.asStateFlow()
    
    // StateFlow to observe farmlands state
    private val _farmlandsState = MutableStateFlow<FarmlandsState>(FarmlandsState.Idle)
    val farmlandsState: Flow<FarmlandsState> = _farmlandsState.asStateFlow()
    
    // StateFlow to observe cart state for products
    private val _productCartState = MutableStateFlow<CartState>(CartState.Idle)
    val productCartState: Flow<CartState> = _productCartState.asStateFlow()
    
    // StateFlow to observe cart state for farmlands
    private val _farmlandCartState = MutableStateFlow<CartState>(CartState.Idle)
    val farmlandCartState: Flow<CartState> = _farmlandCartState.asStateFlow()
    
    // StateFlow for product cart items
    private val _productCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val productCartItems: Flow<List<CartItem>> = _productCartItems.asStateFlow()

    // StateFlow for farmland cart items
    private val _farmlandCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val farmlandCartItems: Flow<List<CartItem>> = _farmlandCartItems.asStateFlow()

    /**
     * Get all products
     */
    suspend fun getProducts(limit: Int = 20, offset: Int = 0) {
        try {
            _productsState.value = ProductsState.Loading
            val response = api.getProducts(null, limit, offset)
            _productsState.value = ProductsState.Success(response.products)
        } catch (e: Exception) {
            _productsState.value = ProductsState.Error(e.message ?: "Unknown error fetching products")
        }
    }

    /**
     * Get all farmlands
     * Note: This method needs to be implemented in the LivestockWealthApi
     */
    suspend fun getFarmlands() {
        try {
            _farmlandsState.value = FarmlandsState.Loading

            // We need to add this method to the LivestockWealthApi class
            val farmlands = api.getFarmlands()
            _farmlandsState.value = FarmlandsState.Success(farmlands.data)
        } catch (e: Exception) {
            _farmlandsState.value = FarmlandsState.Error(e.message ?: "Unknown error fetching farmlands")
        }
    }

    /**
     * Add product to cart
     */
    suspend fun addProductToCart(productId: String, quantity: Int) {
        try {
            _productCartState.value = CartState.Loading
            val addToCartBody = AddToCartBody(productId, quantity)
            val response = api.addToCart(addToCartBody)
            _productCartState.value = CartState.Success(response)

            // Update cart items
            _productCartItems.value = response.items
        } catch (e: Exception) {
            _productCartState.value = CartState.Error(e.message ?: "Unknown error adding product to cart")
        }
    }

    /**
     * Add farmland to cart
     * Note: This method needs to be implemented in the LivestockWealthApi
     */
    suspend fun addFarmlandToCart(farmlandId: String, quantity: Int) {
        try {
            _farmlandCartState.value = CartState.Loading

            // We need to add this method to the LivestockWealthApi class
            // For now, we'll use a similar pattern as products
            val addToCartBody = AddToCartBody(farmlandId, quantity)
            val response = api.addToCart(addToCartBody)
            _farmlandCartState.value = CartState.Success(response)

            // Update cart items
            _farmlandCartItems.value = response.items
        } catch (e: Exception) {
            _farmlandCartState.value = CartState.Error(e.message ?: "Unknown error adding farmland to cart")
        }
    }

    /**
     * Get product cart
     */
    suspend fun getProductCart() {
        try {
            _productCartState.value = CartState.Loading
            val response = api.getCart()
            _productCartState.value = CartState.Success(response)

            // Update cart items
            _productCartItems.value = response.items
        } catch (e: Exception) {
            _productCartState.value = CartState.Error(e.message ?: "Unknown error fetching product cart")
        }
    }

    /**
     * Get farmland cart
     * Note: This method needs to be implemented in the LivestockWealthApi
     */
    suspend fun getFarmlandCart() {
        try {
            _farmlandCartState.value = CartState.Loading

            // We need to add this method to the LivestockWealthApi class
            // For now, we'll use the same cart
            val response = api.getCart()
            _farmlandCartState.value = CartState.Success(response)

            // Update cart items
            _farmlandCartItems.value = response.items
        } catch (e: Exception) {
            _farmlandCartState.value = CartState.Error(e.message ?: "Unknown error fetching farmland cart")
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
 * Farmlands state sealed class
 */
sealed class FarmlandsState {
    object Idle : FarmlandsState()
    object Loading : FarmlandsState()
    data class Success(val farmlands: List<Farmland>) : FarmlandsState()
    data class Error(val message: String) : FarmlandsState()
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
