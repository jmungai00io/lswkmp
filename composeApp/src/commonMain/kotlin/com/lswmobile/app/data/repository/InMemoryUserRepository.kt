package com.lswmobile.app.data.repository

import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Simple in-memory user repository for storing user data locally
 * This implements the UserRepository interface but stores data in memory
 */
class InMemoryUserRepository(
    private val api: LivestockWealthApi
) : UserRepository {
    
    private val currentUserFlow = MutableStateFlow<UserResponse?>(null)
    
    override val currentUser: Flow<UserResponse?>
        get() = currentUserFlow
    
    override suspend fun fetchUser(): Result<UserResponse> {
        return try {
            val userResponse = api.getUser()

            // Store user locally
            currentUserFlow.value = userResponse
            
            Result.success(userResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateUser(user: UserResponse) {
        currentUserFlow.value = user
    }
    
    override suspend fun clearUser() {
        currentUserFlow.value = null
    }
    
    override fun isUserKYCVerified(): Flow<Boolean> {
        return currentUser.map { user ->
            user?.isKYCed ?: false
        }
    }
} 