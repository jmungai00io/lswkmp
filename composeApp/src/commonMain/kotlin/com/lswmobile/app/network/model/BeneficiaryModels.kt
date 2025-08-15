package com.lswmobile.app.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Beneficiary model
 */
@Serializable
data class Beneficiary(
    @SerialName("_id")
    val _id: String,
    var email: String,
    var firstName: String,
    var lastName: String,
    var phoneNumber: String,
    var ID: IdClass,
    val createdAt: String="",
    val updatedAt: String=""
)

@Serializable
enum class IdType {
    NATIONAL_ID,
    PASSPORT
}

@Serializable
data class IdClass(
    var type: IdType,
    var value: String
)

@Serializable
data class AddBeneficiaryResponse(
    val success: Boolean,
    val data: Beneficiary
)

/**
 * My beneficiaries response
 */
@Serializable
data class MyBeneficiariesResponse(
    val success: Boolean,
    val data: List<Beneficiary>,
    val totalPages: Int,
    val totalCount: Int,
    val currentPage: Int
)
