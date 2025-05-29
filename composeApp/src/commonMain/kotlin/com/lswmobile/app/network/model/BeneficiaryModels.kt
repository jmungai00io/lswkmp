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
    val userId: String,
    val name: String,
    val bankAccountId: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Add beneficiary response
 */
@Serializable
data class AddBeneficiaryResponse(
    val beneficiary: Beneficiary,
    val message: String
)

/**
 * My beneficiaries response
 */
@Serializable
data class MyBeneficiariesResponse(
    val success: Boolean,
    val data: List<Beneficiary>
)
