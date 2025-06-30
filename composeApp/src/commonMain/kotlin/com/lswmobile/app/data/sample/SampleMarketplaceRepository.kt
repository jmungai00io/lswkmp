package com.lswmobile.app.data.sample

import com.lswmobile.app.network.model.AddToCartBody
import com.lswmobile.app.network.model.CartItem
import com.lswmobile.app.network.model.CartResponse
import com.lswmobile.app.network.model.Farmaland
import com.lswmobile.app.network.model.ProductFarmland
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Sample repository for marketplace with hardcoded data for UI development
 * Handles both ProductFarmland and regular Farmland models with separate carts
 */
class SampleMarketplaceRepository private constructor() {
    
    // Farmland products
    private val _productFarmlands = MutableStateFlow<List<ProductFarmland>>(createSampleProductFarmlands())
    val productFarmlands: StateFlow<List<ProductFarmland>> = _productFarmlands.asStateFlow()
    
    // Regular products
    private val _farmlands = MutableStateFlow<List<Farmaland>>(createSampleFarmlands())
    val farmlands: StateFlow<List<Farmaland>> = _farmlands.asStateFlow()
    
    // Cart for ProductFarmland
    private val _farmlandCart = MutableStateFlow<List<CartItem>>(emptyList())
    val farmlandCart: StateFlow<List<CartItem>> = _farmlandCart.asStateFlow()
    
    // Cart for regular Farmland
    private val _regularCart = MutableStateFlow<List<CartItem>>(emptyList())
    val regularCart: StateFlow<List<CartItem>> = _regularCart.asStateFlow()
    
    // Total amount for ProductFarmland cart
    private val _farmlandCartTotal = MutableStateFlow(0.0)
    val farmlandCartTotal: StateFlow<Double> = _farmlandCartTotal.asStateFlow()
    
    // Total amount for regular Farmland cart
    private val _regularCartTotal = MutableStateFlow(0.0)
    val regularCartTotal: StateFlow<Double> = _regularCartTotal.asStateFlow()
    
    /**
     * Add ProductFarmland to cart
     */
    fun addProductFarmlandToCart(productId: String, quantity: Int) {
        val product = _productFarmlands.value.find { it._id == productId } ?: return
        
        // Check if product already in cart
        val existingCartItem = _farmlandCart.value.find { it.productId == productId }
        
        if (existingCartItem != null) {
            // Update quantity
            _farmlandCart.update { currentItems ->
                currentItems.map {
                    if (it.productId == productId) {
                        it.copy(quantity = it.quantity + quantity)
                    } else {
                        it
                    }
                }
            }
        } else {
            // Add new item
            _farmlandCart.update { currentItems ->
                currentItems + CartItem(
                    id = generateCartItemId(),
                    productId = productId,
                    quantity = quantity,
                    price = product.price,
                    product = product
                )
            }
        }
        
        // Update total
        calculateFarmlandCartTotal()
    }
    
    /**
     * Add regular Farmland to cart
     */
    fun addRegularToCart(productId: String, quantity: Int) {
        val product = _farmlands.value.find { it._id == productId } ?: return
        
        // Check if product already in cart
        val existingCartItem = _regularCart.value.find { it.productId == productId }
        
        if (existingCartItem != null) {
            // Update quantity
            _regularCart.update { currentItems ->
                currentItems.map {
                    if (it.productId == productId) {
                        it.copy(quantity = it.quantity + quantity)
                    } else {
                        it
                    }
                }
            }
        } else {
            // Add new item
            _regularCart.update { currentItems ->
                currentItems + CartItem(
                    id = generateCartItemId(),
                    productId = productId,
                    quantity = quantity,
                    price = product.price,
                    product = convertToProductFarmland(product)
                )
            }
        }
        
        // Update total
        calculateRegularCartTotal()
    }
    
    /**
     * Remove item from ProductFarmland cart
     */
    fun removeProductFarmlandFromCart(cartItemId: String) {
        _farmlandCart.update { currentItems ->
            currentItems.filter { it.id != cartItemId }
        }
        
        // Update total
        calculateFarmlandCartTotal()
    }
    
    /**
     * Remove item from regular Farmland cart
     */
    fun removeFarmlandFromCart(cartItemId: String) {
        _regularCart.update { currentItems ->
            currentItems.filter { it.id != cartItemId }
        }
        
        // Update total
        calculateRegularCartTotal()
    }
    
    /**
     * Update quantity for ProductFarmland cart item
     */
    fun updateProductFarmlandCartItemQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeProductFarmlandFromCart(cartItemId)
            return
        }
        
        _farmlandCart.update { currentItems ->
            currentItems.map {
                if (it.id == cartItemId) {
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }
        }
        
        // Update total
        calculateFarmlandCartTotal()
    }
    
    /**
     * Update quantity for regular Farmland cart item
     */
    fun updateFarmlandCartItemQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFarmlandFromCart(cartItemId)
            return
        }
        
        _regularCart.update { currentItems ->
            currentItems.map {
                if (it.id == cartItemId) {
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }
        }
        
        // Update total
        calculateRegularCartTotal()
    }
    
    /**
     * Clear ProductFarmland cart
     */
    fun clearProductFarmlandCart() {
        _farmlandCart.value = emptyList()
        _farmlandCartTotal.value = 0.0
    }
    
    /**
     * Clear regular Farmland cart
     */
    fun clearFarmlandCart() {
        _regularCart.value = emptyList()
        _regularCartTotal.value = 0.0
    }
    
    /**
     * Calculate total for ProductFarmland cart
     */
    private fun calculateFarmlandCartTotal() {
        val total = _farmlandCart.value.sumOf { it.price * it.quantity }
        _farmlandCartTotal.value = total
    }
    
    /**
     * Calculate total for regular Farmland cart
     */
    private fun calculateRegularCartTotal() {
        val total = _regularCart.value.sumOf { it.price * it.quantity }
        _regularCartTotal.value = total
    }
    
    /**
     * Generate a random string ID for cart items
     */
    private fun generateCartItemId(): String {
        return List(16) { ('a'..'z') + ('0'..'9') }.flatten().shuffled().take(8).joinToString("")
    }
    
    /**
     * Create sample ProductFarmland data
     */
    private fun createSampleProductFarmlands(): List<ProductFarmland> {
        return listOf(
            ProductFarmland(
                _id = "pf1",
                name = "Premium Cattle Farm",
                description = "High-yield cattle investment opportunity with regular dividends",
                price = 25000.00,
                imageUrl = "https://images.unsplash.com/photo-1500595046743-cd271d694d30?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                status = "available",
                createdAt = "2023-01-15T10:30:00Z",
                updatedAt = "2023-01-15T10:30:00Z"
            ),
            ProductFarmland(
                _id = "pf2",
                name = "Organic Crop Investment",
                description = "Sustainable organic crop farming with quarterly returns",
                price = 15000.00,
                imageUrl = "https://images.unsplash.com/photo-1625246333195-78d9c38ad449?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                status = "limited",
                createdAt = "2023-02-20T14:15:00Z",
                updatedAt = "2023-02-20T14:15:00Z"
            ),
            ProductFarmland(
                _id = "pf3",
                name = "Heritage Livestock Project",
                description = "Investment in preserving heritage breed livestock with ethical farming practices",
                price = 35000.00,
                imageUrl = "https://images.unsplash.com/photo-1516467508483-a7212febe31a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                status = "available",
                createdAt = "2023-03-10T09:45:00Z",
                updatedAt = "2023-03-10T09:45:00Z"
            ),
            ProductFarmland(
                _id = "pf4",
                name = "Dairy Production Farm",
                description = "High-capacity dairy production with modern facilities",
                price = 28000.00,
                imageUrl = "https://images.unsplash.com/photo-1495107334309-fcf20f6a8343?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                status = "available",
                createdAt = "2023-04-05T16:20:00Z",
                updatedAt = "2023-04-05T16:20:00Z"
            ),
            ProductFarmland(
                _id = "pf5",
                name = "Sustainable Farmland Investment",
                description = "Long-term investment in sustainable agricultural practices",
                price = 18000.00,
                imageUrl = "https://images.unsplash.com/photo-1594761056008-565e1f5c54ea?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1738&q=80",
                status = "limited",
                createdAt = "2023-05-12T11:30:00Z",
                updatedAt = "2023-05-12T11:30:00Z"
            )
        )
    }
    
    /**
     * Create sample regular Farmland data
     */
    private fun createSampleFarmlands(): List<Farmaland> {
        return listOf(
            Farmaland(
                _id = "f1",
                name = "Community Supported Agriculture",
                description = "Investment in local community farm with weekly produce shares",
                price = 10000.00,
                imageUrl = "https://images.unsplash.com/photo-1464226184884-fa280b87c399?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80"
            ),
            Farmaland(
                _id = "f2",
                name = "Urban Farming Project",
                description = "Innovative urban farming utilizing vertical gardening and hydroponics",
                price = 12000.00,
                imageUrl = "https://images.unsplash.com/photo-1530836369250-ef72a3f5cda8?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80"
            ),
            Farmaland(
                _id = "f3",
                name = "Vineyard Investment",
                description = "Premium vineyard investment with annual wine allocation",
                price = 45000.00,
                imageUrl = "https://images.unsplash.com/photo-1559944152-71066286e105?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1742&q=80"
            ),
            Farmaland(
                _id = "f4",
                name = "Greenhouse Produce",
                description = "High-tech greenhouse for year-round produce production",
                price = 22000.00,
                imageUrl = "https://images.unsplash.com/photo-1574323347407-f5e1c5a1ec21?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80"
            ),
            Farmaland(
                _id = "f5",
                name = "Poultry Farm",
                description = "Free-range ethical poultry farming with monthly egg yields",
                price = 14000.00,
                imageUrl = "https://images.unsplash.com/photo-1548550023-2bdb3c5beed7?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80"
            )
        )
    }
    
    /**
     * Convert Farmland to ProductFarmland for cart usage
     */
    private fun convertToProductFarmland(farmland: Farmaland): ProductFarmland {
        return ProductFarmland(
            _id = farmland._id,
            name = farmland.name,
            description = farmland.description,
            price = farmland.price,
            imageUrl = farmland.imageUrl,
            status = "available", // Default status for sample data
            createdAt = "",
            updatedAt = ""
        )
    }
    
    companion object {
        private var instance: SampleMarketplaceRepository? = null
        
        fun getInstance(): SampleMarketplaceRepository {
            val current = instance
            if (current != null) {
                return current
            }
            
            val newInstance = SampleMarketplaceRepository()
            instance = newInstance
            return newInstance
        }
    }
}
