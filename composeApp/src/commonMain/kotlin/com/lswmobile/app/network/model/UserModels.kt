package com.lswmobile.app.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User response model
 */
@Serializable
data class UserResponse(
    @SerialName("_id")
    val _id: String,
    val email: String,
    val customerRef: String?=null,
    val isKYCed: Boolean?=false,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val enabled: Boolean,
    val role: String,
    val dateCreated: String,
    @SerialName("__v")
    val __v: Int,
    val dateVerified: String?="",
    val lastLoginDate: String?="",
    val customerId: Int,
    @SerialName("_preferences")
    val _preferences: Preferences? = null,
    val kycVerification: KycVerification? = null,
    val updatedAt: String,
    val kycDocumentsSubmissionDate: String? = "",
    val lastLoginLocation: String,
    val avatarUrl: String? = "",
    val dateOfBirth: String? = "",
    val gender: String? = "",
    val country: String? = "",
    val town: String? = "",
    val address: String? = "",
    val zipCode: String? = "",
    val province: String? = null,
    val ID: IdDocument? = null,
    var getEnabled: Boolean = false
) {
    @Serializable
    data class Preferences(
        var shouldAutoReinvest: Boolean,
        var shouldReceiveSmsNotifications: Boolean
    )

    @Serializable
    data class IdDocument(
        var type: String,
        var value: String
    )

    @Serializable
    data class KycVerification(
        val status: String = "",
    )
}

/**
 * Update profile request body
 */
@Serializable
data class UpdateProfileBody(
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val country: String? = null,
    val town: String? = null,
    val zipCode: String? = null,
    val gender: String? = null,
    val ID: UserResponse.IdDocument? = null,
    val address: String? = null
)

/**
 * Update profile response
 */
@Serializable
data class UpdateProfileResponse(
    val success: Boolean=false,
    val message: String="",
    val data: UserResponse
)

/**
 * User overview response
 */
@Serializable
data class UserOverviewResponse(
    val investments: List<Investment>,
    val totalInvestment: Double,
    val earnings: Double,
    val message: String
)

/**
 * Investment model
 */
@Serializable
data class Investment(
    val id: String,
    val productId: String,
    val productName: String,
    val amount: Double,
    val date: String,
    val earnings: Double,
    val status: String
)
