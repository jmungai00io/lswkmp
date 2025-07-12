package com.lswmobile.app.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lswmobile.app.network.model.BankType
import com.lswmobile.app.network.model.OrderWithFullUser
import com.lswmobile.app.ui.components.BackButton
import com.lswmobile.app.ui.screens.orders.OrderViewModel
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius
import com.lswmobile.app.ui.utils.formatCurrency
import com.lswmobile.app.ui.utils.formatDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime

/**
 * Enhanced Debit Payment Screen
 * Handles debit card payments with two-step process: form and mandate signing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebitPaymentScreen(
    orderNumber: Int,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    viewModel: DebitPaymentViewModel,
    orderViewModel: OrderViewModel
) {
    var currentStep by remember { mutableStateOf(1) }
    
    // Collect state from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val isConfirming by viewModel.isConfirming.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val selectedOrder by orderViewModel.selectedOrder.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()
    
    // Form data
    val fullName by viewModel.fullName.collectAsState()
    val accountNumber by viewModel.accountNumber.collectAsState()
    val accountType by viewModel.accountType.collectAsState()
    val debitOrderDate by viewModel.debitOrderDate.collectAsState()
    val amountToDeduct by viewModel.amountToDeduct.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val countryPhoneCode by viewModel.countryPhoneCode.collectAsState()
    val address by viewModel.address.collectAsState()
    val debitMandateSign by viewModel.debitMandateSign.collectAsState()
    val selectedBank by viewModel.selectedBank.collectAsState()
    val banks by viewModel.banks.collectAsState()
    
    // Debug logging for banks
    LaunchedEffect(banks) {
        println("DebitPaymentScreen: Banks StateFlow updated: ${banks.size} banks")
        println("DebitPaymentScreen: Banks data: $banks")
    }
    
    // Validation errors
    val fullNameError by viewModel.fullNameError.collectAsState()
    val accountNumberError by viewModel.accountNumberError.collectAsState()
    val accountTypeError by viewModel.accountTypeError.collectAsState()
    val phoneError by viewModel.phoneError.collectAsState()
    val addressError by viewModel.addressError.collectAsState()
    val debitDateError by viewModel.debitDateError.collectAsState()
    val mandateError by viewModel.mandateError.collectAsState()
    val signTouched by viewModel.signTouched.collectAsState()
    
    // Default to full payment type for now
    val paymentType = PaymentType.FULL_PAYMENT
    
    // Calculate payment amount
    val paymentAmount = remember(selectedOrder, walletBalance, paymentType) {
        selectedOrder?.let { order ->
            viewModel.calculatePaymentAmount(order.amount, walletBalance, paymentType)
        } ?: 0.0
    }
    
    // Update amount when payment amount changes
    LaunchedEffect(paymentAmount) {
        viewModel.onAmountChange(paymentAmount.toString())
    }
    
    // Load data on initial composition
    LaunchedEffect(orderNumber) {
        viewModel.loadDebitPaymentData(orderNumber)
    }

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis > Clock.System.now().toEpochMilliseconds()
            }
        }
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (currentStep == 1) "Debit Payment" else "Debit Mandate",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    BackButton(onClick = onNavigateBack)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Step indicator
            StepIndicator(
                currentStep = currentStep,
                totalSteps = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.spacing.medium.dp)
            )
            
            // Content area with weight to take available space
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (currentStep) {
                    1 -> DebitPaymentForm(
                        fullName = fullName,
                        onFullNameChange = viewModel::onFullNameChange,
                        fullNameError = fullNameError,
                        amountToDeduct = amountToDeduct,
                        onAmountChange = viewModel::onAmountChange,
                        selectedBank = selectedBank,
                        banks = banks,
                        onBankChange = viewModel::onBankChange,
                        accountNumber = accountNumber,
                        onAccountNumberChange = viewModel::onAccountNumberChange,
                        accountNumberError = accountNumberError,
                        accountType = accountType,
                        onAccountTypeChange = viewModel::onAccountTypeChange,
                        accountTypeError = accountTypeError,
                        phoneNumber = phoneNumber,
                        onPhoneChange = viewModel::onPhoneChange,
                        phoneError = phoneError,
                        countryPhoneCode = countryPhoneCode,
                        onCountryPhoneCodeChange = viewModel::onCountryPhoneCodeChange,
                        debitOrderDate = debitOrderDate,
                        onDebitDateChange = viewModel::onDebitDateChange,
                        debitDateError = debitDateError,
                        address = address,
                        onAddressChange = viewModel::onAddressChange,
                        addressError = addressError,
                        showDatePicker = showDatePicker,
                        onShowDatePickerChange = { showDatePicker = it },
                        datePickerState = datePickerState,
                        orderNumber = orderNumber,
                        selectedOrder = selectedOrder,
                        walletBalance = walletBalance,
                        paymentAmount = paymentAmount,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        isStepValid = viewModel.isStep1Valid()
                    )
                    2 -> DebitMandateStep(
                        fullName = fullName,
                        address = address,
                        phoneNumber = phoneNumber,
                        countryPhoneCode = countryPhoneCode,
                        selectedBank = selectedBank,
                        accountNumber = accountNumber,
                        accountType = accountType,
                        amountToDeduct = amountToDeduct,
                        debitOrderDate = debitOrderDate,
                        debitMandateSign = debitMandateSign,
                        onMandateSignChange = viewModel::onMandateSignChange,
                        mandateError = mandateError,
                        signTouched = signTouched,
                        isStepValid = viewModel.isStep2Valid()
                    )
                }
            }
            
            // Bottom action buttons - now guaranteed to be visible
            val step1Valid = viewModel.isStep1Valid()
            val step2Valid = viewModel.isStep2Valid()
            
            // Debug logging for form validation
            LaunchedEffect(step1Valid, step2Valid) {
                println("DebitPaymentScreen: Step 1 valid: $step1Valid, Step 2 valid: $step2Valid")
            }
            
            BottomActionButtons(
                currentStep = currentStep,
                onPrevious = { 
                    println("DebitPaymentScreen: Going back to step 1")
                    currentStep = 1 
                },
                onNext = { 
                    println("DebitPaymentScreen: Going to step 2")
                    currentStep = 2 
                },
                onConfirm = {
                    println("DebitPaymentScreen: Submitting debit payment")
                    viewModel.confirmDebitPayment(orderNumber, paymentType, onPaymentSuccess)
                },
                isConfirming = isConfirming,
                isStepValid = if (currentStep == 1) step1Valid else step2Valid
            )
        }
    }
}

@Composable
private fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.medium.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalSteps) { step ->
                val stepNumber = step + 1
                val isActive = stepNumber == currentStep
                val isCompleted = stepNumber < currentStep
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small.dp)
                ) {
                    // Step circle
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = when {
                                    isCompleted -> MaterialTheme.colorScheme.primary
                                    isActive -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = AppIcons.Filled.Check,
                                contentDescription = "Completed",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = stepNumber.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Step label
                    if (isActive) {
                        Text(
                            text = when (stepNumber) {
                                1 -> "Account Details"
                                2 -> "Mandate Signing"
                                else -> "Step $stepNumber"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Connector line (except for last step)
                if (stepNumber < totalSteps) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(
                                color = if (isCompleted) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(1.dp)
                            )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebitPaymentForm(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    fullNameError: String,
    amountToDeduct: String,
    onAmountChange: (String) -> Unit,
    selectedBank: BankType,
    banks: List<BankType>,
    onBankChange: (BankType) -> Unit,
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    accountNumberError: String,
    accountType: String,
    onAccountTypeChange: (String) -> Unit,
    accountTypeError: String,
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    phoneError: String,
    countryPhoneCode: String,
    onCountryPhoneCodeChange: (String) -> Unit,
    debitOrderDate: String,
    onDebitDateChange: (String) -> Unit,
    debitDateError: String,
    address: String,
    onAddressChange: (String) -> Unit,
    addressError: String,
    showDatePicker: Boolean,
    onShowDatePickerChange: (Boolean) -> Unit,
    datePickerState: DatePickerState,
    orderNumber: Int,
    selectedOrder: OrderWithFullUser?,
    walletBalance: Double,
    paymentAmount: Double,
    isLoading: Boolean,
    errorMessage: String?,
    isStepValid: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(AppTheme.spacing.medium.dp)
    ) {
        // Payment Summary Card
        PaymentSummaryCard(
            orderNumber = orderNumber,
            selectedOrder = selectedOrder,
            walletBalance = walletBalance,
            paymentAmount = paymentAmount
        )
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
        
        // Loading indicator
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
        }
        
        // Error message
        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(AppTheme.spacing.medium.dp)
                )
            }
            Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
        }
        
        // Form fields
        Text(
            text = "Account Details",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        // Full Name
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Full Name") },
            isError = fullNameError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DefaultCornerRadius)
        )
        if (fullNameError.isNotEmpty()) {
            Text(
                text = fullNameError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
        
        // Amount (read-only)
        OutlinedTextField(
            value = amountToDeduct,
            onValueChange = onAmountChange,
            label = { Text("Amount to be Deducted") },
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DefaultCornerRadius)
        )
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
        
        // Bank Selection
        // Debug: Show banks count
        Text(
            text = "Debug: Banks loaded: ${banks.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        BankDropdown(
            selectedBank = selectedBank,
            banks = banks,
            onBankChange = onBankChange
        )
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
        
        // Account Number
        OutlinedTextField(
            value = accountNumber,
            onValueChange = onAccountNumberChange,
            label = { Text("Account Number") },
            isError = accountNumberError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DefaultCornerRadius)
        )
        if (accountNumberError.isNotEmpty()) {
            Text(
                text = accountNumberError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
        
        // Account Type
        AccountTypeDropdown(
            accountType = accountType,
            onAccountTypeChange = onAccountTypeChange,
            accountTypeError = accountTypeError
        )
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
        
        // Phone Number
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneChange,
            label = { Text("Phone Number") },
            isError = phoneError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DefaultCornerRadius)
        )
        if (phoneError.isNotEmpty()) {
            Text(
                text = phoneError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
        
        // Debit Order Date
        OutlinedButton(
            onClick = { onShowDatePickerChange(true) },
            shape = RoundedCornerShape(DefaultCornerRadius),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Debit Order Date: ",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = debitOrderDate.ifEmpty { "Select date" },
                    color = if (debitOrderDate.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant 
                           else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        if (debitDateError.isNotEmpty()) {
            Text(
                text = debitDateError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
        
        // Address
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Address") },
            isError = addressError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DefaultCornerRadius)
        )
        if (addressError.isNotEmpty()) {
            Text(
                text = addressError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom buttons
    }
    
    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onShowDatePickerChange(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDebitDateChange(formatDate(millis))
                        }
                        onShowDatePickerChange(false)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { onShowDatePickerChange(false) }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun DebitMandateStep(
    fullName: String,
    address: String,
    phoneNumber: String,
    countryPhoneCode: String,
    selectedBank: BankType,
    accountNumber: String,
    accountType: String,
    amountToDeduct: String,
    debitOrderDate: String,
    debitMandateSign: String,
    onMandateSignChange: (String) -> Unit,
    mandateError: String,
    signTouched: Boolean,
    isStepValid: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppTheme.spacing.medium.dp)
    ) {
        // Mandate document
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.spacing.medium.dp)
            ) {
                Text(
                    text = "DEBIT MANDATE",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
                
                // Account details summary
                AccountDetailsSummary(
                    fullName = fullName,
                    address = address,
                    phoneNumber = phoneNumber,
                    countryPhoneCode = countryPhoneCode,
                    selectedBank = selectedBank,
                    accountNumber = accountNumber,
                    accountType = accountType,
                    amountToDeduct = amountToDeduct,
                    debitOrderDate = debitOrderDate
                )
                
                Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
                
                // Mandate terms
                MandateTerms(debitOrderDate = debitOrderDate)
                
                Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
                
                // Signature section
                SignatureSection(
                    debitMandateSign = debitMandateSign,
                    onMandateSignChange = onMandateSignChange,
                    mandateError = mandateError,
                    signTouched = signTouched
                )
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom buttons
    }
}

@Composable
private fun PaymentSummaryCard(
    orderNumber: Int,
    selectedOrder: OrderWithFullUser?,
    walletBalance: Double,
    paymentAmount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.medium.dp)
        ) {
            Text(
                text = "Payment Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Divider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            DetailRow("Order Number", orderNumber.toString())
            DetailRow("Wallet Balance", formatCurrency(walletBalance))
            DetailRow("Order Amount", formatCurrency(selectedOrder?.amount ?: 0.0))
            DetailRow("Payment Method", "Debit Order")
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Divider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Amount to Pay",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatCurrency(paymentAmount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun BankDropdown(
    selectedBank: BankType,
    banks: List<BankType>,
    onBankChange: (BankType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Debug logging for BankDropdown
    LaunchedEffect(banks) {
        println("BankDropdown: Received banks: ${banks.size} banks")
        println("BankDropdown: Banks data: $banks")
    }
    
    LaunchedEffect(expanded) {
        println("BankDropdown: Dropdown expanded state: $expanded")
    }
    
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
                        println("BankDropdown: Icon clicked, expanding dropdown")
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
                println("BankDropdown: Dropdown dismissed")
                expanded = false 
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            println("BankDropdown: Rendering dropdown menu with ${banks.size} banks, expanded=$expanded")
            banks.forEach { bank ->
                println("BankDropdown: Rendering bank item: ${bank.name}")
                DropdownMenuItem(
                    text = { Text(bank.name) },
                    onClick = {
                        println("BankDropdown: Bank selected: ${bank.name}")
                        onBankChange(bank)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AccountTypeDropdown(
    accountType: String,
    onAccountTypeChange: (String) -> Unit,
    accountTypeError: String
) {
    var expanded by remember { mutableStateOf(false) }
    val accountTypes = listOf("Savings", "Current", "Transmission")
    
    Column {
        OutlinedTextField(
            value = accountType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Account Type") },
            isError = accountTypeError.isNotEmpty(),
            trailingIcon = {
                Icon(
                    imageVector = AppIcons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.clickable { 
                        println("AccountTypeDropdown: Icon clicked, expanding dropdown")
                        expanded = true 
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DefaultCornerRadius),
            interactionSource = remember { MutableInteractionSource() }
        )
        
        if (accountTypeError.isNotEmpty()) {
            Text(
                text = accountTypeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { 
                println("AccountTypeDropdown: Dropdown dismissed")
                expanded = false 
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            println("AccountTypeDropdown: Rendering dropdown menu with ${accountTypes.size} types, expanded=$expanded")
            accountTypes.forEach { type ->
                println("AccountTypeDropdown: Rendering account type item: $type")
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        println("AccountTypeDropdown: Account type selected: $type")
                        onAccountTypeChange(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AccountDetailsSummary(
    fullName: String,
    address: String,
    phoneNumber: String,
    countryPhoneCode: String,
    selectedBank: BankType,
    accountNumber: String,
    accountType: String,
    amountToDeduct: String,
    debitOrderDate: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DetailRow("Name of Account to Debit", fullName)
        DetailRow("Domicile", address)
        DetailRow("Contact Numbers", "$countryPhoneCode$phoneNumber")
        DetailRow("Bank", selectedBank.name)
        DetailRow("Branch Code", selectedBank.code)
        DetailRow("Account Number", accountNumber)
        DetailRow("Account Type", accountType)
        DetailRow("Amount to be Deducted", amountToDeduct)
        DetailRow("Debit Order Date", debitOrderDate)
    }
}

@Composable
private fun MandateTerms(debitOrderDate: String) {
    val today: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    val signedAtDate = "Signed at: ${today.dayOfMonth} ${today.month.name} ${today.year}"
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "LIVESTOCK WEALTH",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "This signed authority and mandate is given by me/us, the undersigned, on this $signedAtDate",
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "I/We hereby authorize you to issue payment instructions to debit my/our account as specified above.",
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "In the event that the payment day falls on a weekend or public holiday, the debit will be processed on the next business day.",
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "Payment instructions due: $debitOrderDate",
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "I understand that this authority may be cancelled by me/us in writing.",
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "The individual payment instructions will be processed in accordance with the mandate.",
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "B MANDATE",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "I can acknowledge that this mandate will be used for future debit order transactions.",
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "I can acknowledge that there may be penalties for dishonored payments.",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "C CANCELLATION",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "We agree to the cancellation of this mandate.",
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "D ASSIGNMENT",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "I acknowledge that this mandate may be assigned to another party.",
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = signedAtDate,
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "Account Holder:",
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
        
        Text(
            text = "Type your full name:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = AppTheme.spacing.small.dp)
        )
    }
}

@Composable
private fun SignatureSection(
    debitMandateSign: String,
    onMandateSignChange: (String) -> Unit,
    mandateError: String,
    signTouched: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = debitMandateSign,
            onValueChange = onMandateSignChange,
            label = { Text("Full name in CAPS") },
            isError = mandateError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DefaultCornerRadius)
        )
        
        if (mandateError.isNotEmpty()) {
            Text(
                text = mandateError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        if (debitMandateSign.isNotEmpty()) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            Text(
                text = debitMandateSign,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun BottomActionButtons(
    currentStep: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onConfirm: () -> Unit,
    isConfirming: Boolean,
    isStepValid: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppTheme.spacing.medium.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.medium.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            when (currentStep) {
                1 -> {
                    // Step 1: Only Next button
                    Button(
                        onClick = onNext,
                        enabled = isStepValid,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Next")
                    }
                }
                2 -> {
                    // Step 2: Previous and Submit buttons
                    Button(
                        onClick = onPrevious,
                        enabled = true,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Previous")
                    }
                    
                    Spacer(modifier = Modifier.width(AppTheme.spacing.small.dp))
                    
                    Button(
                        onClick = onConfirm,
                        enabled = isStepValid && !isConfirming,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isConfirming) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...")
                        } else {
                            Text("Submit Debit")
                        }
                    }
                }
            }
        }
    }
} 