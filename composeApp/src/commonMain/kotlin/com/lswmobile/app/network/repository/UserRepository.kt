package com.lswmobile.app.network.repository

import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.UpdateProfileBody
import com.lswmobile.app.network.model.UpdateProfileResponse
import com.lswmobile.app.network.model.UserOverviewResponse
import com.lswmobile.app.network.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.JsonObject

/**
 * Repository for user-related operations
 */
class UserRepository(private val api: LivestockWealthApi) {
    
    // StateFlow to observe user profile state
    private val _userState = MutableStateFlow<UserState>(UserState.Idle)
    val userState: Flow<UserState> = _userState.asStateFlow()
    
    /**
     * Get current user profile
     */
    suspend fun getCurrentUser(): Result<UserResponse> {
        return try {
            _userState.value = UserState.Loading
            val response = api.getUser()
            _userState.value = UserState.Success(response)
            Result.success(response)
        } catch (e: Exception) {
            _userState.value = UserState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    /**
     * Update user profile
     */
    suspend fun updateProfile(
        firstName: String? = null,
        lastName: String? = null,
        phoneNumber: String? = null,
        email: String? = null,
        dateOfBirth: String? = null,
        country: String? = null,
        town: String? = null,
        address: String? = null,
        zipCode: String? = null,
        gender: String? = null
    ): Result<UpdateProfileResponse> {
        return try {
            _userState.value = UserState.Loading
            val updateProfileBody = UpdateProfileBody(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                email = email,
                dateOfBirth = dateOfBirth,
                country = country,
                town = town,
                address = address,
                zipCode = zipCode,
                gender = gender
            )
            val response = api.updateProfile(updateProfileBody)
            _userState.value = UserState.Success(response.data)
            Result.success(response)
        } catch (e: Exception) {
            _userState.value = UserState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    /**
     * Update user preferences
     */
    suspend fun updatePreferences(notifications: Boolean, marketing: Boolean): Result<JsonObject> {
        return try {
            val preferencesJson = JsonObject(
                mapOf(
                    "notifications" to kotlinx.serialization.json.JsonPrimitive(notifications),
                    "marketing" to kotlinx.serialization.json.JsonPrimitive(marketing)
                )
            )
            val response = api.updatePreference(preferencesJson)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get user overview (investments, earnings, etc.)
     */
    suspend fun getUserOverview(): Result<UserOverviewResponse> {
        return try {
            val response = api.getUserOverview()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Upload user avatar
     */
    suspend fun uploadAvatar(fileBytes: ByteArray, fileName: String): Result<ByteArray> {
        return try {
            val response = api.uploadAvatar(fileBytes, fileName)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * User state sealed class
 */
sealed class UserState {
    object Idle : UserState()
    object Loading : UserState()
    data class Success(val user: UserResponse) : UserState()
    data class Error(val message: String) : UserState()
}
