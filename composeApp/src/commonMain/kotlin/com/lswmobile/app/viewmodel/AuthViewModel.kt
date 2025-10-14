package com.lswmobile.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lswmobile.app.auth.OtpFlowType
import com.lswmobile.app.data.repository.UserRepository
import com.lswmobile.app.network.repository.AuthRepository
import com.lswmobile.app.network.repository.LoginState
import com.lswmobile.app.network.repository.RegistrationState
import com.lswmobile.app.utils.ErrorUtils
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
    
    val loginState: Flow<LoginState> = authRepository.loginState
    val registrationState: Flow<RegistrationState> = authRepository.registrationState
    
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    /**
     * Login user with email and password
     */
    fun login(email: String, password: String, mode: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authRepository.login(email, password, mode)

                if (authRepository.hasToken()) {
                    _uiState.value = AuthUiState.Success.Login("Login successful, OTP required")
                } else {
                    _uiState.value = AuthUiState.Idle
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(ErrorUtils.extractErrorMessage(e))
            }
        }
    }
    
    /**
     * Register a new user
     */
    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        phoneNumber: String,
        firstName: String,
        lastName: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authRepository.register(
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    phoneNumber = phoneNumber,
                    firstName = firstName,
                    lastName = lastName,
                    mode = "email"
                )
                _uiState.value = AuthUiState.Success.Registration("Registration successful")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(ErrorUtils.extractErrorMessage(e))
            }
        }
    }
    
    /**
     * Verify OTP code
     */
    fun verifyOtp(email: String, otp: String, flowType: OtpFlowType) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.verifyOtp(email, otp, flowType)
                .onSuccess {
                    _uiState.value = AuthUiState.Success.OtpVerification("OTP verified successfully")
                }
                .onFailure {
                    val exception = if (it is Exception) it else Exception(it.message, it)
                    _uiState.value = AuthUiState.Error(ErrorUtils.extractErrorMessage(exception))
                }
        }
    }

    /**
     * Resend OTP based on the flow type (login or registration)
     */
    fun resendOtp(flowType: OtpFlowType) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.resendOtp(flowType)
                .onSuccess {
                    _uiState.value = AuthUiState.Success.Generic("OTP resend requested")
                }
                .onFailure {
                    val exception = if (it is Exception) it else Exception(it.message, it)
                    _uiState.value = AuthUiState.Error(ErrorUtils.extractErrorMessage(exception))
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
                    _uiState.value = AuthUiState.Success.Logout("Logged out successfully")
                }
                .onFailure {
                    val exception = if (it is Exception) it else Exception(it.message, it)
                    _uiState.value = AuthUiState.Error(ErrorUtils.extractErrorMessage(exception))
                }
        }
    }

    /**
     * Reset UI state to idle after handling an auth event
     */
    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
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
                    val exception = if (it is Exception) it else Exception(it.message, it)
                    _uiState.value = AuthUiState.Error(ErrorUtils.extractErrorMessage(exception))
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
        class Logout(message: String) : Success(message)
    }
    data class Error(val message: String) : AuthUiState()
}
