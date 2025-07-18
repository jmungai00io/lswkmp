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
    
    // Default to full payment type for now
    val paymentType = PaymentType.FULL_PAYMENT
    
    // Calculate payment amount
    val paymentAmount = remember(selectedOrder, walletBalance, paymentType) {
        selectedOrder?.let { order ->
            viewModel.calculatePaymentAmount(order.amount, walletBalance, paymentType)
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
                    if (!isConfirming) {
                        viewModel.confirmEftPayment(orderNumber, paymentType, onPaymentSuccess)
                    }
                },
                containerColor = if (isConfirming) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                },
                contentColor = if (isConfirming) {
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
            Text(
                text = "LSW Bank Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(DefaultCornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.spacing.medium.dp)
                ) {
                    eftDetails?.let { details ->
                        DetailRow("Account Holder", details.accountHolder)
                        DetailRow("Account Number", details.accountNumber)
                        DetailRow("Bank Name", details.bank)
                        DetailRow("Branch Code", details.branchCode)
                        DetailRow("Account Type", details.accountType)
                        DetailRow("Bank Address", details.bankAddress)
                        DetailRow("Swift Code", details.swiftCode)
                        selectedOrder?.reference?.let { DetailRow("Reference", it) }
                    } ?: run {
                        if (isLoading) {
                            Text(
                                text = "Loading bank details...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Bank details not available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.large.dp))
        }
    }
}

/**
 * Helper function to display a detail row with label and value
 */
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
} 