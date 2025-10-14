package com.lswmobile.app.network.repository

import com.lswmobile.app.AppInitializer
import com.lswmobile.app.auth.OtpFlowType
import com.lswmobile.app.config.AppConfigFactory
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.RefreshableTokenProvider
import com.lswmobile.app.network.SessionManager
import com.lswmobile.app.network.TokenProvider
import com.lswmobile.app.network.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * Repository for authentication related operations
 */
class AuthRepository(
    private val api: LivestockWealthApi,
    private val tokenProvider: TokenProvider
) {
    private companion object {
        private const val LOGIN_VERIFY_ENDPOINT = "/auth"
    }

    private fun isLikelyJwt(token: String): Boolean {
        // Basic sanity check: JWT typically has 3 dot-separated parts
        return token.count { it == '.' } == 2 && token.length > 20
    }

    private fun sanitizeAccessToken(raw: String): String {
        return raw.trim()
            .removePrefix("Bearer ")
            .removePrefix("bearer ")
            .trim()
    }

    init {
        // Wire refresh delegate if using SimpleTokenProvider so Ktor Auth can refresh using cookies
        (tokenProvider as? RefreshableTokenProvider)?.setRefreshDelegate { _ ->
            // Call refresh endpoint: server sets new httpOnly cookie and returns { token }
            val response = api.refreshToken()
            val tokenElement = response["token"]
            val parsed = tokenElement?.jsonPrimitive?.content
            val newAccessToken = parsed?.let { sanitizeAccessToken(it) }
            val dotCount = newAccessToken?.count { it == '.' } ?: -1

            if (newAccessToken != null && newAccessToken.isNotBlank() && newAccessToken != "false" && isLikelyJwt(newAccessToken)) {
                // Return pair: accessToken and empty refresh token string (cookie carries refresh)
                SessionManager.notifySuccess()
                Pair(newAccessToken, tokenProvider.getRefreshToken() ?: "")
            } else {
                SessionManager.notifyUnauthorized()
                null
            }
        }
    }
    // StateFlow to observe login state
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: Flow<LoginState> = _loginState.asStateFlow()
    
    // StateFlow to observe registration state
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: Flow<RegistrationState> = _registrationState.asStateFlow()
    
    // Store temporary credentials during the auth flow
    private var tempEmail: String? = null
    private var tempPassword: String? = null
    private var tempToken: String? = null
    private var tempConfirmPassword: String? = null
    private var tempFirstName: String? = null
    private var tempLastName: String? = null
    private var tempPhoneNumber: String? = null
    private var tempOtpMode: String? = null
    
    /**
     * Login user
     */
    suspend fun login(email: String, password: String, mode: String) {
        try {
            _loginState.value = LoginState.Loading
            
            // Store credentials for later use in OTP verification
            tempEmail = email
            tempPassword = password
            tempOtpMode = mode
            
            val response = api.loginUser(LoginBody(email, password), mode)

            // Extract and store token from response
            if (response.containsKey("token")) {
                tempToken = response["token"]?.jsonPrimitive?.content

            } else {
                null
            }
            
            _loginState.value = LoginState.Success(response)
        } catch (e: Exception) {
            _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            throw e
        }
    }
    
    /**
     * Register user
     */
    suspend fun register(
        email: String,
        password: String,
        confirmPassword: String,
        phoneNumber: String,
        firstName: String,
        lastName: String,
        mode: String
    ) {
        try {
            _registrationState.value = RegistrationState.Loading
            
            // Store credentials for later use in OTP verification
            tempEmail = email
            tempPassword = password
            tempConfirmPassword = confirmPassword
            tempFirstName = firstName
            tempLastName = lastName
            tempPhoneNumber = phoneNumber
            tempOtpMode = mode
            
            val response = api.preRegister(
                PreRegisterBody(
                    email = email,
                    phoneNumber = phoneNumber,
                    firstName = firstName,
                    lastName = lastName
                ),
                mode
            )
            if (response.containsKey("token")) {
                tempToken = response["token"]?.jsonPrimitive?.content
            }
            
            _registrationState.value = RegistrationState.Success(response)
        } catch (e: Exception) {
            _registrationState.value = RegistrationState.Error(e.message ?: "Unknown error")
        }
    }
    
    /**
     * Verify OTP
     */
    suspend fun verifyOtp(email: String, otp: String, flowType: OtpFlowType): Result<JsonObject> {
        return try {
            // Use stored credentials if available, otherwise use the provided email
            val emailToUse = tempEmail ?: email
            val response = when (flowType) {
                OtpFlowType.LOGIN -> api.sendOtp(
                    LOGIN_VERIFY_ENDPOINT,
                    SendOTPBody(
                        email = emailToUse,
                        password = tempPassword,
                        token = tempToken,
                        code = otp,
                        otp = otp,
                        isWhatsApp = false
                    )
                )

                OtpFlowType.REGISTER -> {
                    val registerBody = createRegisterBody(emailToUse, otp)
                    api.registerUser(registerBody)
                }
            }

            response["token"]?.jsonPrimitive?.content?.let { finalToken ->
                if (finalToken.isNotBlank()) {
                    tokenProvider.saveTokens(finalToken, "")
                    AppInitializer.reinitializeNetworkClients()
                }
            }

            clearTempAuthState()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resendOtp(flowType: OtpFlowType): Result<Unit> {
        return try {
            when (flowType) {
                OtpFlowType.LOGIN -> {
                    val email = tempEmail ?: throw IllegalStateException("Email missing for login OTP resend")
                    val password = tempPassword ?: throw IllegalStateException("Password missing for login OTP resend")
                    val mode = tempOtpMode ?: "sms"
                    api.loginUser(LoginBody(email, password), mode)
                }

                OtpFlowType.REGISTER -> {
                    val email = tempEmail ?: throw IllegalStateException("Email missing for registration OTP resend")
                    val phone = tempPhoneNumber ?: throw IllegalStateException("Phone number missing for registration OTP resend")
                    val first = tempFirstName ?: throw IllegalStateException("First name missing for registration OTP resend")
                    val last = tempLastName ?: throw IllegalStateException("Last name missing for registration OTP resend")
                    val mode = tempOtpMode ?: "email"
                    api.preRegister(
                        PreRegisterBody(
                            email = email,
                            phoneNumber = phone,
                            firstName = first,
                            lastName = last
                        ),
                        mode
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createRegisterBody(email: String, otp: String): RegisterBody {
        val phone = tempPhoneNumber
        val first = tempFirstName
        val last = tempLastName
        val password = tempPassword
        val confirm = tempConfirmPassword
        val token = tempToken

        if (phone == null || first == null || last == null || password == null || confirm == null || token == null) {
            throw IllegalStateException("Missing registration context for OTP verification")
        }

        return RegisterBody(
            email = email,
            phoneNumber = phone,
            firstName = first,
            lastName = last,
            password = password,
            confirmPassword = confirm,
            otp = otp,
            code = otp,
            token = token
        )
    }

    private fun clearTempAuthState() {
        tempEmail = null
        tempPassword = null
        tempConfirmPassword = null
        tempFirstName = null
        tempLastName = null
        tempPhoneNumber = null
        tempToken = null
        tempOtpMode = null
    }
    
    /**
     * Refresh token
     */
    suspend fun refreshToken(): Result<JsonObject> {
        return try {
            val response = api.refreshToken()
            // Extract access token from body and save; refresh token is rotated in httpOnly cookie
            val tokenElement = response["token"]
            val parsed = tokenElement?.jsonPrimitive?.content
            val accessToken = parsed?.let { sanitizeAccessToken(it) }
            val dotCount = accessToken?.count { it == '.' } ?: -1
            if (accessToken != null && accessToken.isNotBlank() && accessToken != "false" && isLikelyJwt(accessToken)) {
                tokenProvider.saveTokens(accessToken, tokenProvider.getRefreshToken() ?: "")
                SessionManager.notifySuccess()
                Result.success(response)
            } else {
                SessionManager.notifyUnauthorized()
                Result.failure(IllegalStateException("Invalid access token from refresh"))
            }
        } catch (e: Exception) {
            SessionManager.notifyUnauthorized()
            Result.failure(e)
        }
    }
    
    /**
     * Logout
     */
    suspend fun logout(): Result<Unit> {
        return try {
            val response = api.logOutUser()
            // Clear tokens
            tokenProvider.clearTokens()
            // Clear refresh cookies stored on client
            AppInitializer.clearCookies()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Reset password
     */
    suspend fun resetPassword(email: String): Result<JsonObject> {
        return try {
            val requestBody = JsonObject(mapOf("email" to JsonPrimitive(email)))
            val response = api.resetPassword(requestBody)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Passkey (WebAuthn) methods
    
    /**
     * Get registration options
     */
    suspend fun getRegistrationOptions(platform: String): Result<RegistrationOptionsResponse> {
        return try {
            val response = api.getRegistrationOptions(platform)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Verify registration
     */
    suspend fun verifyRegistration(request: VerifyRegistrationRequest): Result<JsonObject> {
        return try {
            val response = api.verifyRegistration(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get authenticators
     */
    suspend fun getAuthenticators(): Result<List<AuthenticatorResponse>> {
        return try {
            val response = api.getAuthenticators()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete authenticator
     */
    suspend fun deleteAuthenticator(credentialId: String): Result<JsonObject> {
        return try {
            val response = api.deleteAuthenticator(credentialId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get passkey login options
     */
    suspend fun getPasskeyLoginOptions(platform: String): Result<PasskeyLoginOptionsResponse> {
        return try {
            val response = api.getPasskeyLoginOptions(platform)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Verify passkey login
     */
    suspend fun verifyPasskeyLogin(request: PasskeyLoginVerificationRequest): Result<JsonObject> {
        return try {
            val response = api.verifyPasskeyLogin(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if we have a temporary token from login/registration
     * Used to determine if we should navigate to OTP verification
     */
    fun hasToken(): Boolean {
        return tempToken != null
    }
}

/**
 * Login state sealed class
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val data: JsonObject) : LoginState()
    data class Error(val message: String) : LoginState()
}

/**
 * Registration state sealed class
 */
sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val data: JsonObject) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
