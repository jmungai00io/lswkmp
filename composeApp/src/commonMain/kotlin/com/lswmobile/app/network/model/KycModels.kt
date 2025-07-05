package com.lswmobile.app.network.model

import kotlinx.serialization.Serializable

/**
 * KYC Status constants
 */
object KycStatuses {
    const val SUBMITTED_DOCUMENTS = "SUBMITTED_DOCUMENTS"
    const val VERIFIED = "VERIFIED"
    const val UNVERIFIED = "UNVERIFIED"
    const val REJECTED = "REJECTED"
}

/**
 * KYC Document types
 */
object KycDocumentTypes {
    const val GOVERNMENT_ID = "govtId"
    const val PROOF_OF_ADDRESS = "proofOfAddress"
    const val SELFIE = "selfie"
}

/**
 * KYC Upload Response
 */
@Serializable
data class KycUploadResponse(
    val success: Boolean,
    val message: String,
    val data: KycUploadData? = null
)

@Serializable
data class KycUploadData(
    val submissionId: String? = null,
    val status: String? = null
)

/**
 * KYC Status Response
 */
@Serializable
data class KycStatusResponse(
    val success: Boolean,
    val message: String,
    val data: KycStatusData? = null
)

@Serializable
data class KycStatusData(
    val status: String,
    val submissionDate: String? = null,
    val verificationDate: String? = null,
    val rejectionReason: String? = null
) 