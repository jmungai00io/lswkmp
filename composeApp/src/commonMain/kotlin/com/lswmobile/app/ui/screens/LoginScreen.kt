package com.lswmobile.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Star
import com.lswmobile.app.ui.components.LivestockButton
import com.lswmobile.app.ui.components.ErrorToast
import com.lswmobile.app.ui.components.LivestockPasswordField
import com.lswmobile.app.ui.components.LivestockTextField
import com.lswmobile.app.ui.resources.ResourceHelper
import com.lswmobile.app.viewmodel.AuthUiState
import com.lswmobile.app.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Login screen for the Livestock Wealth app
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToOtp: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var uiErrorMessage by remember { mutableStateOf<String?>(null) }
    
    val focusManager = LocalFocusManager.current
    
    // Local function to validate the form
    fun isFormValid(): Boolean {
        emailError = validateEmail(email)
        passwordError = validatePassword(password)
        return emailError == null && passwordError == null && email.isNotBlank() && password.isNotBlank()
    }
    
    // Collect UI state
    LaunchedEffect(Unit) {
        authViewModel.uiState.collectLatest { state ->
            when (state) {
                is AuthUiState.Loading -> isLoading = true
                
                // Only navigate to OTP on login success
                is AuthUiState.Success.Login -> {
                    isLoading = false
                    // Navigate to OTP screen when login is successful
                    onNavigateToOtp(email)
                }
                
                // Handle other success types (shouldn't normally happen in login screen)
                is AuthUiState.Success -> {
                    isLoading = false
                    // Log but don't navigate for other success types
                }
                
                is AuthUiState.Error -> {
                    isLoading = false
                    // Show backend error using reusable ErrorToast overlay
                    uiErrorMessage = state.message
                }
                
                is AuthUiState.Idle -> isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Logo
        Image(
            painter = ResourceHelper.loadLivestockWealthLogo(),
            contentDescription = "Livestock Wealth Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Title
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Demo Material Icons (for cross-platform test)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = "Star",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        
        // Email field
        LivestockTextField(
            value = email,
            onValueChange = { 
                email = it
                emailError = validateEmail(it)
            },
            label = "Email",
            hint = "Enter your email",
            isError = emailError != null,
            errorMessage = emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Password field
        LivestockPasswordField(
            value = password,
            onValueChange = {
                password = it
                passwordError = validatePassword(it)
            },
            label = "Password",
            isError = passwordError != null,
            errorMessage = passwordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (isFormValid()) {
                        authViewModel.login(email, password, "sms")
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Forgot password link
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToForgotPassword() }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Login button
        LivestockButton(
            text = "Login",
            onClick = {
                if (isFormValid()) {
                    authViewModel.login(email, password, "sms")
                } else {
                    focusManager.clearFocus()
                }
            },
            isLoading = isLoading,
            enabled = email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Register link
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Register",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }

    // Overlay error toast for backend/login errors
    ErrorToast(
        errorMessage = uiErrorMessage,
        onDismiss = { uiErrorMessage = null }
    )
}

// Validation functions
private fun validateEmail(email: String): String? {
    return if (email.isBlank()) {
        "Email is required"
    } else if (!isValidEmail(email)) {
        "Invalid email format"
    } else {
        null
    }
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return email.trim().matches(emailRegex)
}

private fun validatePassword(password: String): String? {
    return if (password.isBlank()) {
        "Password is required"
    } else {
        null
    }
}
