package com.lswmobile.app.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Login request body
 */
@Serializable
data class LoginBody(
    val email: String,
    val password: String
)

/**
 * Send OTP request body
 */
@Serializable
data class SendOTPBody(
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var phoneNumber: String? = null,
    var password: String? = null,
    var token: String? = null,
    var code: String? = null,
    var otp: String? = null,
    var isWhatsApp: Boolean = false,
    var bioToken: String? = null
)

/**
 * Pre-register request body
 */
@Serializable
data class PreRegisterBody(
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val password: String
)

/**
 * Register request body
 */
@Serializable
data class RegisterBody(
    val email: String,
    val otp: String
)

/**
 * Refresh token response
 */
@Serializable
data class RefreshTokenPayload(
    val accessToken: String,
    val refreshToken: String
)

/**
 * Passkey registration options response
 */
@Serializable
data class RegistrationOptionsResponse(
    val rp: Map<String, JsonElement>? = null,
    val user: Map<String, JsonElement>? = null,
    val challenge: String? = null,
    @SerialName("pubKeyCredParams")
    val pubKeyCredParams: List<Map<String, JsonElement>>? = null,
    val timeout: Long? = null,
    val attestation: String? = null,
    @SerialName("excludeCredentials")
    val excludeCredentials: List<Map<String, JsonElement>>? = null
)

/**
 * Verify registration request
 */
@Serializable
data class VerifyRegistrationRequest(
    @SerialName("attestationResponse")
    val attestationResponse: AttestationResponse,
    @SerialName("deviceName")
    val deviceName: String,
    @SerialName("platform")
    val platform: String = "android"
)

/**
 * Attestation response
 */
@Serializable
data class AttestationResponse(
    @SerialName("id") 
    val id: String,
    @SerialName("rawId") 
    val rawId: String,
    @SerialName("response") 
    val response: AuthenticatorAttestationResponseData,
    @SerialName("type") 
    val type: String = "public-key",
    @SerialName("clientExtensionResults") 
    val clientExtensionResults: Map<String, JsonElement> = emptyMap(),
    @SerialName("transports") 
    val transports: List<String>? = null
)

/**
 * Authenticator attestation response data
 */
@Serializable
data class AuthenticatorAttestationResponseData(
    @SerialName("clientDataJSON") 
    val clientDataJSON: String,
    @SerialName("attestationObject") 
    val attestationObject: String
)

/**
 * Authenticator response
 */
@Serializable
data class AuthenticatorResponse(
    @SerialName("credentialID") 
    val credentialID: String,
    @SerialName("deviceName") 
    val deviceName: String? = null,
    @SerialName("createdAt") 
    val createdAt: String,
    @SerialName("lastUsedAt") 
    val lastUsedAt: String? = null
)

/**
 * Passkey login options response
 */
@Serializable
data class PasskeyLoginOptionsResponse(
    @SerialName("challengeId") 
    val challengeId: String,
    @SerialName("challenge") 
    val challenge: String? = null,
    @SerialName("timeout") 
    val timeout: Long? = null,
    @SerialName("rpId") 
    val rpId: String? = null,
    @SerialName("allowCredentials") 
    val allowCredentials: List<Map<String, JsonElement>>? = null,
    @SerialName("userVerification") 
    val userVerification: String? = null
)

/**
 * Passkey assertion response
 */
@Serializable
data class PasskeyAssertionResponse(
    @SerialName("id") 
    val id: String,
    @SerialName("rawId") 
    val rawId: String,
    @SerialName("type") 
    val type: String,
    @SerialName("response") 
    val response: Map<String, JsonElement>,
    @SerialName("clientExtensionResults") 
    val clientExtensionResults: Map<String, JsonElement> = emptyMap(),
    @SerialName("transports") 
    val transports: List<String>? = null
)

/**
 * Passkey login verification request
 */
@Serializable
data class PasskeyLoginVerificationRequest(
    @SerialName("assertionResponse") 
    val assertionResponse: PasskeyAssertionResponse,
    @SerialName("challengeId") 
    val challengeId: String,
    @SerialName("platform") 
    val platform: String = "android"
)

/**
 * Token refresh state
 */
sealed class TokenRefreshState {
    data object Idle : TokenRefreshState()
    data object Success : TokenRefreshState()
    data class Error(val message: String) : TokenRefreshState()
    data object Unauthorized : TokenRefreshState()
}
