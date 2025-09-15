package com.lswmobile.app.data.repository

import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.KycStatusResponse
import com.lswmobile.app.network.model.KycUploadResponse

/**
 * Repository for KYC-related operations
 */
class KycRepository(
    private val api: LivestockWealthApi
) {
    
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
    ): Result<KycUploadResponse> {
        return try {
            val response = api.uploadKycDocuments(
                governmentIdBytes = governmentIdBytes,
                proofOfAddressBytes = proofOfAddressBytes,
                selfieBytes = selfieBytes,
                governmentIdFileName = governmentIdFileName,
                proofOfAddressFileName = proofOfAddressFileName,
                selfieFileName = selfieFileName
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get KYC status
     */
    suspend fun getKycStatus(): Result<KycStatusResponse> {
        return try {
            val response = api.getKycStatus()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 