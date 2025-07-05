package com.lswmobile.app.data.repository

import com.lswmobile.app.network.model.UserResponse
import kotlinx.coroutines.flow.Flow

/**
 * Interface for user repository operations
 * This allows for different implementations (SQLDelight, in-memory, etc.)
 */
interface UserRepository {
    /**
     * Get current user data
     */
    val currentUser: Flow<UserResponse?>
    
    /**
     * Fetch user from API and store locally
     */
    suspend fun fetchUser(): Result<UserResponse>
    
    /**
     * Update user data locally
     */
    suspend fun updateUser(user: UserResponse)
    
    /**
     * Clear user data (for logout)
     */
    suspend fun clearUser()
    
    /**
     * Check if user is KYC verified
     */
    fun isUserKYCVerified(): Flow<Boolean>
} 