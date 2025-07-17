package com.lswmobile.app.ui.screens.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.Withdrawal
import com.lswmobile.app.ui.components.PullToRefreshContainer
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius
import com.lswmobile.app.ui.utils.formatDate
import com.lswmobile.app.viewmodel.WithdrawalViewModel
import org.koin.compose.koinInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWithdrawalsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToRequestWithdrawal: () -> Unit = {},
    onNavigateToWithdrawalDetail: (String) -> Unit = {},
    viewModel: WithdrawalViewModel = koinInject()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    // Track pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    
    // Initialize ViewModel
    LaunchedEffect(Unit) {
        viewModel.initialize()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Withdrawals") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = AppIcons.Filled.Back,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToRequestWithdrawal) {
                        Icon(
                            imageVector = AppIcons.Filled.Add,
                            contentDescription = "Request Withdrawal"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        PullToRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadWithdrawals()
                kotlinx.coroutines.GlobalScope.launch {
                    delay(1500) // Simulate network delay
                    isRefreshing = false
                }
            }
        ) {
            when {
                viewModel.isLoading && viewModel.withdrawals.isEmpty() -> {
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
                            Text("Loading withdrawals...")
                        }
                    }
                }
                
                viewModel.withdrawals.isEmpty() -> {
                    EmptyWithdrawalsState(
                        onRequestWithdrawal = onNavigateToRequestWithdrawal,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                
                else -> {
                    WithdrawalsList(
                        withdrawals = viewModel.withdrawals,
                        onWithdrawalClick = onNavigateToWithdrawalDetail,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyWithdrawalsState(
    onRequestWithdrawal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = AppIcons.Filled.Receipt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Withdrawals Yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "You haven't made any withdrawal requests yet. Start by requesting your first withdrawal.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRequestWithdrawal,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = AppIcons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Request Withdrawal")
        }
    }
}

@Composable
private fun WithdrawalsList(
    withdrawals: List<Withdrawal>,
    onWithdrawalClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = AppTheme.spacing.medium.dp,
            vertical = AppTheme.spacing.medium.dp
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium.dp)
    ) {
        items(withdrawals) { withdrawal ->
            WithdrawalCard(
                withdrawal = withdrawal,
                onClick = { onWithdrawalClick(withdrawal._id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WithdrawalCard(
    withdrawal: Withdrawal,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DefaultCornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with amount and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "R${withdrawal.amount}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                withdrawal.status?.let { WithdrawalStatusChip(status = it) }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Bank details
            withdrawal.bank?.let { bank ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = AppIcons.Filled.Description,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = bank,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Account name
            withdrawal.accountName?.let { accountName ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = AppIcons.Filled.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = accountName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Reason
            withdrawal.reason?.let { reason ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = AppIcons.Filled.Description,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Fee and net amount
            if (withdrawal.withdrawalFee > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Fee:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "R${withdrawal.withdrawalFee}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Net Amount:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Calculate net amount from amount and fee
                    val netAmount = try {
                        val amountValue = withdrawal.amount.toDoubleOrNull() ?: 0.0
                        amountValue - withdrawal.withdrawalFee
                    } catch (e: Exception) {
                        0.0
                    }
                    Text(
                        text = "R$netAmount",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = AppIcons.Filled.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Requested on ${formatDate(withdrawal.dateCreated)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

internal data class StatusInfo(
    val displayText: String,
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val textColor: androidx.compose.ui.graphics.Color
)