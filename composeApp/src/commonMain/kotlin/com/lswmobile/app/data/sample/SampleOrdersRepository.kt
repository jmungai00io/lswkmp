package com.lswmobile.app.data.sample

import com.lswmobile.app.network.model.OrderItem
import com.lswmobile.app.network.model.Asset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

/**
 * Sample repository for orders data with hardcoded orders
 * Will be replaced with actual API integration later
 */
class SampleOrdersRepository {
    
    private val _orders = MutableStateFlow<List<OrderItem>>(generateSampleOrders())
    val orders: Flow<List<OrderItem>> = _orders.asStateFlow()
    
    private val _assets = MutableStateFlow<List<Asset>>(generateSampleAssets())
    val assets: Flow<List<Asset>> = _assets.asStateFlow()
    
    /**
     * Get order by ID
     */
    fun getOrderById(orderId: String): OrderItem? {
        return _orders.value.find { it._id == orderId }
    }
    
    /**
     * Get asset by ID
     */
    fun getAssetById(assetId: String): Asset? {
        return _assets.value.find { it.id == assetId }
    }
    
    /**
     * Get assets for order ID
     */
    fun getAssetsForOrder(orderId: String): List<Asset> {
        return _assets.value.filter { it.orderId == orderId }
    }
    
    /**
     * Generate sample orders with realistic data
     */
    private fun generateSampleOrders(): List<OrderItem> {
        val baseTime = Clock.System.now()
        
        return listOf(
            OrderItem(
                _id = "ord_1",
                userId = "user_1",
                productId = "1", // Premium Cattle Investment
                amount = 25000.0,
                status = "completed",
                createdAt = (baseTime - 30.days).toString(),
                updatedAt = (baseTime - 29.days).toString()
            ),
            OrderItem(
                _id = "ord_2",
                userId = "user_1",
                productId = "3", // Organic Vegetable Farm
                amount = 10000.0,
                status = "completed",
                createdAt = (baseTime - 15.days).toString(),
                updatedAt = (baseTime - 14.days).toString()
            ),
            OrderItem(
                _id = "ord_3",
                userId = "user_1",
                productId = "6", // Aquaponics Fish Farm
                amount = 18000.0,
                status = "processing",
                createdAt = (baseTime - 5.days).toString(),
                updatedAt = (baseTime - 4.days).toString()
            ),
            OrderItem(
                _id = "ord_4",
                userId = "user_1",
                productId = "2", // Sustainable Dairy Farm
                amount = 15000.0,
                status = "pending",
                createdAt = (baseTime - 2.days).toString(),
                updatedAt = (baseTime - 2.days).toString()
            ),
            OrderItem(
                _id = "ord_5",
                userId = "user_1",
                productId = "5", // Sustainable Sheep Farm
                amount = 12000.0,
                status = "cancelled",
                createdAt = (baseTime - 10.days).toString(),
                updatedAt = (baseTime - 8.days).toString()
            )
        )
    }
    
    /**
     * Generate sample assets for the orders
     */
    private fun generateSampleAssets(): List<Asset> {
        return listOf(
            Asset(
                id = "asset_1",
                name = "Premium Cattle Share #12345",
                type = "livestock",
                status = "active",
                orderId = "ord_1",
                orderNumber = 12345,
                purchaseDate = formatDate(Clock.System.now() - 29.days),
                imageUrl = "https://images.unsplash.com/photo-1570042225831-d98fa7577f1e?q=80&w=1740&auto=format&fit=crop"
            ),
            Asset(
                id = "asset_2",
                name = "Organic Vegetable Farm Share #23456",
                type = "produce",
                status = "active",
                orderId = "ord_2",
                orderNumber = 23456,
                purchaseDate = formatDate(Clock.System.now() - 14.days),
                imageUrl = "https://images.unsplash.com/photo-1523348837708-15d4a09cfac2?q=80&w=1740&auto=format&fit=crop"
            ),
            Asset(
                id = "asset_3",
                name = "Aquaponics Fish Farm Share #34567",
                type = "aquaculture",
                status = "pending",
                orderId = "ord_3",
                orderNumber = 34567,
                purchaseDate = formatDate(Clock.System.now() - 4.days),
                imageUrl = "https://images.unsplash.com/photo-1535742386204-960ec2a22da6?q=80&w=1740&auto=format&fit=crop"
            )
        )
    }
    
    /**
     * Format date to friendly string
     */
    private fun formatDate(instant: Instant): String {
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}-${dateTime.dayOfMonth.toString().padStart(2, '0')}"
    }
    
    companion object {
        private var instance: SampleOrdersRepository? = null
        
        fun getInstance(): SampleOrdersRepository {
            return instance ?: SampleOrdersRepository().also { instance = it }
        }
    }
}
