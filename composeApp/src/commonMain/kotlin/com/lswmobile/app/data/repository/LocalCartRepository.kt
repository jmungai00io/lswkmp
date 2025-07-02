package com.lswmobile.app.data.repository

import com.lswmobile.app.data.model.*
import com.lswmobile.app.network.model.Farmland
import com.lswmobile.app.network.model.ProductClassic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random

/**
 * Local cart repository using in-memory storage
 * Handles both products and farmlands in a unified way
 * Note: This is a temporary implementation without persistence
 */
class LocalCartRepository(
    private val currentUserId: String
) : CartRepository {
    
    // StateFlow for cart items
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: Flow<List<CartItem>> = _cartItems.asStateFlow()
    
    // StateFlow for cart summary
    private val _cartSummary = MutableStateFlow(CartSummary(0, 0, 0.0))
    override val cartSummary: Flow<CartSummary> = _cartSummary.asStateFlow()
    
    init {
        // TODO: Uncomment when CartDatabase is available
        // CoroutineScope(Dispatchers.Main).launch {
        //     loadCartItems()
        //     loadCartSummary()
        // }
    }
    
    /**
     * Add a product to cart
     */
    override suspend fun addProductToCart(product: ProductClassic, quantity: Int): CartOperationResult {
        return try {
            val cartItem = createCartItemFromProduct(product, quantity)
            // TODO: Uncomment when CartDatabase is available
            /*
            cartItemQueries.insertCartItem(
                id = cartItem.id,
                itemId = cartItem.itemId,
                itemType = cartItem.itemType.name,
                quantity = cartItem.quantity.toLong(),
                price = cartItem.price,
                name = cartItem.name,
                location = cartItem.location,
                images = cartItem.images?.let { Json.encodeToString(it) },
                profitRate = cartItem.profitRate,
                profitCycle = cartItem.profitCycle,
                unitCount = cartItem.unitCount,
                productType = cartItem.productType,
                investmentTerm = cartItem.investmentTerm,
                dividendCycle = cartItem.dividendCycle,
                profitInformation = cartItem.profitInformation,
                count = cartItem.count,
                inStock = cartItem.inStock?.let { if (it) 1L else 0L },
                isArchived = cartItem.isArchived?.let { if (it) 1L else 0L },
                infoUrl = cartItem.infoUrl,
                userId = cartItem.userId,
                createdAt = cartItem.createdAt
            )
            */
            
            // For now, add to memory
            val currentItems = _cartItems.value.toMutableList()
            currentItems.add(cartItem)
            _cartItems.value = currentItems
            
            updateCartSummary()
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e.message ?: "Failed to add product to cart")
        }
    }
    
    /**
     * Add a farmland to cart
     */
    override suspend fun addFarmlandToCart(farmland: Farmland, quantity: Int): CartOperationResult {
        return try {
            val cartItem = createCartItemFromFarmland(farmland, quantity)
            // TODO: Uncomment when CartDatabase is available
            /*
            cartItemQueries.insertCartItem(
                id = cartItem.id,
                itemId = cartItem.itemId,
                itemType = cartItem.itemType.name,
                quantity = cartItem.quantity.toLong(),
                price = cartItem.price,
                name = cartItem.name,
                location = cartItem.location,
                images = cartItem.images?.let { Json.encodeToString(it) },
                profitRate = cartItem.profitRate,
                profitCycle = cartItem.profitCycle,
                unitCount = cartItem.unitCount,
                productType = cartItem.productType,
                investmentTerm = cartItem.investmentTerm,
                dividendCycle = cartItem.dividendCycle,
                profitInformation = cartItem.profitInformation,
                count = cartItem.count,
                inStock = cartItem.inStock?.let { if (it) 1L else 0L },
                isArchived = cartItem.isArchived?.let { if (it) 1L else 0L },
                infoUrl = cartItem.infoUrl,
                userId = cartItem.userId,
                createdAt = cartItem.createdAt
            )
            */
            
            // For now, add to memory
            val currentItems = _cartItems.value.toMutableList()
            currentItems.add(cartItem)
            _cartItems.value = currentItems
            
            updateCartSummary()
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e.message ?: "Failed to add farmland to cart")
        }
    }
    
    /**
     * Update cart item quantity
     */
    override suspend fun updateCartItemQuantity(itemId: String, quantity: Int): CartOperationResult {
        return try {
            // TODO: Uncomment when CartDatabase is available
            /*
            cartItemQueries.updateCartItemQuantity(
                quantity = quantity.toLong(),
                id = itemId,
                userId = currentUserId
            )
            */
            
            // For now, update in memory
            val currentItems = _cartItems.value.toMutableList()
            val index = currentItems.indexOfFirst { it.id == itemId }
            if (index != -1) {
                currentItems[index] = currentItems[index].copy(quantity = quantity)
                _cartItems.value = currentItems
                updateCartSummary()
            }
            
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e.message ?: "Failed to update cart item quantity")
        }
    }
    
    /**
     * Remove item from cart
     */
    override suspend fun removeFromCart(itemId: String): CartOperationResult {
        return try {
            // TODO: Uncomment when CartDatabase is available
            /*
            cartItemQueries.deleteCartItem(
                id = itemId,
                userId = currentUserId
            )
            */
            
            // For now, remove from memory
            val currentItems = _cartItems.value.toMutableList()
            currentItems.removeAll { it.id == itemId }
            _cartItems.value = currentItems
            
            updateCartSummary()
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e.message ?: "Failed to remove item from cart")
        }
    }
    
    /**
     * Clear all cart items for current user
     */
    override suspend fun clearCart(): CartOperationResult {
        return try {
            // TODO: Uncomment when CartDatabase is available
            // cartItemQueries.clearCart(userId = currentUserId)
            
            // For now, clear from memory
            _cartItems.value = emptyList()
            updateCartSummary()
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e.message ?: "Failed to clear cart")
        }
    }
    
    /**
     * Get cart items by type
     */
    override fun getCartItemsByType(itemType: CartItemType): Flow<List<CartItem>> {
        return cartItems.map { items ->
            items.filter { it.itemType == itemType }
        }
    }
    
    /**
     * Get cart summary by type
     */
    suspend fun getCartSummaryByType(itemType: CartItemType): CartSummaryByType {
        // TODO: Uncomment when CartDatabase is available
        /*
        val result = cartItemQueries.getCartSummaryByType(
            userId = currentUserId,
            itemType = itemType.name
        ).executeAsOneOrNull()
        
        return CartSummaryByType(
            itemCount = result?.column1?.toInt() ?: 0,
            totalQuantity = result?.column2?.toInt() ?: 0,
            totalPrice = result?.column3 ?: 0.0,
            itemType = itemType
        )
        */
        
        // For now, calculate from memory
        val items = _cartItems.value.filter { it.itemType == itemType }
        val itemCount = items.size
        val totalQuantity = items.sumOf { it.quantity }
        val totalPrice = items.sumOf { it.price * it.quantity }
        
        return CartSummaryByType(
            itemCount = itemCount,
            totalQuantity = totalQuantity,
            totalPrice = totalPrice,
            itemType = itemType
        )
    }
    
    /**
     * Load cart items from database
     */
    private suspend fun loadCartItems() {
        // TODO: Uncomment when CartDatabase is available
        /*
        val items = cartItemQueries.getCartItems(userId = currentUserId)
            .executeAsList()
            .map { row ->
                CartItem(
                    id = row.id,
                    itemId = row.itemId,
                    itemType = CartItemType.valueOf(row.itemType),
                    quantity = row.quantity.toInt(),
                    price = row.price,
                    name = row.name,
                    location = row.location,
                    images = row.images?.let { Json.decodeFromString<List<String>>(it) },
                    profitRate = row.profitRate,
                    profitCycle = row.profitCycle,
                    unitCount = row.unitCount,
                    productType = row.productType,
                    investmentTerm = row.investmentTerm,
                    dividendCycle = row.dividendCycle,
                    profitInformation = row.profitInformation,
                    count = row.count,
                    inStock = row.inStock?.let { it == 1L },
                    isArchived = row.isArchived?.let { it == 1L },
                    infoUrl = row.infoUrl,
                    userId = row.userId,
                    createdAt = row.createdAt
                )
            }
        
        _cartItems.value = items
        */
    }
    
    /**
     * Load cart summary from database
     */
    private suspend fun loadCartSummary() {
        // TODO: Uncomment when CartDatabase is available
        /*
        val result = cartItemQueries.getCartSummary(userId = currentUserId)
            .executeAsOneOrNull()
        
        _cartSummary.value = CartSummary(
            itemCount = result?.column1?.toInt() ?: 0,
            totalQuantity = result?.column2?.toInt() ?: 0,
            totalPrice = result?.column3 ?: 0.0
        )
        */
    }
    
    /**
     * Update cart summary from current items
     */
    private fun updateCartSummary() {
        val items = _cartItems.value
        val itemCount = items.size
        val totalQuantity = items.sumOf { it.quantity }
        val totalPrice = items.sumOf { it.price * it.quantity }
        
        _cartSummary.value = CartSummary(
            itemCount = itemCount,
            totalQuantity = totalQuantity,
            totalPrice = totalPrice
        )
    }
    
    /**
     * Create CartItem from ProductClassic
     */
    private fun createCartItemFromProduct(product: ProductClassic, quantity: Int): CartItem {
        return CartItem(
            id = generateCartItemId(),
            itemId = product._id,
            itemType = CartItemType.PRODUCT,
            quantity = quantity,
            price = product.price,
            name = product.name ?: product.productName ?: "Unnamed Product",
            location = product.location,
            images = product.images,
            profitRate = product.profitRate?.toString(),
            profitCycle = product.profitCycle?.toString(),
            unitCount = product.unitCount?.toString(),
            productType = product.productType,
            investmentTerm = product.investmentTerm?.toString(),
            dividendCycle = product.dividendCycle?.toString(),
            profitInformation = product.profitInformation,
            count = product.count?.toString(),
            inStock = product.inStock,
            isArchived = product.isArchived,
            infoUrl = product.infoUrl,
            userId = currentUserId,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
    }
    
    /**
     * Create CartItem from Farmland
     */
    private fun createCartItemFromFarmland(farmland: Farmland, quantity: Int): CartItem {
        return CartItem(
            id = generateCartItemId(),
            itemId = farmland._id,
            itemType = CartItemType.FARMLAND,
            quantity = quantity,
            price = farmland.price,
            name = farmland.name,
            location = farmland.location,
            images = farmland.images,
            profitRate = farmland.profitRate?.toString(),
            profitCycle = farmland.profitCycle?.toString(),
            unitCount = farmland.unitCount?.toString(),
            productType = farmland.productType,
            investmentTerm = null,
            dividendCycle = null,
            profitInformation = null,
            count = null,
            inStock = null,
            isArchived = farmland.isArchived,
            infoUrl = farmland.infoUrl,
            userId = currentUserId,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
    }
    
    /**
     * Generate unique cart item ID
     */
    private fun generateCartItemId(): String {
        return "cart_${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(1000, 9999)}"
    }
} 