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
import com.lswmobile.app.network.model.UserResponse
import com.lswmobile.app.ui.components.LivestockTextField
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.utils.formatDate
import com.lswmobile.app.ui.utils.isValidPhoneNumber
import com.lswmobile.app.viewmodel.UpdateProfileViewModel
import org.koin.compose.koinInject
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    user: UserResponse? = null,
    onNavigateBack: () -> Unit = {},
    viewModel: UpdateProfileViewModel = koinInject()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    // Date picker state
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
    
    // Initialize form when user data is available
    LaunchedEffect(user) {
        viewModel.initializeForm(user)
    }
    
    // Handle success message
    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let {
            // Navigate back after successful update
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Profile") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
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
                        imageVector = AppIcons.Filled.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Update Your Profile",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Keep your information up to date",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error message
            viewModel.errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppIcons.Filled.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                imageVector = AppIcons.Filled.Clear,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
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
                        value = viewModel.firstName,
                        onValueChange = { viewModel.updateFirstName(it) },
                        label = "First Name *",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Last Name
                    LivestockTextField(
                        value = viewModel.lastName,
                        onValueChange = { viewModel.updateLastName(it) },
                        label = "Last Name *",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Phone Number
                    LivestockTextField(
                        value = viewModel.phoneNumber,
                        onValueChange = { viewModel.updatePhoneNumber(it) },
                        label = "Phone Number *",
                        hint = "e.g., +1234567890",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = !viewModel.isPhoneNumberValid,
                        errorMessage = if (!viewModel.isPhoneNumberValid) "Please enter a valid international phone number" else null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Date of Birth
                    OutlinedButton(
                        onClick = { openDialog = true },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Date of Birth: ",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (viewModel.dateOfBirth.isNotEmpty()) {
                                    formatDate(viewModel.dateOfBirth)
                                } else {
                                    "No date selected"
                                },
                                color = if (viewModel.dateOfBirth.isNotEmpty()) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Gender Selection
                    Text(
                        text = "Gender *",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    viewModel.genderOptions.forEach { gender ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = viewModel.gender == gender,
                                onClick = { viewModel.updateGender(gender) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (gender) {
                                    "MALE" -> "Male"
                                    "FEMALE" -> "Female"
                                    else -> gender
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Address Information Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Address Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Country
                    LivestockTextField(
                        value = viewModel.country,
                        onValueChange = { viewModel.updateCountry(it) },
                        label = "Country",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Town/City
                    LivestockTextField(
                        value = viewModel.town,
                        onValueChange = { viewModel.updateTown(it) },
                        label = "Town/City",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Address
                    LivestockTextField(
                        value = viewModel.address,
                        onValueChange = { viewModel.updateAddress(it) },
                        label = "Address",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Zip Code
                    LivestockTextField(
                        value = viewModel.zipCode,
                        onValueChange = { viewModel.updateZipCode(it) },
                        label = "Zip Code",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action Buttons
            Button(
                onClick = { viewModel.updateProfile() },
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.isFormValid && !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (viewModel.isLoading) "Updating..." else "Update Profile")
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
    
    // Date Picker Dialog
    if (openDialog) {
        DatePickerDialog(
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                            val selectedDate = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                            
                            // Format as YYYY-MM-DD
                            val formattedDate = "${selectedDate.date.year}-" +
                                "${selectedDate.date.monthNumber.toString().padStart(2, '0')}-" +
                                "${selectedDate.date.dayOfMonth.toString().padStart(2, '0')}"
                            
                            viewModel.updateDateOfBirth(formattedDate)
                        }
                        openDialog = false
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
} 