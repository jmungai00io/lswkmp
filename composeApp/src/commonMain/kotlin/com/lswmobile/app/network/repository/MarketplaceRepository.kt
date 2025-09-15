package com.lswmobile.app.network.repository

import com.lswmobile.app.data.model.CartItem
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    // Add preorder state
    private val _preorderState = MutableStateFlow<PreorderState>(PreorderState.Idle)
    val preorderState: Flow<PreorderState> = _preorderState.asStateFlow()

    // State flow for order creation
    private val _orderState = MutableStateFlow<OrderState>(OrderState.Idle)
    val orderState: Flow<OrderState> = _orderState.asStateFlow()

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
     * Preorder a product that is out of stock
     */
    suspend fun preorderProduct(productType: String) {
        try {
            _preorderState.value = PreorderState.Loading
            val response = api.preorderProduct(productType)
            
            if (response.success) {
                val message = response.message ?: "Successfully added to waitlist"
                _preorderState.value = PreorderState.Success(
                    message = message,
                    waitingListPosition = response.waitingListPosition
                )
            } else {
                val errorMessage = response.error ?: "Unknown error occurred during preorder"
                _preorderState.value = PreorderState.Error(errorMessage)
            }
        } catch (e: Exception) {
            _preorderState.value = PreorderState.Error(e.message ?: "Unknown error preordering product")
        }
    }
    
    /**
     * Reset preorder state to idle
     * Call this after handling a preorder result in the UI
     */
    fun resetPreorderState() {
        _preorderState.value = PreorderState.Idle
    }

    /**
     * Create order for products
     */
    suspend fun createProductOrder(cartItems: List<CartItem>, totalAmount: Double) {
        _orderState.value = OrderState.Loading
        
        // Calculate the total for just these product items
        val productSubtotal = cartItems.sumOf { it.price * it.quantity }
        
        try {
            // Map cart items to order items
            val orderItems = cartItems.map { cartItem ->
                MarketplaceOrderItem(
                    productType = cartItem.productType ?: "",
                    productId = cartItem.itemId,
                    quantity = cartItem.quantity
                )
            }
            

            // Call API to create order with product subtotal
            val response = api.createMarketplaceOrder(orderItems, productSubtotal)

            if (response.success) {
                _orderState.value = OrderState.Success(
                    orderId = response.orderId ?: "",
                    message = response.message ?: "Order created successfully"
                )
            } else {
                _orderState.value = OrderState.Error(
                    message = response.error ?: "Failed to create order"
                )
            }
        } catch (e: Exception) {
            _orderState.value = OrderState.Error(
                message = e.message ?: "Unknown error occurred while creating order"
            )
        }
    }
    
    /**
     * Create order for farmlands
     */
    suspend fun createFarmlandOrder(cartItems: List<CartItem>, totalAmount: Double) {
        _orderState.value = OrderState.Loading
        
        // Calculate the total for just these farmland items
        val farmlandSubtotal = cartItems.sumOf { it.price * it.quantity }
        try {
            // Map cart items to order items
            val orderItems = cartItems.map { cartItem ->
                MarketplaceOrderItem(
                    productType = "farmLand", // Farmlands always have this type
                    productId = cartItem.itemId,
                    quantity = cartItem.quantity
                )
            }
            
            val response = api.createMarketplaceOrder(orderItems, farmlandSubtotal)

            if (response.success) {
                _orderState.value = OrderState.Success(
                    orderId = response.orderId ?: "",
                    message = response.message ?: "Order created successfully"
                )
            } else {
                _orderState.value = OrderState.Error(
                    message = response.error ?: "Failed to create order"
                )
            }
        } catch (e: Exception) {
            _orderState.value = OrderState.Error(
                message = e.message ?: "Unknown error occurred while creating order"
            )
        }
    }
    
    /**
     * Reset order state
     */
    fun resetOrderState() {
        _orderState.value = OrderState.Idle
    }
}

/**
 * Products state sealed class
 */
sealed class ProductsState {
    object Idle : ProductsState()
    object Loading : ProductsState()
    data class Success(val products: List<ProductClassic>) : ProductsState()
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

/**
 * Preorder state sealed class
 */
sealed class PreorderState {
    object Idle : PreorderState()
    object Loading : PreorderState()
    data class Success(val message: String, val waitingListPosition: Int? = null) : PreorderState()
    data class Error(val message: String) : PreorderState()
}

/**
 * Order state sealed class
 */
sealed class OrderState {
    object Idle : OrderState()
    object Loading : OrderState()
    data class Success(val orderId: String, val message: String?) : OrderState()
    data class Error(val message: String) : OrderState()
}
