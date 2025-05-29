package com.lswmobile.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lswmobile.app.network.model.UserResponse
import com.lswmobile.app.network.repository.UserRepository
import com.lswmobile.app.network.repository.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for user-related operations
 */
class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    
    // StateFlow from the repository
    val userState: Flow<UserState> = userRepository.userState
    
    // Internal state for UI
    private val _uiState = MutableStateFlow<UserVMUiState>(UserVMUiState.Idle)
    val uiState: StateFlow<UserVMUiState> = _uiState.asStateFlow()
    
    // User data
    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> = _user.asStateFlow()
    
    /**
     * Load current user profile
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = UserVMUiState.Loading
            userRepository.getCurrentUser()
                .onSuccess { 
                    _user.value = it
                    _uiState.value = UserVMUiState.Success.Profile("Profile loaded successfully")
                }
                .onFailure { 
                    _uiState.value = UserVMUiState.Error(it.message ?: "Failed to load user profile")
                }
        }
    }
    
    /**
     * Update user profile
     */
    fun updateProfile(
        firstName: String? = null,
        lastName: String? = null,
        phoneNumber: String? = null,
        email: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = UserVMUiState.Loading
            userRepository.updateProfile(firstName, lastName, phoneNumber, email)
                .onSuccess { 
                    _user.value = it.user
                    _uiState.value = UserVMUiState.Success.Profile(it.message)
                }
                .onFailure { 
                    _uiState.value = UserVMUiState.Error(it.message ?: "Failed to update profile")
                }
        }
    }
    
    /**
     * Update user preferences
     */
    fun updatePreferences(notifications: Boolean, marketing: Boolean) {
        viewModelScope.launch {
            _uiState.value = UserVMUiState.Loading
            userRepository.updatePreferences(notifications, marketing)
                .onSuccess { 
                    _uiState.value = UserVMUiState.Success.Preferences("Preferences updated successfully")
                    // Reload user profile to get updated preferences
                    loadUserProfile()
                }
                .onFailure { 
                    _uiState.value = UserVMUiState.Error(it.message ?: "Failed to update preferences")
                }
        }
    }
    
    /**
     * Get user investment overview
     */
    fun loadUserOverview() {
        viewModelScope.launch {
            _uiState.value = UserVMUiState.Loading
            userRepository.getUserOverview()
                .onSuccess { 
                    _uiState.value = UserVMUiState.Success.Overview("Overview loaded successfully")
                }
                .onFailure { 
                    _uiState.value = UserVMUiState.Error(it.message ?: "Failed to load overview")
                }
        }
    }
}

/**
 * UI state for user-related operations
 */
sealed class UserVMUiState {
    object Idle : UserVMUiState()
    object Loading : UserVMUiState()
    
    // Different types of success states
    sealed class Success(val message: String) : UserVMUiState() {
        class Profile(message: String) : Success(message)
        class Preferences(message: String) : Success(message)
        class Overview(message: String) : Success(message)
        class Generic(message: String) : Success(message)
    }
    
    data class Error(val message: String) : UserVMUiState()
}
