package com.lswmobile.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.lswmobile.app.ui.screens.ForgotPasswordScreen
import com.lswmobile.app.ui.screens.LoginScreen
import com.lswmobile.app.ui.screens.OtpVerificationScreen
import com.lswmobile.app.ui.screens.RegisterScreen
import com.lswmobile.app.viewmodel.AuthUiState
import com.lswmobile.app.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Main navigation routes
 */
enum class AppRoute {
    LOGIN,
    OTP_VERIFICATION,
    MAIN,
    REGISTER,
    FORGOT_PASSWORD
}

/**
 * App navigation component that manages the navigation flow
 */
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    startDestination: AppRoute = AppRoute.LOGIN
) {
    var currentRoute by remember { mutableStateOf(startDestination) }
    var email by remember { mutableStateOf("") }
    
    // For debugging - print the current route whenever it changes
    LaunchedEffect(currentRoute) {
    }

    // Handle global authentication state changes
    LaunchedEffect(authViewModel) {
        authViewModel.uiState.collectLatest { state ->
            when (state) {
                is AuthUiState.Success.OtpVerification -> {
                    currentRoute = AppRoute.MAIN
                }
                is AuthUiState.Success.Logout -> {
                    currentRoute = AppRoute.LOGIN
                    authViewModel.resetUiState()
                }
                else -> Unit
            }
        }
    }
    
    when (currentRoute) {
        AppRoute.LOGIN -> {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToOtp = { userEmail ->
                    email = userEmail
                    currentRoute = AppRoute.OTP_VERIFICATION
                },
                onNavigateToRegister = {
                    currentRoute = AppRoute.REGISTER
                },
                onNavigateToForgotPassword = {
                    currentRoute = AppRoute.FORGOT_PASSWORD
                }
            )
        }
        
        AppRoute.OTP_VERIFICATION -> {
            OtpVerificationScreen(
                email = email,
                authViewModel = authViewModel,
                onNavigateToHome = {
                    currentRoute = AppRoute.MAIN
                }
            )
        }
        
        AppRoute.MAIN -> {
            MainAppContainer(
                authViewModel = authViewModel,
                onLogoutNavigateToLogin = {
                    currentRoute = AppRoute.LOGIN
                }
            )
        }
        
        AppRoute.REGISTER -> {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToOtp = { userEmail ->
                    email = userEmail
                    currentRoute = AppRoute.OTP_VERIFICATION
                },
                onNavigateToLogin = {
                    currentRoute = AppRoute.LOGIN
                }
            )
        }
        
        AppRoute.FORGOT_PASSWORD -> {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    currentRoute = AppRoute.LOGIN
                }
            )
        }
    }
}
