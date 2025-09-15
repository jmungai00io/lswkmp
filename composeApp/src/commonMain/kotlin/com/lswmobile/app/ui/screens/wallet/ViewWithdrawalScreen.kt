package com.lswmobile.app.ui.screens.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.Withdrawal
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius
import com.lswmobile.app.ui.utils.formatDate
import com.lswmobile.app.viewmodel.WithdrawalViewModel
import network.chaintech.cmpcountrycodepicker.ui.CountryPicker
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewWithdrawalScreen(
    withdrawalId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: WithdrawalViewModel = koinInject()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    // Load withdrawal details
    LaunchedEffect(withdrawalId) {
        viewModel.loadWithdrawalDetails(withdrawalId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Withdrawal Details") },
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
        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading withdrawal details...")
                    }
                }
            }
            
            viewModel.selectedWithdrawal != null -> {
                WithdrawalDetailsContent(
                    withdrawal = viewModel.selectedWithdrawal!!,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = AppIcons.Filled.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Withdrawal not found",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "The withdrawal you're looking for doesn't exist or has been removed.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WithdrawalDetailsContent(
    withdrawal: Withdrawal,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with amount and status
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
                Text(
                    text = "R${withdrawal.amount}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                WithdrawalStatusChip(status = withdrawal.status ?: "PENDING")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Show bank name instead of withdrawalType which doesn't exist
                withdrawal.bank?.let { bank ->
                    Text(
                        text = bank,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Withdrawal Information
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Withdrawal Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRow("Amount", "R${withdrawal.amount}")
                InfoRow("Fee", "R${withdrawal.withdrawalFee}")
                // Calculate net amount from amount and fee
                val netAmount = try {
                    val amountValue = withdrawal.amount.toDoubleOrNull() ?: 0.0
                    amountValue - withdrawal.withdrawalFee
                } catch (e: Exception) {
                    0.0
                }
                InfoRow("Net Amount", "R$netAmount")
                withdrawal.reason?.let { reason ->
                    InfoRow("Reason", reason)
                }
                // Use bank instead of withdrawalType
                withdrawal.bank?.let { bank ->
                    InfoRow("Bank", bank)
                }
                InfoRow("Status", withdrawal.status ?: "PENDING")
                InfoRow("Created", formatDate(withdrawal.dateCreated))
                withdrawal.dateProcessed?.let { dateProcessed ->
                    InfoRow("Processed", formatDate(dateProcessed))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bank Details
        if (withdrawal.accountNumber != null || withdrawal.accountName != null || withdrawal.bank != null) {
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
                    
                    withdrawal.accountName?.let { name ->
                        InfoRow("Account Holder", name)
                    }
                    withdrawal.accountNumber?.let { number ->
                        InfoRow("Account Number", number)
                    }
                    withdrawal.bank?.let { bankName ->
                        InfoRow("Bank Name", bankName)
                    }
                    withdrawal.branchCode?.let { branchCode ->
                        InfoRow("Branch Code", branchCode)
                    }
                    withdrawal.country?.let { country ->
                        InfoRow("Country", country)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Status Timeline
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Status Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                StatusTimelineItem(
                    status = "Requested",
                    date = formatDate(withdrawal.dateCreated),
                    isCompleted = true,
                    isActive = withdrawal.status == "PENDING"
                )
                
                StatusTimelineItem(
                    status = "Processing",
                    date = "In progress",
                    isCompleted = withdrawal.status in listOf("PROCESSING", "COMPLETED"),
                    isActive = withdrawal.status == "PROCESSING"
                )
                
                StatusTimelineItem(
                    status = "Completed",
                    date = if (withdrawal.status == "COMPLETED") 
                           withdrawal.dateProcessed?.let { formatDate(it) } ?: "Pending" 
                           else "Pending",
                    isCompleted = withdrawal.status == "COMPLETED",
                    isActive = withdrawal.status == "COMPLETED"
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
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

@Composable
private fun WithdrawalStatusChip(status: String) {
    val statusInfo = when (status.uppercase()) {
        "PENDING" -> StatusInfo(
            "Pending",
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.onTertiary
        )
        "PROCESSING" -> StatusInfo(
            "Processing",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        "COMPLETED" -> StatusInfo(
            "Completed",
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.onTertiary
        )
        "FAILED" -> StatusInfo(
            "Failed",
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError
        )
        "CANCELLED" -> StatusInfo(
            "Cancelled",
            MaterialTheme.colorScheme.outline,
            MaterialTheme.colorScheme.onSurface
        )
        else -> StatusInfo(
            status,
            MaterialTheme.colorScheme.outline,
            MaterialTheme.colorScheme.onSurface
        )
    }
    
    Surface(
        color = statusInfo.backgroundColor,
        contentColor = statusInfo.textColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = statusInfo.displayText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )

    }
}

@Composable
private fun StatusTimelineItem(
    status: String,
    date: String,
    isCompleted: Boolean,
    isActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status indicator
        Box(
            modifier = Modifier
                .size(24.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when {
                    isActive -> MaterialTheme.colorScheme.primary
                    isCompleted -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.outline
                },
                modifier = Modifier.size(16.dp)
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = AppIcons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Status content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}