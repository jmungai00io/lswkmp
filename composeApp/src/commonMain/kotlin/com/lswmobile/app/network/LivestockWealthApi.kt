package com.lswmobile.app.network

import com.lswmobile.app.network.model.*
import io.ktor.client.call.*
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import com.lswmobile.app.config.AppConfigFactory

/**
 * API client for Livestock Wealth app
 */
class LivestockWealthApi(private val client: KtorClient) {
    
    // =============== AUTH ENDPOINTS ===============
    
    /**
     * Login user
     */
    suspend fun loginUser(loginBody: LoginBody, mode: String): JsonObject {
        val response = client.client.post {
            url("/auth/request-otp/$mode")
            setBody(loginBody)
            parameter("mode", mode)
        }
        if (AppConfigFactory.get().isDevelopment) {
            runCatching {
                val cookies = client.client.cookies(response.request.url)
                val rt = cookies.firstOrNull { it.name.equals("refreshToken", ignoreCase = true) }
                val masked = rt?.value?.let { v ->
                    if (v.length > 10) "${v.take(4)}...${v.takeLast(4)}(len=${v.length})" else "<short>"
                } ?: "<none>"
            }
        }
        return response.body()
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
        val response = client.client.post {
            url(endpoint)
            setBody(otpBody)
        }
        if (AppConfigFactory.get().isDevelopment) {
            runCatching {
                val cookies = client.client.cookies(response.request.url)
                val rt = cookies.firstOrNull { it.name.equals("refreshToken", ignoreCase = true) }
                val masked = rt?.value?.let { v ->
                    if (v.length > 10) "${v.take(4)}...${v.takeLast(4)}(len=${v.length})" else "<short>"
                } ?: "<none>"
            }
        }
        return response.body()
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
    suspend fun refreshToken(): JsonObject {
        // Server sets new httpOnly cookie and returns { token: <accessToken> }
        val response = client.client.get {
            url("/auth/refresh-token")
        }
        // Inspect cookie storage to verify refreshToken presence (masked)
        if (AppConfigFactory.get().isDevelopment) {
            runCatching {
                val cookies = client.client.cookies(response.request.url)
                val rt = cookies.firstOrNull { it.name.equals("refreshToken", ignoreCase = true) }
                val masked = rt?.value?.let { v ->
                    if (v.length > 10) "${v.take(4)}...${v.takeLast(4)}(len=${v.length})" else "<short>"
                } ?: "<none>"
            }
        }
        return response.body()
    }
    
    /**
     * Logout user
     */
    suspend fun logOutUser(): Unit {
        val response = client.client.get {
            url("/auth/logout")
        }

        // Some environments return 204 with no payload, so just ensure the body is consumed.
        runCatching { response.body<JsonElement>() }
        return Unit
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

        // Determine MIME type based on file extension
        val mimeType = when {
            fileName.lowercase().endsWith(".jpg") || fileName.lowercase().endsWith(".jpeg") -> "image/jpeg"
            fileName.lowercase().endsWith(".png") -> "image/png"
            fileName.lowercase().endsWith(".webp") -> "image/webp"
            else -> "image/jpeg" // Default to JPEG
        }
        

        val response = client.client.submitFormWithBinaryData(
            url = "/users/upload-avatar",
            formData = formData {
                append("file", fileBytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=$fileName")
                    append(HttpHeaders.ContentType, mimeType)
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
            
            val responseText = response.bodyAsText()

            return response.body()
        } catch (e: Exception) {
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

            return response.body()
        } catch (e: Exception) {
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

            return response.body()
        } catch (e: Exception) {
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

            return response.body()
        } catch (e: Exception) {
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

            return response.body()
        } catch (e: Exception) {
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
            
            val responseText = response.bodyAsText()

            val parsedResponse = response.body<OrderWithFullUserResponse>()
            
            if (parsedResponse.success) {
                val orderData = parsedResponse.data
            }
            
            return parsedResponse
        } catch (e: Exception) {
            throw e
        }
    }
    
    /**
     * Update payment method for an order
     */
    suspend fun updatePaymentMethod(orderId: String, paymentBody: PaymentBody): JsonObject {
        return try {

            val response = client.client.put {
                url("/transactions/orders/$orderId/payment-method")
                setBody(paymentBody)
            }
            
            response.body()
        } catch (e: Exception) {
            throw e
        }
    }
    
    // =============== BENEFICIARY ENDPOINTS ===============
    
    /**
     * Get beneficiaries
     */
    suspend fun getBeneficiaries(page: Int = 1, limit: Int = 10): MyBeneficiariesResponse {
        return client.client.get {
            url("/users/beneficiaries")
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }
    
    /**
     * Add beneficiary
     */
    suspend fun addBeneficiary(beneficiary: Beneficiary): AddBeneficiaryResponse {
        return client.client.post {
            url("/users/add-beneficiary")
            setBody(beneficiary)
        }.body()
    }
    
    /**
     * Update beneficiary
     */
    suspend fun updateBeneficiary(beneficiaryId: String, beneficiary: Beneficiary): MyBeneficiariesResponse {
        return client.client.patch {
            url("/users/beneficiaries/$beneficiaryId")
            setBody(beneficiary)
        }.body()
    }
    
    /**
     * Delete beneficiary
     */
    suspend fun deleteBeneficiary(beneficiaryId: String): JsonObject {
        return client.client.delete {
            url("/users/delete-beneficiary/$beneficiaryId")
        }.body()
    }
    
    // =============== WITHDRAWAL ENDPOINTS ===============
    
    /**
     * Get withdrawals
     */
    suspend fun getWithdrawals(): MyWithdrawalsResponse {
        return client.client.get {
            url("/transactions/withdrawals/my-withdrawals")
        }.body()
    }
    
    /**
     * Get withdrawal by ID
     */
    suspend fun getWithdrawal(withdrawalId: String): GetWithdrawalResponse {
        return client.client.get {
            url("/transactions/withdrawals/$withdrawalId")
        }.body()
    }
    
    /**
     * Request withdrawal
     */
    suspend fun requestWithdrawal(withdrawal: WithdrawalBody): CreateWithdrawalResponse {
        return client.client.post {
            url("/transactions/withdrawals")
            setBody(withdrawal)
        }.body()
    }
    
    /**
     * Get withdrawal fees and local banks
     */
    suspend fun getWithdrawalFeesAndBanks(): LocalBanksResponse {
        return client.client.get {
            url("/transactions/withdrawal-fees-and-banks")
        }.body()
    }
    
    // =============== FINANCE ENDPOINTS ===============
    
    /**
     * Get bank details
     */
    suspend fun getBankDetails(): EftDetailsServerResponse {
        return client.client.get {
            url("/transactions/eft-bank-details")
        }.body()
    }
    
    /**
     * Setup debit order
     */
    suspend fun setupDebitOrder(debitOrderBody: DebitOrderBody): JsonObject {
        return client.client.post {
            url("/transactions/debit-orders")
            setBody(debitOrderBody)
        }.body()
    }
    
    /**
     * Get local banks
     */
    suspend fun getLocalBanks(): BanksResponse {
        return try {
            val response = client.client.get {
                url("/transactions/local-banks")
            }
            val body = response.body<BanksResponse>()
            body
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Get withdrawal fees
     */
    suspend fun getWithdrawalFees(): WithdrawalFeesResponse {
        return client.client.get {
            url("/transactions/withdrawal-fees")
        }.body()
    }
    
    /**
     * Get wallet overview
     */
    suspend fun getWalletOverview(): WalletOverview {
        return client.client.get {
            url("/transactions/statements/balance")
        }.body()
    }

    /**
     * Get my statements (wallet transactions)
     */
    suspend fun getMyStatements(): StatementsResponse {
        return client.client.get {
            url("/transactions/statements/my-statements")
        }.body()
    }

    /**
     * Download my statement PDF for a user
     * Returns the raw PDF bytes
     */
    suspend fun downloadStatementPdf(userId: String): ByteArray {
        val response = client.client.get {
            // matches the React path: `transactions/statements/export/${currentUser._id}`
            url("/transactions/statements/export/$userId")
        }
        return response.readBytes()
    }

    /**
     * Download Tax Certificate for a specific year
     * Returns the raw PDF bytes
     */
    suspend fun downloadTaxCertificate(year: Int): ByteArray {
        val response = client.client.get {
            url("/certificates/tax")
            parameter("year", year)
        }
        return response.readBytes()
    }
    
    /**
     * Get my assets
     */
    suspend fun getMyAssets(allocated: Boolean? = null): GetAssetsResponse {
        return client.client.get {
            url("/transactions/orders/my-items")
            allocated?.let { parameter("allocated", it) }
        }.body()
    }
    
    // =============== NEWS ENDPOINTS ===============
    
    /**
     * Get news feed
     */
    suspend fun getNewsFeed(page: Int = 1, limit: Int = 10): NewsFeedResponse {
        return client.client.get {
            url("/news")
            parameter("limit", limit)
            parameter("page", page)
        }.body()
    }
    
    /**
     * Toggle like on news item
     */
    suspend fun toggleNewsLike(newsId: String): JsonObject {
        return client.client.post {
            url("/news/$newsId/like")
        }.body()
    }
    
    // =============== KYC ENDPOINTS ===============
    
    /**
     * Upload KYC documents
     */
    suspend fun uploadKycDocuments(
        governmentIdBytes: ByteArray,
        proofOfAddressBytes: ByteArray,
        selfieBytes: ByteArray,
        governmentIdFileName: String,
        proofOfAddressFileName: String,
        selfieFileName: String
    ): KycUploadResponse {
        return try {

            val govtIdMimeType = when {
                governmentIdFileName.lowercase().endsWith(".jpg") || governmentIdFileName.lowercase().endsWith(".jpeg") -> "image/jpeg"
                governmentIdFileName.lowercase().endsWith(".png") -> "image/png"
                governmentIdFileName.lowercase().endsWith(".webp") -> "image/webp"
                else -> "image/jpeg" // Default to JPEG
            }
            
            val proofOfAddressMimeType = when {
                proofOfAddressFileName.lowercase().endsWith(".jpg") || proofOfAddressFileName.lowercase().endsWith(".jpeg") -> "image/jpeg"
                proofOfAddressFileName.lowercase().endsWith(".png") -> "image/png"
                proofOfAddressFileName.lowercase().endsWith(".webp") -> "image/webp"
                else -> "image/jpeg" // Default to JPEG
            }
            
            val selfieMimeType = when {
                selfieFileName.lowercase().endsWith(".jpg") || selfieFileName.lowercase().endsWith(".jpeg") -> "image/jpeg"
                selfieFileName.lowercase().endsWith(".png") -> "image/png"
                selfieFileName.lowercase().endsWith(".webp") -> "image/webp"
                else -> "image/jpeg" // Default to JPEG
            }
            

            val response = client.client.submitFormWithBinaryData(
                url = "/kyc/upload-documents",
                formData = formData {
                    append("govtId", governmentIdBytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=$governmentIdFileName")
                        append(HttpHeaders.ContentType, govtIdMimeType)
                    })
                    append("proofOfAddress", proofOfAddressBytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=$proofOfAddressFileName")
                        append(HttpHeaders.ContentType, proofOfAddressMimeType)
                    })
                    append("selfie", selfieBytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=$selfieFileName")
                        append(HttpHeaders.ContentType, selfieMimeType)
                    })
                }
            )
            
           val responseBody: KycUploadResponse = response.body()
            responseBody
        } catch (e: Exception) {
            throw e
        }
    }
    
    /**
     * Get KYC status
     */
    suspend fun getKycStatus(): KycStatusResponse {
        return client.client.get {
            url("/kyc/status")
        }.body()
    }
}
