package com.lswmobile.app.network.repository

import com.lswmobile.app.network.LivestockWealthApi
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
    
    /**
     * Login user
     */
    suspend fun login(email: String, password: String, mode: String) {
        try {
            println("AuthRepository.login: Starting login process with email: $email, mode: $mode")
            _loginState.value = LoginState.Loading
            
            // Store credentials for later use in OTP verification
            tempEmail = email
            tempPassword = password
            
            val response = api.loginUser(LoginBody(email, password), mode)
            println("AuthRepository.login: Login successful, response received")
            
            // Extract and store token from response
            if (response.containsKey("token")) {
                tempToken = response["token"]?.jsonPrimitive?.content

                println("Token extracted: ${tempToken?.take(10)}...")
            } else {
                println("No token found in response")
            }
            
            _loginState.value = LoginState.Success(response)
        } catch (e: Exception) {
            println("AuthRepository.login: Error during login: ${e.message}")
            e.printStackTrace()
            _loginState.value = LoginState.Error(e.message ?: "Unknown error")
        }
    }
    
    /**
     * Register user
     */
    suspend fun register(
        email: String,
        password: String,
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
            
            val response = api.preRegister(
                PreRegisterBody(email, password, phoneNumber, firstName, lastName),
                mode
            )
            
            // Extract and store token from response
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
    suspend fun verifyOtp(email: String, otp: String, endpoint: String): Result<JsonObject> {
        return try {
            // Use stored credentials if available, otherwise use the provided email
            val emailToUse = tempEmail ?: email
            
            val response = api.sendOtp(endpoint, SendOTPBody(
                email = emailToUse,
                firstName = null,
                lastName = null,
                phoneNumber = null,
                password = tempPassword,
                token = tempToken,
                code = otp,
                otp = otp,
                isWhatsApp = false,
                bioToken = null
            ))
            
            // Clear temporary credentials after successful verification
            if (response.containsKey("token") && response["token"] != null) {
                val finalToken = response["token"]?.jsonPrimitive?.content
                tokenProvider.saveTokens(finalToken ?: "", "")
                
                // Clear temp storage
                tempEmail = null
                tempPassword = null
                tempToken = null
            }
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Refresh token
     */
    suspend fun refreshToken(): Result<RefreshTokenPayload> {
        return try {
            val response = api.refreshToken()
            // Save the new tokens
            tokenProvider.saveTokens(response.accessToken, response.refreshToken)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Logout
     */
    suspend fun logout(): Result<JsonObject> {
        return try {
            val response = api.logOutUser()
            // Clear tokens
            tokenProvider.clearTokens()
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
