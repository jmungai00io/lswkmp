package com.lswmobile.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.auth.OtpFlowType
import com.lswmobile.app.auth.OtpNavigationState
import com.lswmobile.app.ui.resources.ResourceHelper
import com.lswmobile.app.ui.theme.AppIcons
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
    otpState: OtpNavigationState,
    authViewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onRegistrationComplete: () -> Unit = {},
    onGoBack: () -> Unit = {}
) {
    val email = otpState.email
    val flowType = otpState.flowType

    var otp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var resendEnabled by remember { mutableStateOf(true) }
    var registrationSuccessMessage by remember { mutableStateOf<String?>(null) }
    var showRegistrationDialog by remember { mutableStateOf(false) }
    

    // Collect UI state
    LaunchedEffect(Unit) {
        authViewModel.uiState.collectLatest { state ->
            when (state) {
                is AuthUiState.Loading -> isLoading = true
                
                // Only navigate to home on OTP verification success
                is AuthUiState.Success.OtpVerification -> {
                    isLoading = false
                    if (flowType == OtpFlowType.LOGIN) {
                        authViewModel.resetUiState()
                        onNavigateToHome()
                    } else {
                        registrationSuccessMessage = state.message
                        showRegistrationDialog = true
                    }
                }
                is AuthUiState.Success.Generic -> {
                    isLoading = false
                }
                
                // Handle other success cases
                is AuthUiState.Success -> {
                    isLoading = false
                }
                
                is AuthUiState.Error -> {
                    isLoading = false
                    otpError = state.message
                }
                
                is AuthUiState.Idle -> isLoading = false
            }
        }
    }
    
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    
    val handleBack: () -> Unit = {
        authViewModel.resetUiState()
        onGoBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Back button in top-left corner
        IconButton(
            onClick = handleBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = AppIcons.Filled.Back,
                contentDescription = "Go Back",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    focusManager.clearFocus()
                },
            contentAlignment = Alignment.Center
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .verticalScroll(scrollState),
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
                        focusManager.clearFocus()
                        if (validateOtp(otp) == null) {
                            authViewModel.verifyOtp(email, otp, flowType)
                        } else {
                            otpError = validateOtp(otp)
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Verify button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (validateOtp(otp) == null) {
                        authViewModel.verifyOtp(email, otp, flowType)
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
                            authViewModel.resendOtp(flowType)
                            resendEnabled = false
                            // In a real app, you would start a timer here to re-enable after a cooldown period
                        }
                    },
                    enabled = resendEnabled
                ) {
                    Text("Resend")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Back to login option
            TextButton(
                onClick = handleBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = AppIcons.Filled.Back,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Go Back")
            }
        }
        }
    }

    if (showRegistrationDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Registration successful") },
            text = {
                Text(registrationSuccessMessage ?: "Account created successfully. Please check your email to confirm and then log in.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRegistrationDialog = false
                        authViewModel.resetUiState()
                        onRegistrationComplete()
                    }
                ) {
                    Text("Go to Login")
                }
            }
        )
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
