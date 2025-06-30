package com.lswmobile.app.data.sample

import com.lswmobile.app.network.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Sample repository for profile data with hardcoded user information
 * Will be replaced with actual API integration later
 */
class SampleProfileRepository {
    
    private val _userProfile = MutableStateFlow(generateSampleUserProfile())
    val userProfile: Flow<UserResponse> = _userProfile.asStateFlow()
    
    private val _kycStatus = MutableStateFlow(KycStatus(isVerified = false, completedSteps = 1, totalSteps = 3))
    val kycStatus: Flow<KycStatus> = _kycStatus.asStateFlow()
    
    /**
     * Update user profile
     */
    fun updateUserProfile(
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        phoneNumber: String? = null,
        address: String? = null,
        avatarUrl: String? = null
    ) {
        val currentProfile = _userProfile.value
        _userProfile.value = currentProfile.copy(
            firstName = firstName ?: currentProfile.firstName,
            lastName = lastName ?: currentProfile.lastName,
            email = email ?: currentProfile.email,
            avatarUrl = avatarUrl ?: currentProfile.avatarUrl
        )
    }
    
    /**
     * Update KYC status - increment completed steps
     */
    fun updateKycStatus() {
        val current = _kycStatus.value
        if (current.completedSteps < current.totalSteps) {
            _kycStatus.value = current.copy(
                completedSteps = current.completedSteps + 1,
                isVerified = current.completedSteps + 1 >= current.totalSteps
            )
        }
    }
    
    /**
     * Get KYC progress percentage
     */
    fun getKycProgressPercentage(): Float {
        val current = _kycStatus.value
        return current.completedSteps.toFloat() / current.totalSteps.toFloat()
    }
    
    /**
     * Generate sample user profile
     */
    private fun generateSampleUserProfile(): UserResponse {
        return UserResponse(
            _id = "u123456",
            email = "john.doe@example.com",
            customerRef = "CR123456",
            isKYCed = false,
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "+1234567890",
            enabled = true,
            role = "USER",
            dateCreated = "2023-01-01T00:00:00Z",
            __v = 1,
            dateVerified = "2023-01-02T00:00:00Z",
            lastLoginDate = "2023-06-01T00:00:00Z",
            customerId = 12345,
            _preferences = UserResponse.Preferences(
                shouldAutoReinvest = true,
                shouldReceiveSmsNotifications = true
            ),
            kycVerification = UserResponse.KycVerification(
                status = "PENDING"
            ),
            updatedAt = "2023-06-01T00:00:00Z",
            kycDocumentsSubmissionDate = "2023-01-03T00:00:00Z",
            lastLoginLocation = "Unknown",
            avatarUrl = "https://i.pravatar.cc/300",
            dateOfBirth = "1990-01-01",
            gender = "Male",
            country = "South Africa",
            town = "Cape Town",
            address = "123 Main Street"
        )
    }
    
    companion object {
        private var instance: SampleProfileRepository? = null
        
        /**
         * Get singleton instance of the repository
         */
        fun getInstance(): SampleProfileRepository {
            if (instance == null) {
                instance = SampleProfileRepository()
            }
            return instance!!
        }
    }
}

/**
 * KYC verification status data class
 */
data class KycStatus(
    val isVerified: Boolean,
    val completedSteps: Int,
    val totalSteps: Int
)
