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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.ui.components.LivestockButton
import com.lswmobile.app.ui.components.LivestockPasswordField
import com.lswmobile.app.ui.components.LivestockTextField
import com.lswmobile.app.ui.resources.ResourceHelper
import com.lswmobile.app.viewmodel.AuthViewModel
import com.lswmobile.app.viewmodel.AuthUiState
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.DrawableResource

/**
 * Registration screen for the Livestock Wealth app
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateToOtp: (String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current
    
    // Form validation logic
    fun isFormValid(): Boolean {
        val isFirstNameValid = validateName(firstName, "First name") == null
        val isLastNameValid = validateName(lastName, "Last name") == null
        val isEmailValid = validateEmail(email) == null
        val isPhoneValid = validatePhoneNumber(phoneNumber) == null
        val isPasswordValid = validatePassword(password) == null
        val isConfirmPasswordValid = validateConfirmPassword(password, confirmPassword) == null
        
        return isFirstNameValid && isLastNameValid && isEmailValid && 
               isPhoneValid && isPasswordValid && isConfirmPasswordValid
    }
    
    // Collect UI state
    LaunchedEffect(Unit) {
        authViewModel.uiState.collectLatest { state ->
            when (state) {
                is AuthUiState.Loading -> isLoading = true
                is AuthUiState.Success -> {
                    isLoading = false
                    // Navigate to OTP screen when registration is successful
                    onNavigateToOtp(email)
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Logo
        Image(
            painter = ResourceHelper.loadShortLogo(),
            contentDescription = "Livestock Wealth Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp)
        )
        
        // Title
        Text(
            text = "Create an Account",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Subtitle
        Text(
            text = "Join Livestock Wealth and start investing",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // First Name
        LivestockTextField(
            value = firstName,
            onValueChange = { 
                firstName = it
                firstNameError = validateName(it, "First name")
            },
            label = "First Name",
            hint = "Enter your first name",
            isError = firstNameError != null,
            errorMessage = firstNameError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Last Name
        LivestockTextField(
            value = lastName,
            onValueChange = { 
                lastName = it
                lastNameError = validateName(it, "Last name")
            },
            label = "Last Name",
            hint = "Enter your last name",
            isError = lastNameError != null,
            errorMessage = lastNameError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Email
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
        
        // Phone Number
        LivestockTextField(
            value = phoneNumber,
            onValueChange = { 
                phoneNumber = it
                phoneError = validatePhoneNumber(it)
            },
            label = "Phone Number",
            hint = "Enter your phone number",
            isError = phoneError != null,
            errorMessage = phoneError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Password
        LivestockPasswordField(
            value = password,
            onValueChange = {
                password = it
                passwordError = validatePassword(it)
                if (confirmPassword.isNotEmpty()) {
                    confirmPasswordError = validateConfirmPassword(it, confirmPassword)
                }
            },
            label = "Password",
            isError = passwordError != null,
            errorMessage = passwordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Confirm Password
        LivestockPasswordField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = validateConfirmPassword(password, it)
            },
            label = "Confirm Password",
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isFormValid()) {
                        authViewModel.register(
                            email = email,
                            password = password,
                            phoneNumber = phoneNumber,
                            firstName = firstName,
                            lastName = lastName
                        )
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Register button
        LivestockButton(
            text = "Register",
            onClick = {
                if (isFormValid()) {
                    authViewModel.register(
                        email = email,
                        password = password,
                        phoneNumber = phoneNumber,
                        firstName = firstName,
                        lastName = lastName
                    )
                }
            },
            isLoading = isLoading,
            enabled = !isLoading && isFormValid(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Login link
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Login",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Validation functions
private fun validateName(name: String, field: String): String? {
    return if (name.isBlank()) {
        "$field is required"
    } else {
        null
    }
}

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

private fun validatePhoneNumber(phone: String): String? {
    return if (phone.isBlank()) {
        "Phone number is required"
    } else if (phone.length < 10) {
        "Phone number must be at least 10 digits"
    } else {
        null
    }
}

private fun validatePassword(password: String): String? {
    return when {
        password.isBlank() -> "Password is required"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }
}

private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
    return when {
        confirmPassword.isBlank() -> "Please confirm your password"
        confirmPassword != password -> "Passwords do not match"
        else -> null
    }
}
