package com.lswmobile.app.ui.screens.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.Statement
import com.lswmobile.app.ui.components.PullToRefreshContainer
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.absoluteValue

/**
 * Wallet screen showing financial overview and recent transactions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(

    onNavigateToPortfolio: () -> Unit,
    onNavigateToAssets: () -> Unit,
    onNavigateToStatement: () -> Unit,
    onNavigateToWithdrawals: () -> Unit,
    onRequestWithdrawal: () -> Unit
) {
    // Collect data from the repository
//    val walletOverview by repository.walletOverview.collectAsState(initial = null)
//    val statements by repository.statements.collectAsState(initial = emptyList())
    
    // Track pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    
    // Setup scrolling behavior for the large title (iOS-style)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            WalletTopBar(
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        PullToRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                // Simulate a refresh
                kotlinx.coroutines.GlobalScope.launch {
                    delay(1500) // Simulate network delay
                    isRefreshing = false
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    bottom = AppTheme.spacing.extraLarge.dp
                ),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium.dp)
            ) {
                // Balance Card
//                item {
//                    BalanceCard(
//                        balance = walletOverview?.balance ?: 0.0,
//                        onRequestWithdrawal = onRequestWithdrawal
//                    )
//                }
                
                // Quick Actions
                item {
                    QuickActions(
                        onNavigateToPortfolio = onNavigateToPortfolio,
                        onNavigateToAssets = onNavigateToAssets,
                        onNavigateToStatement = onNavigateToStatement,
                        onNavigateToWithdrawals = onNavigateToWithdrawals
                    )
                }
                
                // Recent Transactions
                item {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = AppTheme.spacing.medium.dp,
                            vertical = AppTheme.spacing.small.dp
                        )
                    )
                }
                
                // Transaction items
//                items(statements.take(10)) { statement ->
//                    TransactionItem(statement = statement)
//                }
                
                // View All button
                item {
                    FilledTonalButton(
                        onClick = onNavigateToStatement,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.spacing.medium.dp)
                    ) {
                        Text("View All Transactions")
                    }
                    
                    Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
                }
            }
        }
    }
}

/**
 * Top app bar for the Wallet screen with large title (iOS style)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletTopBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = {
            Text(
                text = "Wallet",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Card displaying the current wallet balance
 */
@Composable
private fun BalanceCard(
    balance: Double,
    onRequestWithdrawal: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.medium.dp),
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.medium.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Current Balance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Text(
                text = "R${balance.toDouble().toString().take(10)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
            
            Button(
                onClick = onRequestWithdrawal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(top = AppTheme.spacing.medium.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Filled.ArrowUpward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(AppTheme.spacing.small.dp))
                Text("Request Withdrawal")
            }
        }
    }
}

/**
 * Quick action buttons for common wallet operations
 */
@Composable
private fun QuickActions(
    onNavigateToPortfolio: () -> Unit,
    onNavigateToAssets: () -> Unit,
    onNavigateToStatement: () -> Unit,
    onNavigateToWithdrawals: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = AppTheme.spacing.medium.dp),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small.dp)
    ) {
        item {
            QuickActionItem(
                icon = AppIcons.Filled.Folder,
                label = "Portfolio",
                onClick = onNavigateToPortfolio
            )
        }
        
        item {
            QuickActionItem(
                icon = AppIcons.Filled.Home,
                label = "Assets",
                onClick = onNavigateToAssets
            )
        }
        
        item {
            QuickActionItem(
                icon = AppIcons.Filled.Description,
                label = "Statement",
                onClick = onNavigateToStatement
            )
        }
        
        item {
            QuickActionItem(
                icon = AppIcons.Filled.Receipt,
                label = "Withdrawals",
                onClick = onNavigateToWithdrawals
            )
        }
    }
}

/**
 * Individual quick action item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        shape = RoundedCornerShape(DefaultCornerRadius),
        modifier = Modifier.width(110.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(
                vertical = AppTheme.spacing.medium.dp,
                horizontal = AppTheme.spacing.small.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * List item for a transaction
 */
@Composable
private fun TransactionItem(statement: Statement) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.medium.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppTheme.spacing.small.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction icon and info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isIncoming = statement.amount > 0
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isIncoming) MaterialTheme.colorScheme.tertiaryContainer
                            else MaterialTheme.colorScheme.errorContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIncoming) AppIcons.Filled.ArrowDownward else AppIcons.Filled.ArrowUpward,
                        contentDescription = if (isIncoming) "Incoming" else "Outgoing",
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(AppTheme.spacing.small.dp))
                
                Column {
                    Text(
                        text = statement.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = formatDate(statement.dateOfTransaction),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Amount
            Text(
                text = "${if (statement.amount > 0) "+" else ""}R${statement.amount.toDouble().toString().take(10)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (statement.amount > 0) MaterialTheme.colorScheme.tertiary
                       else MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Format a date to a readable string
 */
private fun formatDate(date: String): String {
    return try {
        val localDate = LocalDate.parse(date.split("T")[0])
        "${localDate.dayOfMonth} ${getMonthName(localDate.monthNumber)} ${localDate.year}"
    } catch (e: Exception) {
        date // Fallback to raw date if parsing fails
    }
}

/**
 * Get month name from month number
 */
private fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> "Unknown"
    }
}

@Preview
@Composable
private fun WalletScreenPreview() {
//    val repository = SampleFinanceRepository.getInstance()
    
    WalletScreen(
//        repository = repository,
        onNavigateToPortfolio = {},
        onNavigateToAssets = {},
        onNavigateToStatement = {},
        onNavigateToWithdrawals = {},
        onRequestWithdrawal = {}
    )
}
