package com.lswmobile.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lswmobile.app.ui.components.ErrorToast
import com.lswmobile.app.ui.components.LivestockTextField
import com.lswmobile.app.ui.components.SuccessToast
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.utils.formatIdType
import com.lswmobile.app.ui.utils.isValidEmail
import com.lswmobile.app.viewmodel.BeneficiaryViewModel
import com.lswmobile.app.network.model.IdType

/**
 * Validate international phone number format
 * Accepts formats like: +1 234 567 8900, +44 20 7946 0958, +27 123456789
 * Supports most international country codes and various formatting
 */
fun validateInternationalPhoneNumber(phoneNumber: String): Boolean {
    // Remove all spaces, dashes, parentheses, and dots for validation
    val cleanNumber = phoneNumber.replace(Regex("[\\s\\-\\(\\)\\.]"), "")
    
    // Comprehensive international phone number pattern
    // Matches: +[country code][national number]
    // Country codes: 1-3 digits
    // National numbers: 7-15 digits (most mobile numbers are 7-12 digits)
    // Total length: 8-17 digits (including country code)
    val pattern = Regex("^\\+[1-9]\\d{1,2}\\d{7,14}$")
    
    return pattern.matches(cleanNumber)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBeneficiaryScreen(
    beneficiaryViewModel: BeneficiaryViewModel? = null,
    onNavigateBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    // Form state
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var idType by remember { mutableStateOf(IdType.PASSPORT) }
    var idValue by remember { mutableStateOf("") }
    
    // Validation state
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneNumberError by remember { mutableStateOf<String?>(null) }
    var idValueError by remember { mutableStateOf<String?>(null) }
    
    // Collect ViewModel state
    val isLoading = beneficiaryViewModel?.isLoading ?: false
    val error = beneficiaryViewModel?.error
    val successMessage = beneficiaryViewModel?.successMessage
    
    // Handle success message
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            onSuccess()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Beneficiary") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = AppIcons.Filled.Back,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = AppIcons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Add New Beneficiary",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = "Add someone who will benefit from your investments",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Personal Information Section
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Personal Information",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // First Name
                            LivestockTextField(
                                value = firstName,
                                onValueChange = { 
                                    firstName = it
                                    firstNameError = null
                                },
                                label = "First Name *",
                                modifier = Modifier.fillMaxWidth(),
                                isError = firstNameError != null,
                                errorMessage = firstNameError
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Last Name
                            LivestockTextField(
                                value = lastName,
                                onValueChange = { 
                                    lastName = it
                                    lastNameError = null
                                },
                                label = "Last Name *",
                                modifier = Modifier.fillMaxWidth(),
                                isError = lastNameError != null,
                                errorMessage = lastNameError
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Email
                            LivestockTextField(
                                value = email,
                                onValueChange = { 
                                    email = it
                                    emailError = null
                                },
                                label = "Email Address *",
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                isError = emailError != null,
                                errorMessage = emailError
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Phone Number
                            LivestockTextField(
                                value = phoneNumber,
                                onValueChange = { 
                                    phoneNumber = it
                                    phoneNumberError = null
                                },
                                label = "Phone Number *",
                                hint = "+1 234 567 8900",
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth(),
                                isError = phoneNumberError != null,
                                errorMessage = phoneNumberError
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // ID Information Section
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Identification",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // ID Type Selection
                            Text(
                                text = "ID Type *",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val idTypes = beneficiaryViewModel?.idTypes ?: listOf(IdType.PASSPORT, IdType.NATIONAL_ID)
                            
                            idTypes.forEach { type ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = idType == type,
                                        onClick = { idType = type }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = formatIdType(type.name),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // ID Value
                            LivestockTextField(
                                value = idValue,
                                onValueChange = { 
                                    idValue = it
                                    idValueError = null
                                },
                                label = "ID Number *",
                                modifier = Modifier.fillMaxWidth(),
                                isError = idValueError != null,
                                errorMessage = idValueError
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Action Buttons
                    Button(
                        onClick = {
                            // Validate form
                            var isValid = true
                            
                            if (firstName.isBlank()) {
                                firstNameError = "First name is required"
                                isValid = false
                            }
                            
                            if (lastName.isBlank()) {
                                lastNameError = "Last name is required"
                                isValid = false
                            }
                            
                            if (email.isBlank()) {
                                emailError = "Email is required"
                                isValid = false
                            } else if (!isValidEmail(email)) {
                                emailError = "Please enter a valid email address"
                                isValid = false
                            }
                            
                            if (phoneNumber.isBlank()) {
                                phoneNumberError = "Phone number is required"
                                isValid = false
                            } else if (!validateInternationalPhoneNumber(phoneNumber)) {
                                phoneNumberError = "Please enter a valid international phone number (e.g., +1 234 567 8900)"
                                isValid = false
                            }
                            
                            if (idValue.isBlank()) {
                                idValueError = "ID number is required"
                                isValid = false
                            }
                            
                            if (isValid) {
                                beneficiaryViewModel?.addBeneficiary(
                                    firstName = firstName,
                                    lastName = lastName,
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    idType = idType,
                                    idValue = idValue
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (isLoading) "Adding..." else "Add Beneficiary")
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            }
            
            // Error and Success Toasts (overlay)
            ErrorToast(
                errorMessage = error,
                onDismiss = { beneficiaryViewModel?.clearError() }
            )
            
            SuccessToast(
                successMessage = successMessage,
                onDismiss = { beneficiaryViewModel?.clearSuccessMessage() }
            )
        }
    }
}