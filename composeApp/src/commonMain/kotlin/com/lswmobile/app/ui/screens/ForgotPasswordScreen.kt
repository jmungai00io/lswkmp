package com.lswmobile.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.ui.components.LivestockButton
import com.lswmobile.app.ui.components.LivestockTextField
import com.lswmobile.app.ui.resources.ResourceHelper
import com.lswmobile.app.viewmodel.AuthUiState
import com.lswmobile.app.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.DrawableResource

/**
 * Forgot Password screen for the Livestock Wealth app
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var resetSent by remember { mutableStateOf(false) }
    
    // Collect UI state
    LaunchedEffect(Unit) {
        authViewModel.uiState.collectLatest { state ->
            when (state) {
                is AuthUiState.Loading -> isLoading = true
                is AuthUiState.Success -> {
                    isLoading = false
                    resetSent = true
                }
                is AuthUiState.Error -> {
                    isLoading = false
                    // Handle error (could show a snackbar or dialog)
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
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Instructions
        Text(
            text = if (resetSent) 
                "Password reset link has been sent to your email. Please check your inbox."
            else
                "Enter your email address and we'll send you a link to reset your password.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (!resetSent) {
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
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (isEmailValid(email)) {
                            authViewModel.resetPassword(email)
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reset Password button
            LivestockButton(
                text = "Reset Password",
                onClick = {
                    if (isEmailValid(email)) {
                        authViewModel.resetPassword(email)
                    }
                },
                isLoading = isLoading,
                enabled = !isLoading && email.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Button to return to login
            LivestockButton(
                text = "Return to Login",
                onClick = { onNavigateToLogin() },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Back to login
        if (!resetSent) {
            Text(
                text = "← Back to Login",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Validation function
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
    return email.contains("@") && email.contains(".")
}

private fun isEmailValid(email: String): Boolean {
    return validateEmail(email) == null
}
