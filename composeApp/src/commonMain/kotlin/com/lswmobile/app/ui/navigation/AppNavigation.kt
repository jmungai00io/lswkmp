package com.lswmobile.app.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.ui.resources.ResourceHelper
import com.lswmobile.app.ui.screens.ForgotPasswordScreen
import com.lswmobile.app.ui.screens.LoginScreen
import com.lswmobile.app.ui.screens.OtpVerificationScreen
import com.lswmobile.app.ui.screens.RegisterScreen
import com.lswmobile.app.viewmodel.AuthUiState
import com.lswmobile.app.viewmodel.AuthViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi

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

    // Check authentication state
    val uiState by authViewModel.uiState.collectAsState(initial = null)
    
    // Handle global authentication state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success.OtpVerification -> {
                currentRoute = AppRoute.MAIN
            }
            is AuthUiState.Success.Logout -> {
                currentRoute = AppRoute.LOGIN
                authViewModel.resetUiState()
            }
            else -> { /* No action for other states */ }
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
            MainAppContainer(authViewModel = authViewModel)
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
