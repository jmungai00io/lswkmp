package com.lswmobile.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lswmobile.app.data.repository.UserRepository
import com.lswmobile.app.network.model.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing user data and operations
 */
class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    
    // User state
    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> = _user.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // KYC verification state
    private val _isKYCVerified = MutableStateFlow(false)
    val isKYCVerified: StateFlow<Boolean> = _isKYCVerified.asStateFlow()
    
    init {
        // Observe user changes from repository
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                _user.value = user
                _isKYCVerified.value = user?.kycVerification?.status=="VERIFIED"
            }
        }
    }
    
    /**
     * Fetch user data from API
     */
    fun fetchUser() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                println("UserViewModel: Fetching user data")
                val result = userRepository.fetchUser()
                
                result.fold(
                    onSuccess = { user ->
                        println("UserViewModel: User fetched successfully")
                        _user.value = user
                        _isKYCVerified.value = user.kycVerification?.status=="VERIFIED"
                    },
                    onFailure = { exception ->
                        println("UserViewModel: Error fetching user: ${exception.message}")
                        _error.value = exception.message ?: "Failed to fetch user data"
                    }
                )
            } catch (e: Exception) {
                println("UserViewModel: Unexpected error: ${e.message}")
                _error.value = e.message ?: "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update user data locally
     */
    fun updateUser(user: UserResponse) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(user)
                _user.value = user
                _isKYCVerified.value = user.kycVerification?.status=="VERIFIED"
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update user data"
            }
        }
    }
    
    /**
     * Clear user data (for logout)
     */
    fun clearUser() {
        viewModelScope.launch {
            try {
                userRepository.clearUser()
                _user.value = null
                _isKYCVerified.value = false
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to clear user data"
            }
        }
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }
}
