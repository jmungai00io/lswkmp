package com.lswmobile.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.ui.resources.ResourceHelper
import com.lswmobile.app.viewmodel.AuthUiState
import com.lswmobile.app.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * OTP Verification screen for the Livestock Wealth app
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun OtpVerificationScreen(
    email: String,
    authViewModel: AuthViewModel,
    onNavigateToHome: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var resendEnabled by remember { mutableStateOf(true) }
    
    println("We are in OtpVerificationScreen with email: $email")
    
    // Collect UI state
    LaunchedEffect(Unit) {
        authViewModel.uiState.collectLatest { state ->
            when (state) {
                is AuthUiState.Loading -> isLoading = true
                
                // Only navigate to home on OTP verification success
                is AuthUiState.Success.OtpVerification -> {
                    isLoading = false
                    // Navigate to home screen when OTP verification is successful
                    onNavigateToHome()
                }
                
                // Handle other success cases
                is AuthUiState.Success -> {
                    isLoading = false
                    // We handle other success types but don't navigate
                    println("Received success: ${state.message} but not navigating")
                }
                
                is AuthUiState.Error -> {
                    isLoading = false
                    otpError = state.message
                }
                
                is AuthUiState.Idle -> isLoading = false
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo
            Image(
                painter = ResourceHelper.loadShortLogo(),
                contentDescription = "Livestock Wealth Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title
            Text(
                text = "OTP Verification",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            // Subtitle
            Text(
                text = "Please enter the verification code sent to $email",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // OTP input field
            OutlinedTextField(
                value = otp,
                onValueChange = { 
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        otp = it
                        otpError = null
                    }
                },
                label = { Text("Verification Code") },
                placeholder = { Text("Enter 6-digit code") },
                singleLine = true,
                isError = otpError != null,
                supportingText = otpError?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (validateOtp(otp) == null) {
                            authViewModel.verifyOtp(email, otp, "/auth")
                        } else {
                            otpError = validateOtp(otp)
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Verify button
            Button(
                onClick = {
                    if (validateOtp(otp) == null) {
                        authViewModel.verifyOtp(email, otp, "/auth")
                    } else {
                        otpError = validateOtp(otp)
                    }
                },
                enabled = !isLoading && otp.length == 6,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Verify Code")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Resend code option
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Didn't receive the code? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                TextButton(
                    onClick = { 
                        if (resendEnabled) {
                            authViewModel.login(email, "", "sms")
                            resendEnabled = false
                            // In a real app, you would start a timer here to re-enable after a cooldown period
                        }
                    },
                    enabled = resendEnabled
                ) {
                    Text("Resend")
                }
            }
        }
    }
}

// Validation function
private fun validateOtp(otp: String): String? {
    return when {
        otp.isBlank() -> "Verification code is required"
        otp.length < 6 -> "Please enter all 6 digits"
        !otp.all { it.isDigit() } -> "Code should contain only digits"
        else -> null
    }
}
