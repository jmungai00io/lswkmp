package com.lswmobile.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lswmobile.app.data.repository.UserRepository
import com.lswmobile.app.network.repository.AuthRepository
import com.lswmobile.app.network.repository.LoginState
import com.lswmobile.app.network.repository.RegistrationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for authentication-related operations
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository? = null
) : ViewModel() {
    
    // StateFlows from the repository
    val loginState: Flow<LoginState> = authRepository.loginState
    val registrationState: Flow<RegistrationState> = authRepository.registrationState
    
    // Internal state for UI
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    /**
     * Login user with email and password
     */
    fun login(email: String, password: String, mode: String) {
        println("AuthViewModel.login: Function called with email: $email, mode: $mode")
        viewModelScope.launch {
            println("AuthViewModel.login: Inside coroutine scope")
            _uiState.value = AuthUiState.Loading
            try {
                println("AuthViewModel.login: About to call repository")
                authRepository.login(email, password, mode)
                println("AuthViewModel.login: Repository call completed")
                
                // Check login state to decide what to do next
                // If we have a token, we can navigate to OTP verification
                if (authRepository.hasToken()) {
                    _uiState.value = AuthUiState.Success.Login("Login successful, OTP required")
                } else {
                    _uiState.value = AuthUiState.Idle
                }
            } catch (e: Exception) {
                println("AuthViewModel.login: Exception caught: ${e.message}")
                e.printStackTrace()
                _uiState.value = AuthUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Register a new user
     */
    fun register(
        email: String,
        password: String,
        phoneNumber: String,
        firstName: String,
        lastName: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authRepository.register(email, password, phoneNumber, firstName, lastName, "email")
                _uiState.value = AuthUiState.Success.Registration("Registration successful")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Verify OTP code
     */
    fun verifyOtp(email: String, otp: String, endpoint: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.verifyOtp(email, otp, endpoint)
                .onSuccess {
                    println("OTP verification successful: $it")
                    _uiState.value = AuthUiState.Success.OtpVerification("OTP verified successfully")
                }
                .onFailure {
                    println("OTP verification failed: ${it.message}")
                    _uiState.value = AuthUiState.Error(it.message ?: "Failed to verify OTP")
                }
        }
    }
    
    /**
     * Logout current user
     */
    fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.logout()
                .onSuccess {
                    // Clear user data when logging out
                    userRepository?.clearUser()
                    _uiState.value = AuthUiState.Success.Generic("Logged out successfully")
                }
                .onFailure {
                    _uiState.value = AuthUiState.Error(it.message ?: "Failed to logout")
                }
        }
    }
    
    /**
     * Reset password
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.resetPassword(email)
                .onSuccess {
                    _uiState.value = AuthUiState.Success.Generic("Password reset email sent")
                }
                .onFailure {
                    _uiState.value = AuthUiState.Error(it.message ?: "Failed to reset password")
                }
        }
    }
}

/**
 * UI state for the auth screens
 */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    
    // Different types of success states
    sealed class Success(val message: String) : AuthUiState() {
        class Login(message: String) : Success(message)
        class OtpVerification(message: String) : Success(message)
        class Registration(message: String) : Success(message)
        class Generic(message: String) : Success(message)
    }
    
    data class Error(val message: String) : AuthUiState()
}
