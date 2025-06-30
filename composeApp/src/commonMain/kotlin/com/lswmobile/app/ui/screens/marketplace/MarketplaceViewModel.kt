package com.lswmobile.app.ui.screens.marketplace

import com.lswmobile.app.network.model.CartItem
import com.lswmobile.app.network.model.Farmland
import com.lswmobile.app.network.model.ProductFarmland
import com.lswmobile.app.network.repository.FarmlandsState
import com.lswmobile.app.network.repository.MarketplaceRepository
import com.lswmobile.app.network.repository.ProductsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel for the MarketplaceScreen
 */
class MarketplaceViewModel(
    private val repository: MarketplaceRepository,
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
    
    // Cart items
    private val _productCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val productCartItems: StateFlow<List<CartItem>> = _productCartItems.asStateFlow()
    
    private val _farmlandCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val farmlandCartItems: StateFlow<List<CartItem>> = _farmlandCartItems.asStateFlow()
    
    // Cached data
    private val _products = MutableStateFlow<List<ProductFarmland>>(emptyList())
    val products: StateFlow<List<ProductFarmland>> = _products.asStateFlow()
    
    private val _farmlands = MutableStateFlow<List<Farmland>>(emptyList())
    val farmlands: StateFlow<List<Farmland>> = _farmlands.asStateFlow()
    
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
        
        viewModelScope.launch {
            repository.productCartItems.collect { items ->
                _productCartItems.value = items
            }
        }
        
        viewModelScope.launch {
            repository.farmlandCartItems.collect { items ->
                _farmlandCartItems.value = items
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
            repository.addProductToCart(productId, quantity)
        }
    }
    
    /**
     * Add farmland to cart
     */
    fun addFarmlandToCart(farmlandId: String, quantity: Int) {
        println("Adding farmland to cart: $farmlandId")
        viewModelScope.launch {
            repository.addFarmlandToCart(farmlandId, quantity)
        }
    }
    
    /**
     * Get total number of items in both carts
     */
    val totalCartItems: StateFlow<Int> = MutableStateFlow(0).also { result ->
        viewModelScope.launch {
            launch {
                productCartItems.collect { items ->
                    result.value = items.size + (farmlandCartItems.value.size)
                }
            }
            launch {
                farmlandCartItems.collect { items ->
                    result.value = items.size + (productCartItems.value.size)
                }
            }
        }
    }.asStateFlow()
}
