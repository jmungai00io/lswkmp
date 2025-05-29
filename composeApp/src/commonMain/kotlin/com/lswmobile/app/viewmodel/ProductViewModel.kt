package com.lswmobile.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lswmobile.app.network.model.CartResponse
import com.lswmobile.app.network.model.ProductFarmland
import com.lswmobile.app.network.repository.CartState
import com.lswmobile.app.network.repository.ProductRepository
import com.lswmobile.app.network.repository.ProductsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for product and cart operations
 */
class ProductViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    // StateFlows from the repository
    val productsState: Flow<ProductsState> = productRepository.productsState
    val cartState: Flow<CartState> = productRepository.cartState
    
    // Internal state for UI
    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Idle)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()
    
    // Selected product
    private val _selectedProduct = MutableStateFlow<ProductFarmland?>(null)
    val selectedProduct: StateFlow<ProductFarmland?> = _selectedProduct.asStateFlow()
    
    // Cart data
    private val _cart = MutableStateFlow<CartResponse?>(null)
    val cart: StateFlow<CartResponse?> = _cart.asStateFlow()
    
    /**
     * Load products with optional category filter
     */
    fun loadProducts(category: String? = null) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            productRepository.getProducts(category)
                .onSuccess { 
                    _uiState.value = ProductUiState.Success.Product("Products loaded successfully")
                }
                .onFailure { 
                    _uiState.value = ProductUiState.Error(it.message ?: "Failed to load products")
                }
        }
    }
    
    /**
     * Load product details
     */
    fun loadProductDetails(productId: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            productRepository.getProduct(productId)
                .onSuccess { 
                    _selectedProduct.value = it
                    _uiState.value = ProductUiState.Success.Product("Product loaded")
                }
                .onFailure { 
                    _uiState.value = ProductUiState.Error(it.message ?: "Failed to load product details")
                }
        }
    }
    
    /**
     * Load user's cart
     */
    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            productRepository.getCart()
                .onSuccess { 
                    _cart.value = it
                    _uiState.value = ProductUiState.Success.Cart("Cart loaded")
                }
                .onFailure { 
                    _uiState.value = ProductUiState.Error(it.message ?: "Failed to load cart")
                }
        }
    }
    
    /**
     * Add product to cart
     */
    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            productRepository.addToCart(productId, quantity)
                .onSuccess { 
                    _cart.value = it
                    _uiState.value = ProductUiState.Success.Cart("Added to cart")
                }
                .onFailure { 
                    _uiState.value = ProductUiState.Error(it.message ?: "Failed to add to cart")
                }
        }
    }
    
    /**
     * Update cart item quantity
     */
    fun updateCartItem(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            productRepository.updateCart(cartItemId, quantity)
                .onSuccess { 
                    _cart.value = it
                    _uiState.value = ProductUiState.Success.Cart("Cart updated")
                }
                .onFailure { 
                    _uiState.value = ProductUiState.Error(it.message ?: "Failed to update cart")
                }
        }
    }
    
    /**
     * Remove item from cart
     */
    fun removeFromCart(cartItemId: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            productRepository.removeFromCart(cartItemId)
                .onSuccess { 
                    _cart.value = it
                    _uiState.value = ProductUiState.Success.Cart("Item removed from cart")
                }
                .onFailure { 
                    _uiState.value = ProductUiState.Error(it.message ?: "Failed to remove item from cart")
                }
        }
    }
    
    /**
     * Clear cart
     */
    fun clearCart() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            productRepository.clearCart()
                .onSuccess { 
                    _cart.value = null
                    _uiState.value = ProductUiState.Success.Cart("Cart cleared")
                }
                .onFailure { 
                    _uiState.value = ProductUiState.Error(it.message ?: "Failed to clear cart")
                }
        }
    }
}

/**
 * UI state for product and cart operations
 */
sealed class ProductUiState {
    object Idle : ProductUiState()
    object Loading : ProductUiState()
    
    // Different types of success states
    sealed class Success(val message: String) : ProductUiState() {
        class Cart(message: String) : Success(message)
        class Product(message: String) : Success(message)
        class Generic(message: String) : Success(message)
    }
    
    data class Error(val message: String) : ProductUiState()
}
