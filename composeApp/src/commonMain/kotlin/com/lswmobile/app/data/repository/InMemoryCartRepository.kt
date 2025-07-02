package com.lswmobile.app.data.repository

import com.lswmobile.app.data.model.CartItem
import com.lswmobile.app.data.model.CartItemType
import com.lswmobile.app.data.model.CartOperationResult
import com.lswmobile.app.data.model.CartSummary
import com.lswmobile.app.network.model.Farmland
import com.lswmobile.app.network.model.ProductClassic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.random.Random

/**
 * Simple in-memory cart repository for testing purposes
 * This implements the CartRepository interface but stores data in memory
 */
class InMemoryCartRepository : CartRepository {
    
    private val cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    
    override fun getCartItemsByType(type: CartItemType): Flow<List<CartItem>> {
        return cartItems.map { items ->
            items.values.filter { it.itemType == type }
        }
    }
    
    override val cartSummary: Flow<CartSummary>
        get() = cartItems.map { items ->
            val itemCount = items.values.sumOf { it.quantity }
            val totalAmount = items.values.sumOf { it.price * it.quantity }
            CartSummary(
                itemCount = itemCount,
                totalQuantity = itemCount,
                totalPrice = totalAmount
            )
        }
    
    override suspend fun addProductToCart(product: ProductClassic, quantity: Int): CartOperationResult {
        return try {
            val itemId = "product_${product._id}"
            val existingItem = cartItems.value[itemId]
            
            if (existingItem != null) {
                // Update existing item
                val updatedItem = existingItem.copy(
                    quantity = existingItem.quantity + quantity
                )
                cartItems.value = cartItems.value + (itemId to updatedItem)
            } else {
                // Add new item
                val newItem = CartItem(
                    id = itemId,
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
                    userId = "test_user",
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
                cartItems.value = cartItems.value + (itemId to newItem)
            }
            
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e.message ?: "Failed to add product to cart")
        }
    }
    
    override suspend fun addFarmlandToCart(farmland: Farmland, quantity: Int): CartOperationResult {
        return try {
            val itemId = "farmland_${farmland._id}"
            val existingItem = cartItems.value[itemId]
            
            if (existingItem != null) {
                // Update existing item
                val updatedItem = existingItem.copy(
                    quantity = existingItem.quantity + quantity
                )
                cartItems.value = cartItems.value + (itemId to updatedItem)
            } else {
                // Add new item
                val newItem = CartItem(
                    id = itemId,
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
                    userId = "test_user",
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
                cartItems.value = cartItems.value + (itemId to newItem)
            }
            
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e.message ?: "Failed to add farmland to cart")
        }
    }
    
    override suspend fun updateCartItemQuantity(itemId: String, quantity: Int): CartOperationResult {
        return try {
            val existingItem = cartItems.value[itemId]
            if (existingItem != null) {
                if (quantity <= 0) {
                    // Remove item if quantity is 0 or negative
                    cartItems.value = cartItems.value - itemId
                } else {
                    // Update quantity
                    val updatedItem = existingItem.copy(quantity = quantity)
                    cartItems.value = cartItems.value + (itemId to updatedItem)
                }
            }
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e.message ?: "Failed to update cart item quantity")
        }
    }
    
    override suspend fun removeFromCart(itemId: String): CartOperationResult {
        return try {
            cartItems.value = cartItems.value - itemId
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e.message ?: "Failed to remove item from cart")
        }
    }
    
    override suspend fun clearCart(): CartOperationResult {
        return try {
            cartItems.value = emptyMap()
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e.message ?: "Failed to clear cart")
        }
    }
} 