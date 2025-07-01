package com.lswmobile.app.ui.screens.marketplace

import com.lswmobile.app.data.model.CartItem
import com.lswmobile.app.data.model.CartItemType
import com.lswmobile.app.data.model.CartSummary
import com.lswmobile.app.data.repository.CartRepository
import com.lswmobile.app.network.model.Farmland
import com.lswmobile.app.network.model.ProductClassic
import com.lswmobile.app.network.repository.FarmlandsState
import com.lswmobile.app.network.repository.MarketplaceRepository
import com.lswmobile.app.network.repository.ProductsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel for the MarketplaceScreen
 */
class MarketplaceViewModel(
    private val repository: MarketplaceRepository,
    private val cartRepository: CartRepository? = null,
    private val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    
    // Products state
    private val _productsState = MutableStateFlow<ProductsState>(ProductsState.Idle)
    val productsState: StateFlow<ProductsState> = _productsState.asStateFlow()
    
    // Farmlands state
    private val _farmlandsState = MutableStateFlow<FarmlandsState>(FarmlandsState.Idle)
    val farmlandsState: StateFlow<FarmlandsState> = _farmlandsState.asStateFlow()
    
    // Loading states
    private val _isLoadingProducts = MutableStateFlow(false)
    val isLoadingProducts: StateFlow<Boolean> = _isLoadingProducts.asStateFlow()
    
    private val _isLoadingFarmlands = MutableStateFlow(false)
    val isLoadingFarmlands: StateFlow<Boolean> = _isLoadingFarmlands.asStateFlow()
    
    // Error states
    private val _productError = MutableStateFlow<String?>(null)
    val productError: StateFlow<String?> = _productError.asStateFlow()
    
    private val _farmlandError = MutableStateFlow<String?>(null)
    val farmlandError: StateFlow<String?> = _farmlandError.asStateFlow()
    
    // Cart items from local repository
    private val _productCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val productCartItems: StateFlow<List<CartItem>> = _productCartItems.asStateFlow()
    
    private val _farmlandCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val farmlandCartItems: StateFlow<List<CartItem>> = _farmlandCartItems.asStateFlow()
    
    // Cart summary
    private val _cartSummary = MutableStateFlow(CartSummary(0, 0, 0.0))
    val cartSummary: StateFlow<CartSummary> = _cartSummary.asStateFlow()
    
    // Cached data
    private val _products = MutableStateFlow<List<ProductClassic>>(emptyList())
    val products: StateFlow<List<ProductClassic>> = _products.asStateFlow()
    
    private val _farmlands = MutableStateFlow<List<Farmland>>(emptyList())
    val farmlands: StateFlow<List<Farmland>> = _farmlands.asStateFlow()
    
    // Total cart items
    private val _totalCartItems = MutableStateFlow(0)
    val totalCartItems: StateFlow<Int> = _totalCartItems.asStateFlow()
    
    init {
        // Observe repository states
        viewModelScope.launch {
            repository.productsState.collect { state ->
                _productsState.value = state
                
                when (state) {
                    is ProductsState.Loading -> {
                        _isLoadingProducts.value = true
                        _productError.value = null
                    }
                    is ProductsState.Success -> {
                        _isLoadingProducts.value = false
                        _products.value = state.products
                        _productError.value = null
                    }
                    is ProductsState.Error -> {
                        _isLoadingProducts.value = false
                        _productError.value = state.message
                    }
                    else -> {}
                }
            }
        }
        
        viewModelScope.launch {
            repository.farmlandsState.collect { state ->
                _farmlandsState.value = state
                
                when (state) {
                    is FarmlandsState.Loading -> {
                        _isLoadingFarmlands.value = true
                        _farmlandError.value = null
                    }
                    is FarmlandsState.Success -> {
                        _isLoadingFarmlands.value = false
                        _farmlands.value = state.farmlands
                        _farmlandError.value = null
                    }
                    is FarmlandsState.Error -> {
                        _isLoadingFarmlands.value = false
                        _farmlandError.value = state.message
                    }
                    else -> {}
                }
            }
        }
        
        // Observe local cart repository
        viewModelScope.launch {
            cartRepository?.getCartItemsByType(CartItemType.PRODUCT)?.collect { items ->
                _productCartItems.value = items
            }
        }
        
        viewModelScope.launch {
            cartRepository?.getCartItemsByType(CartItemType.FARMLAND)?.collect { items ->
                _farmlandCartItems.value = items
            }
        }
        
        viewModelScope.launch {
            cartRepository?.cartSummary?.collect { summary ->
                _cartSummary.value = summary
                _totalCartItems.value = summary.itemCount
            }
        }
    }
    
    /**
     * Load all products and farmlands data
     */
    fun loadData() {
        loadProducts()
        loadFarmlands()
    }
    
    /**
     * Load products data
     */
    fun loadProducts() {
        viewModelScope.launch {
            repository.getProducts()
        }
    }
    
    /**
     * Load farmlands data
     */
    fun loadFarmlands() {
        viewModelScope.launch {
            repository.getFarmlands()
        }
    }
    
    /**
     * Add product to cart
     */
    fun addProductToCart(productId: String, quantity: Int) {
        viewModelScope.launch {
            val product = _products.value.find { it._id == productId }
            product?.let {
                cartRepository?.addProductToCart(it, quantity)
            }
        }
    }
    
    /**
     * Add farmland to cart
     */
    fun addFarmlandToCart(farmlandId: String, quantity: Int) {
        println("Adding farmland to cart: $farmlandId")
        viewModelScope.launch {
            val farmland = _farmlands.value.find { it._id == farmlandId }
            farmland?.let {
                cartRepository?.addFarmlandToCart(it, quantity)
            }
        }
    }
    
    /**
     * Update cart item quantity
     */
    fun updateCartItemQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            cartRepository?.updateCartItemQuantity(itemId, quantity)
        }
    }
    
    /**
     * Remove item from cart
     */
    fun removeFromCart(itemId: String) {
        viewModelScope.launch {
            cartRepository?.removeFromCart(itemId)
        }
    }
    
    /**
     * Clear cart
     */
    fun clearCart() {
        viewModelScope.launch {
            cartRepository?.clearCart()
        }
    }
}
