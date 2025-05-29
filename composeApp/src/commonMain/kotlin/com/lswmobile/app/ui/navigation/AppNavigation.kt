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
        println("Navigation: Current route changed to $currentRoute")
    }

    // Check authentication state
    val uiState by authViewModel.uiState.collectAsState(initial = null)
    
    when (currentRoute) {
        AppRoute.LOGIN -> {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToOtp = { userEmail ->
                    email = userEmail
                    println("Navigation: Changing route from LOGIN to OTP_VERIFICATION")
                    currentRoute = AppRoute.OTP_VERIFICATION
                },
                onNavigateToRegister = {
                    println("Navigation: Changing route from LOGIN to REGISTER")
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
                    println("Navigation: Changing route from OTP_VERIFICATION to MAIN")
                    currentRoute = AppRoute.MAIN
                }
            )
        }
        
        AppRoute.MAIN -> {
            MainAppContainer()
        }
        
        AppRoute.REGISTER -> {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToOtp = { userEmail ->
                    email = userEmail
                    println("Navigation: Changing route from REGISTER to OTP_VERIFICATION")
                    currentRoute = AppRoute.OTP_VERIFICATION
                },
                onNavigateToLogin = {
                    println("Navigation: Changing route from REGISTER to LOGIN")
                    currentRoute = AppRoute.LOGIN
                }
            )
        }
        
        AppRoute.FORGOT_PASSWORD -> {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    println("Navigation: Changing route from FORGOT_PASSWORD to LOGIN")
                    currentRoute = AppRoute.LOGIN
                }
            )
        }
    }
}

/**
 * Main container for the app after authentication
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun MainAppContainer() {
    // A simple dashboard to show successful authentication
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo - using the proper resource loading pattern
            Image(
                painter = ResourceHelper.loadShortLogo(),
                contentDescription = "Livestock Wealth Logo",
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Welcome to Livestock Wealth",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "You have successfully logged in!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "This is where your main app content will go.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
