package com.lswmobile.app.ui.screens.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.WithdrawalType
import com.lswmobile.app.network.model.BankType
import com.lswmobile.app.ui.components.LivestockTextField
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius
import com.lswmobile.app.viewmodel.WithdrawalViewModel
import org.koin.compose.koinInject
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import com.lswmobile.app.ui.theme.AppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestWithdrawalScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: WithdrawalViewModel = koinInject()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    // Initialize ViewModel
    LaunchedEffect(Unit) {
        viewModel.initialize()
    }
    
    // Handle success message
    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Withdrawal") },
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
                        imageVector = AppIcons.Filled.ArrowUpward,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Request Withdrawal",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Withdraw funds to your bank account",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
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
            
            // Withdrawal Type Selection
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Select Country",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Display current withdrawal type based on selected country
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Withdrawal Type: ",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = viewModel.selectedWithdrawalType.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Custom country selector instead of CountryPickerBasicTextField
                    CountrySelector(
                        selectedCountryCode = viewModel.selectedCountryCode,
                        onCountrySelected = { viewModel.updateCountryCode(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = if (viewModel.selectedWithdrawalType == WithdrawalType.LOCAL) {
                            "Lower fees, faster processing for South African withdrawals"
                        } else {
                            "Higher fees, additional fields required for international withdrawals"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Withdrawal Details
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Withdrawal Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Amount
                    LivestockTextField(
                        value = viewModel.amount,
                        onValueChange = { viewModel.updateAmount(it) },
                        label = "Amount (ZAR) *",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Reason
                    LivestockTextField(
                        value = viewModel.reason,
                        onValueChange = { viewModel.updateReason(it) },
                        label = "Reason for Withdrawal *",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Fee Information
                    if (viewModel.withdrawalFees != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "Fee Breakdown",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Amount:")
                                    Text("R${viewModel.amount.ifEmpty { "0.00" }}")
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Fee:")
                                    Text("R${viewModel.currentFee}")
                                }
                                
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Net Amount:",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        "R${viewModel.netAmount}",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bank Details
            BankDetailsSection(viewModel = viewModel)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Terms and Conditions
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Terms & Conditions",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = viewModel.agreeToTerms,
                            onCheckedChange = { viewModel.updateAgreeToTerms(it) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I agree to the terms and conditions",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = viewModel.agreeToFees,
                            onCheckedChange = { viewModel.updateAgreeToFees(it) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I agree to the withdrawal fee of R${viewModel.currentFee}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Validation Errors
            if (viewModel.validationErrors.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Please fix the following errors:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        viewModel.validationErrors.forEach { error ->
                            Text(
                                text = "• $error",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Action Buttons
            Button(
                onClick = { viewModel.submitWithdrawal() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (viewModel.isLoading) "Submitting..." else "Submit Withdrawal Request")
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
}

@Composable
private fun BankDetailsSection(viewModel: WithdrawalViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Bank Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Account Number
            LivestockTextField(
                value = viewModel.accountNumber,
                onValueChange = { viewModel.updateAccountNumber(it) },
                label = "Account Number *",
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Account Holder Name
            LivestockTextField(
                value = viewModel.accountHolderName,
                onValueChange = { viewModel.updateAccountHolderName(it) },
                label = "Account Holder Name *",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (viewModel.selectedWithdrawalType == WithdrawalType.LOCAL) {
                // Local withdrawal fields
                LocalWithdrawalFields(viewModel = viewModel)
            } else {
                // International withdrawal fields
                InternationalWithdrawalFields(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun LocalWithdrawalFields(viewModel: WithdrawalViewModel) {
    // SA Bank Selection using BankDropdown
    BankDropdown(
        selectedBank = viewModel.selectedSABank ?: BankType("", ""),
        banks = viewModel.localBanks,
        onBankChange = { bank ->
            viewModel.updateSelectedSABank(bank)
        }
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Branch Name (optional for local)
    LivestockTextField(
        value = viewModel.branchName,
        onValueChange = { viewModel.updateBranchName(it) },
        label = "Branch Name (Optional)",
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun InternationalWithdrawalFields(viewModel: WithdrawalViewModel) {
    // Bank Name
    LivestockTextField(
        value = viewModel.bankName,
        onValueChange = { viewModel.updateBankName(it) },
        label = "Bank Name *",
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // SWIFT Code
    LivestockTextField(
        value = viewModel.swiftCode,
        onValueChange = { viewModel.updateSwiftCode(it) },
        label = "SWIFT Code *",
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // IBAN
    LivestockTextField(
        value = viewModel.iban,
        onValueChange = { viewModel.updateIban(it) },
        label = "IBAN *",
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Bank Address
    LivestockTextField(
        value = viewModel.bankAddress,
        onValueChange = { viewModel.updateBankAddress(it) },
        label = "Bank Address *",
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Branch Name
    LivestockTextField(
        value = viewModel.branchName,
        onValueChange = { viewModel.updateBranchName(it) },
        label = "Branch Name *",
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Country
    LivestockTextField(
        value = viewModel.selectedCountryCode,
        onValueChange = { viewModel.updateCountryCode(it) },
        label = "Country *",
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Passport Number
    LivestockTextField(
        value = viewModel.passportNumber,
        onValueChange = { viewModel.updatePassportNumber(it) },
        label = "Passport Number *",
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Passport Country
    LivestockTextField(
        value = viewModel.passportCountry,
        onValueChange = { viewModel.updatePassportCountry(it) },
        label = "Passport Country *",
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BankDropdown(
    selectedBank: BankType,
    banks: List<BankType>,
    onBankChange: (BankType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        OutlinedTextField(
            value = selectedBank.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Choose Bank") },
            trailingIcon = {
                Icon(
                    imageVector = AppIcons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.clickable { 
                        expanded = true 
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DefaultCornerRadius),
            interactionSource = remember { MutableInteractionSource() }
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { 
                expanded = false 
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            banks.forEach { bank ->
                DropdownMenuItem(
                    text = { Text(bank.name) },
                    onClick = {
                        onBankChange(bank)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CountrySelector(
    selectedCountryCode: String,
    onCountrySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val countriesList = remember {
        listOf(
            "za" to "South Africa",
            "us" to "United States",
            "gb" to "United Kingdom",
            "ca" to "Canada",
            "au" to "Australia",
            "de" to "Germany",
            "fr" to "France",
            "in" to "India",
            "br" to "Brazil",
            "jp" to "Japan",
            "cn" to "China",
            "ru" to "Russia",
            "ng" to "Nigeria",
            "ke" to "Kenya",
            "gh" to "Ghana",
            "eg" to "Egypt"
            // Add more countries as needed
        )
    }

    var expanded by remember { mutableStateOf(false) }
    val selectedCountry = countriesList.firstOrNull { it.first.equals(selectedCountryCode, ignoreCase = true) } 
        ?: (selectedCountryCode to selectedCountryCode.uppercase())

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedCountry.second,
            onValueChange = { },
            readOnly = true,
            label = { Text("Country") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = AppIcons.Filled.KeyboardArrowDown,
                        contentDescription = "Select Country"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 350.dp)
        ) {
            countriesList.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = code.uppercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(40.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(name) 
                        }
                    },
                    onClick = {
                        onCountrySelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}