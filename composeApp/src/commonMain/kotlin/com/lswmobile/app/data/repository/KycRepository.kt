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
            println("KycRepository: Uploading KYC documents")
            val response = api.uploadKycDocuments(
                governmentIdBytes = governmentIdBytes,
                proofOfAddressBytes = proofOfAddressBytes,
                selfieBytes = selfieBytes,
                governmentIdFileName = governmentIdFileName,
                proofOfAddressFileName = proofOfAddressFileName,
                selfieFileName = selfieFileName
            )
            println("KycRepository: KYC documents uploaded successfully")
            Result.success(response)
        } catch (e: Exception) {
            println("KycRepository: Error uploading KYC documents: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Get KYC status
     */
    suspend fun getKycStatus(): Result<KycStatusResponse> {
        return try {
            println("KycRepository: Fetching KYC status")
            val response = api.getKycStatus()
            println("KycRepository: KYC status fetched successfully")
            Result.success(response)
        } catch (e: Exception) {
            println("KycRepository: Error fetching KYC status: ${e.message}")
            Result.failure(e)
        }
    }
} 