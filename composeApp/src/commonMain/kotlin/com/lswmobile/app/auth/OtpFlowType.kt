package com.lswmobile.app.auth

/**
 * Represents the origin of an OTP verification flow.
 */
enum class OtpFlowType {
    LOGIN,
    REGISTER
}

/**
 * Navigation data passed to the OTP screen.
 */
data class OtpNavigationState(
    val email: String,
    val flowType: OtpFlowType
)
