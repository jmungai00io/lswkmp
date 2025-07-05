package com.lswmobile.app.network

import com.lswmobile.app.network.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject

/**
 * API client for Livestock Wealth app
 */
class LivestockWealthApi(private val client: KtorClient) {
    
    // =============== AUTH ENDPOINTS ===============
    
    /**
     * Login user
     */
    suspend fun loginUser(loginBody: LoginBody, mode: String): JsonObject {
        print("LivestockWealthApi loginUser mode: $mode")
        return client.client.post {
            url("/auth/request-otp/$mode")
            setBody(loginBody)
            parameter("mode", mode)
        }.body()
    }
    
    /**
     * Pre-register user
     */
    suspend fun preRegister(preRegisterBody: PreRegisterBody, mode: String): JsonObject {
        return client.client.post {
            url("/auth/pre-register")
            setBody(preRegisterBody)
            parameter("mode", mode)
        }.body()
    }
    
    /**
     * Register user
     */
    suspend fun registerUser(registerBody: RegisterBody): JsonObject {
        return client.client.post {
            url("/auth/register")
            setBody(registerBody)
        }.body()
    }
    
    /**
     * Send OTP
     */
    suspend fun sendOtp(endpoint: String, otpBody: SendOTPBody): JsonObject {
        return client.client.post {
            url(endpoint)
            setBody(otpBody)
        }.body()
    }
    
    /**
     * Reset password
     */
    suspend fun resetPassword(requestBody: JsonObject): JsonObject {
        return client.client.post {
            url("/auth/reset-password")
            setBody(requestBody)
        }.body()
    }
    
    /**
     * Refresh token
     */
    suspend fun refreshToken(): RefreshTokenPayload {
        return client.client.post {
            url("/auth/refresh-token")
        }.body()
    }
    
    /**
     * Logout user
     */
    suspend fun logOutUser(): JsonObject {
        return client.client.post {
            url("/auth/logout")
        }.body()
    }
    
    // =============== USER ENDPOINTS ===============
    
    /**
     * Get current user
     */
    suspend fun getUser(): UserResponse {
        return client.client.get {
            url("/users/current")
        }.body()
    }
    
    /**
     * Update user profile
     */
    suspend fun updateProfile(updateProfileBody: UpdateProfileBody): UpdateProfileResponse {
        return client.client.put {
            url("/users/current")
            setBody(updateProfileBody)
        }.body()
    }
    
    /**
     * Update user preferences
     */
    suspend fun updatePreference(preferencesJson: JsonObject): JsonObject {
        return client.client.patch {
            url("/users/me/preferences")
            setBody(preferencesJson)
        }.body()
    }
    
    /**
     * Get user overview
     */
    suspend fun getUserOverview(): UserOverviewResponse {
        return client.client.get {
            url("/users/me/overview")
        }.body()
    }
    
    /**
     * Upload avatar
     */
    suspend fun uploadAvatar(fileBytes: ByteArray, fileName: String): ByteArray {
        val response = client.client.submitFormWithBinaryData(
            url = "/users/me/avatar",
            formData = formData {
                append("file", fileBytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=$fileName")
                })
            }
        )
        return response.readBytes()
    }
    
    // =============== WEBAUTHN ENDPOINTS ===============
    
    /**
     * Get registration options
     */
    suspend fun getRegistrationOptions(platform: String): RegistrationOptionsResponse {
        return client.client.get {
            url("/auth/webauthn/registration-options")
            parameter("platform", platform)
        }.body()
    }
    
    /**
     * Verify registration
     */
    suspend fun verifyRegistration(request: VerifyRegistrationRequest): JsonObject {
        return client.client.post {
            url("/auth/webauthn/verify-registration")
            setBody(request)
        }.body()
    }
    
    /**
     * Get authenticators
     */
    suspend fun getAuthenticators(): List<AuthenticatorResponse> {
        return client.client.get {
            url("/auth/webauthn/authenticators")
        }.body()
    }
    
    /**
     * Delete authenticator
     */
    suspend fun deleteAuthenticator(credentialId: String): JsonObject {
        return client.client.delete {
            url("/auth/webauthn/authenticators/$credentialId")
        }.body()
    }
    
    /**
     * Get passkey login options
     */
    suspend fun getPasskeyLoginOptions(platform: String): PasskeyLoginOptionsResponse {
        return client.client.get {
            url("/auth/webauthn/login-options")
            parameter("platform", platform)
        }.body()
    }
    
    /**
     * Verify passkey login
     */
    suspend fun verifyPasskeyLogin(request: PasskeyLoginVerificationRequest): JsonObject {
        return client.client.post {
            url("/auth/webauthn/verify-login")
            setBody(request)
        }.body()
    }
    
    // =============== PRODUCT ENDPOINTS ===============
    
    /**
     * Get products
     */
    suspend fun getProducts(category: String? = null, limit: Int = 20, offset: Int = 0): ProductsResponse {
        try {
            val response = client.client.get {
                url("/products")
                category?.let { parameter("category", it) }
                parameter("limit", limit)
                parameter("offset", offset)
            }
            
            // Log the raw response for debugging
            val responseText = response.bodyAsText()
            println("LivestockWealthApi: Raw products response: $responseText")
            
            // Use the response.body() method to decode instead of manual decoding
            return response.body()
        } catch (e: Exception) {
            println("LivestockWealthApi: Error parsing products response: ${e.message}")
            // Return empty response to avoid crashing
            return ProductsResponse(success = false)
        }
    }
    
    /**
     * Get product
     */
    suspend fun getProduct(productId: String): ProductClassic {
        return client.client.get {
            url("/products/$productId")
        }.body()
    }
    
    /**
     * Get farmlands
     */
    suspend fun getFarmlands(limit: Int = 20, offset: Int = 0): FarmlandsResponse {
        try {
            val response = client.client.get {
                url("/farmland")
                parameter("limit", limit)
                parameter("offset", offset)
            }
            
            // Log the raw response for debugging
            val responseText = response.bodyAsText()
            println("LivestockWealthApi: Raw farmlands response: $responseText")
            
            // Use response.body() for decoding
            return response.body()
        } catch (e: Exception) {
            println("LivestockWealthApi: Error parsing farmlands response: ${e.message}")
            // Return empty response to avoid crashing
            return FarmlandsResponse(success = false, data = emptyList())
        }
    }
    
    // =============== PRODUCT PREORDER ENDPOINTS ===============
    
    /**
     * Preorder a product that is currently out of stock
     */
    suspend fun preorderProduct(productType: String): PreorderResponse {
        try {
            val response = client.client.post {
                url("/transactions/orders/preorder")
                setBody(PreorderRequest(productType))
            }
            
            // Log the raw response for debugging
            val responseText = response.bodyAsText()
            println("LivestockWealthApi: Raw preorder response: $responseText")
            
            return response.body()
        } catch (e: Exception) {
            println("LivestockWealthApi: Error during preorder: ${e.message}")
            return PreorderResponse(
                success = false,
                error = e.message ?: "Unknown error occurred during preorder"
            )
        }
    }
    
    // =============== CART ENDPOINTS ===============
    
    /**
     * Get cart
     */
    suspend fun getCart(): CartResponse {
        return client.client.get {
            url("/cart")
        }.body()
    }
    
    /**
     * Add to cart
     */
    suspend fun addToCart(addToCartBody: AddToCartBody): CartResponse {
        return client.client.post {
            url("/cart/items")
            setBody(addToCartBody)
        }.body()
    }
    
    /**
     * Update cart
     */
    suspend fun updateCart(updateCartBody: UpdateCartBody): CartResponse {
        return client.client.patch {
            url("/cart/items/${updateCartBody.cartItemId}")
            setBody(updateCartBody)
        }.body()
    }
    
    /**
     * Remove from cart
     */
    suspend fun removeFromCart(cartItemId: String): CartResponse {
        return client.client.delete {
            url("/cart/items/$cartItemId")
        }.body()
    }
    
    /**
     * Clear cart
     */
    suspend fun clearCart(): JsonObject {
        return client.client.delete {
            url("/cart")
        }.body()
    }
    
    // =============== ORDERS ENDPOINTS ===============
    
    /**
     * Create a marketplace order with the given items and amount
     * This endpoint handles both farmlands and products but they can't be mixed in a single order
     */
    suspend fun createMarketplaceOrder(orderItems: List<MarketplaceOrderItem>, amount: Double): MarketplaceOrderResponse {
        try {
            val response = client.client.post {
                url("/transactions/orders")
                setBody(CreateMarketplaceOrderRequest(orderItems, amount))
            }
            
            // Log the raw response for debugging
            val responseText = response.bodyAsText()
            println("LivestockWealthApi: Raw order creation response: $responseText")
            
            return response.body()
        } catch (e: Exception) {
            println("LivestockWealthApi: Error during order creation: ${e.message}")
            e.printStackTrace()
            return MarketplaceOrderResponse(
                success = false,
                error = e.message ?: "Unknown error occurred during order creation"
            )
        }
    }
    
    /**
     * Create order
     */
    suspend fun createOrder(createOrderBody: CreateOrderBody): OrderResponse {
        return client.client.post {
            url("/orders")
            setBody(createOrderBody)
        }.body()
    }
    
    /**
     * Get orders
     */
    suspend fun getOrders(limit: Int = 20, offset: Int = 0): OrdersResponse {
        return client.client.get {
            url("/orders")
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }
    
    /**
     * Get order
     */
    suspend fun getOrder(orderId: String): OrderWithFullUserResponse {
        return client.client.get {
            url("/orders/$orderId")
        }.body()
    }
    
    /**
     * Get my orders from transactions
     * Returns a list of the user's marketplace orders
     */
    suspend fun getMyOrders(): MyOrdersResponse {
        try {
            val response = client.client.get {
                url("/transactions/orders/my-orders")
            }
            
            // Log the raw response for debugging
            val responseText = response.bodyAsText()
            println("LivestockWealthApi: Raw my orders response: $responseText")
            
            return response.body()
        } catch (e: Exception) {
            println("LivestockWealthApi: Error fetching my orders: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    /**
     * Get order details by order number from transactions
     * Returns detailed information about a specific marketplace order
     */
    suspend fun getOrderByNumber(orderNumber: Int): OrderWithFullUserResponse {
        try {
            val response = client.client.get {
                url("/transactions/orders/$orderNumber")
            }
            
            // Log the raw response for debugging
            val responseText = response.bodyAsText()
            println("LivestockWealthApi: Raw order details response: $responseText")
            
            // Parse the response
            val parsedResponse = response.body<OrderWithFullUserResponse>()
            
            // Log the parsed object
            println("LivestockWealthApi: Order details parsed. Success=${parsedResponse.success}")
            if (parsedResponse.success) {
                val orderData = parsedResponse.data
                println("LivestockWealthApi: Order #$orderNumber - Item count: ${orderData.items.size}")
                println("LivestockWealthApi: Order amount: ${orderData.amount}, status: ${orderData.status}")
                
                // Examine items more closely
                orderData.items.forEachIndexed { index, item ->
                    println("LivestockWealthApi: Item $index details:")
                    println("  ID: ${item._id}")
                    println("  Product Type: ${item.productType}")
                    println("  Price: ${item.priceOfAsset}")
                    println("  Is Unallocated: ${item.isUnallocated}")
                }
            }
            
            return parsedResponse
        } catch (e: Exception) {
            println("LivestockWealthApi: Error fetching order details: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    // =============== BENEFICIARY ENDPOINTS ===============
    
    /**
     * Get beneficiaries
     */
    suspend fun getBeneficiaries(): MyBeneficiariesResponse {
        return client.client.get {
            url("/beneficiaries")
        }.body()
    }
    
    /**
     * Add beneficiary
     */
    suspend fun addBeneficiary(beneficiary: Beneficiary): MyBeneficiariesResponse {
        return client.client.post {
            url("/beneficiaries")
            setBody(beneficiary)
        }.body()
    }
    
    /**
     * Update beneficiary
     */
    suspend fun updateBeneficiary(beneficiaryId: String, beneficiary: Beneficiary): MyBeneficiariesResponse {
        return client.client.patch {
            url("/beneficiaries/$beneficiaryId")
            setBody(beneficiary)
        }.body()
    }
    
    /**
     * Delete beneficiary
     */
    suspend fun deleteBeneficiary(beneficiaryId: String): JsonObject {
        return client.client.delete {
            url("/beneficiaries/$beneficiaryId")
        }.body()
    }
    
    // =============== WITHDRAWAL ENDPOINTS ===============
    
    /**
     * Get withdrawals
     */
    suspend fun getWithdrawals(): MyWithdrawalsResponse {
        return client.client.get {
            url("/withdrawals")
        }.body()
    }
    
    /**
     * Request withdrawal
     */
    suspend fun requestWithdrawal(withdrawal: WithdrawalBody): GetWithdrawalResponse {
        return client.client.post {
            url("/withdrawals")
            setBody(withdrawal)
        }.body()
    }
    
    // =============== FINANCE ENDPOINTS ===============
    
    /**
     * Get bank details
     */
    suspend fun getBankDetails(): EftDetailsServerResponse {
        return client.client.get {
            url("/finance/bank-details")
        }.body()
    }
    
    /**
     * Setup debit order
     */
    suspend fun setupDebitOrder(debitOrderBody: DebitOrderBody): JsonObject {
        return client.client.post {
            url("/finance/debit-order")
            setBody(debitOrderBody)
        }.body()
    }
    
    /**
     * Get wallet overview
     */
    suspend fun getWalletOverview(): WalletOverview {
        return client.client.get {
            url("/finance/wallet")
        }.body()
    }
    
    // =============== NEWS ENDPOINTS ===============
    
    /**
     * Get news feed
     */
    suspend fun getNewsFeed(limit: Int = 20, offset: Int = 0): NewsFeedResponse {
        return client.client.get {
            url("/news")
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }
    
    /**
     * Like news item
     */
    suspend fun likeNewsItem(newsId: String): JsonObject {
        return client.client.post {
            url("/news/$newsId/like")
        }.body()
    }
    
    /**
     * Unlike news item
     */
    suspend fun unlikeNewsItem(newsId: String): JsonObject {
        return client.client.delete {
            url("/news/$newsId/like")
        }.body()
    }
}
