package com.lswmobile.app.network

import com.lswmobile.app.network.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.json.Json
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
            url("/users/me")
        }.body()
    }
    
    /**
     * Update user profile
     */
    suspend fun updateProfile(updateProfileBody: UpdateProfileBody): UpdateProfileResponse {
        return client.client.patch {
            url("/users/me")
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
        return client.client.get {
            url("/products")
            category?.let { parameter("category", it) }
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }
    
    /**
     * Get product
     */
    suspend fun getProduct(productId: String): ProductFarmland {
        return client.client.get {
            url("/products/$productId")
        }.body()
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
