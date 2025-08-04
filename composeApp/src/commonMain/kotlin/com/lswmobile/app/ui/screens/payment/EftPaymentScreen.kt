package com.lswmobile.app.ui.screens.payment

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
import com.lswmobile.app.network.model.EftDetailsType
import com.lswmobile.app.network.model.OrderWithFullUser
import com.lswmobile.app.ui.components.BackButton
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius

/**
 * EFT Payment Screen
 * Handles EFT (Electronic Funds Transfer) payments for orders
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EftPaymentScreen(
    orderNumber: Int,
    viewModel: EftPaymentViewModel,
    orderViewModel: com.lswmobile.app.ui.screens.orders.OrderViewModel,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val eftDetails by viewModel.eftDetails.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isConfirming by viewModel.isConfirming.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val selectedOrder by orderViewModel.selectedOrder.collectAsState()
    
    // Payment type selection
    var selectedPaymentType by remember { mutableStateOf<PaymentType?>(null) }
    
    // Calculate available payment types based on wallet balance
    val availablePaymentTypes = remember(selectedOrder, walletBalance) {
        selectedOrder?.let { order ->
            val orderAmount = order.amount
            val balance = walletBalance
            
            if (balance >= orderAmount) {
                // Can pay full amount with wallet, but still offer both options
                listOf(PaymentType.FULL_PAYMENT, PaymentType.PARTIAL_TOPUP)
            } else if (balance > 0) {
                // Can pay partially with wallet
                listOf(PaymentType.PARTIAL_TOPUP, PaymentType.FULL_PAYMENT)
            } else {
                // No wallet balance, only full payment
                listOf(PaymentType.FULL_PAYMENT)
            }
        } ?: listOf(PaymentType.FULL_PAYMENT)
    }
    
    // Set default payment type
    LaunchedEffect(availablePaymentTypes) {
        if (selectedPaymentType == null && availablePaymentTypes.isNotEmpty()) {
            selectedPaymentType = availablePaymentTypes.first()
        }
    }
    
    // Calculate payment amount based on selected type
    val paymentAmount = remember(selectedOrder, walletBalance, selectedPaymentType) {
        selectedOrder?.let { order ->
            selectedPaymentType?.let { paymentType ->
                viewModel.calculatePaymentAmount(order.amount, walletBalance, paymentType)
            }
        } ?: 0.0
    }
    
    // Load data on initial composition
    LaunchedEffect(orderNumber) {
        viewModel.loadEftPaymentData(orderNumber)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EFT Payment") },
                navigationIcon = {
                    BackButton(onClick = onNavigateBack)
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (!isConfirming && selectedPaymentType != null) {
                        viewModel.confirmEftPayment(orderNumber, selectedPaymentType!!, onPaymentSuccess)
                    }
                },
                containerColor = if (isConfirming || selectedPaymentType == null) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                },
                contentColor = if (isConfirming || selectedPaymentType == null) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onPrimary
                },
                shape = RoundedCornerShape(DefaultCornerRadius),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                if (isConfirming) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isConfirming) "Processing..." else "Confirm EFT Payment",
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(AppTheme.spacing.medium.dp)
        ) {
            // Header
            Text(
                text = "Pay Order #$orderNumber by EFT",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
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
            
            // Payment Type Selection Card
            if (availablePaymentTypes.size > 1) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(DefaultCornerRadius),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.spacing.medium.dp)
                    ) {
                        Text(
                            text = "Payment Type",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
                        
                        availablePaymentTypes.forEach { paymentType ->
                            val isSelected = selectedPaymentType == paymentType
                            val description = when (paymentType) {
                                PaymentType.FULL_PAYMENT -> "Pay the full order amount (R${selectedOrder?.amount ?: 0.0})"
                                PaymentType.PARTIAL_TOPUP -> "Pay remaining amount after wallet (R${paymentAmount})"
                                PaymentType.TOPUP_ONLY -> "Top up wallet only"
                            }
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(AppTheme.spacing.medium.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPaymentType = paymentType },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = if (isSelected) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else {
                                                MaterialTheme.colorScheme.primary
                                            }
                                        )
                                    )
                                    
                                    Spacer(modifier = Modifier.width(AppTheme.spacing.small.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = when (paymentType) {
                                                PaymentType.FULL_PAYMENT -> "Complete Order Payment"
                                                PaymentType.PARTIAL_TOPUP -> "Partial Order Payment"
                                                PaymentType.TOPUP_ONLY -> "Top Up Only"
                                            },
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                        
                                        Text(
                                            text = description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
            }
            
            // Payment Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(DefaultCornerRadius),
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
                    
                    // Payment details
                    DetailRow("Order Number", orderNumber.toString())
                    DetailRow("Wallet Balance", "R ${walletBalance}")
                    DetailRow("Order Amount", "R ${selectedOrder?.amount ?: 0.0}")
                    DetailRow("Payment Method", "EFT Transfer")
                    
                    if (selectedPaymentType == PaymentType.PARTIAL_TOPUP && walletBalance > 0) {
                        DetailRow("Wallet Contribution", "R ${walletBalance}")
                    }
                    
                    Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
                    
                    Divider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                    
                    Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
                    
                    // Amount to pay
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
                            text = "R $paymentAmount",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
            
            // Bank Details Card
            eftDetails?.let { details ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(DefaultCornerRadius)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.spacing.medium.dp)
                    ) {
                        Text(
                            text = "Bank Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
                        
                        Divider()
                        
                        Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
                        
                        DetailRow("Bank Name", details.bank)
                        DetailRow("Account Number", details.accountNumber)
                        DetailRow("Branch Code", details.branchCode)
                        DetailRow("Account Type", details.accountType)
                        DetailRow("Reference", "Order #$orderNumber")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
} 