package com.lswmobile.app.ui.screens.wallet

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.MyAsset
import com.lswmobile.app.network.model.WalletOverview
import com.lswmobile.app.ui.components.ErrorDisplay
import com.lswmobile.app.ui.components.PullToRefreshContainer
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius
import com.lswmobile.app.viewmodel.WalletViewModel

/**
 * Wallet screen showing financial overview and assets
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: WalletViewModel,
    onNavigateToPortfolio: () -> Unit,
    onNavigateToAssets: () -> Unit,
    onNavigateToStatement: () -> Unit,
    onNavigateToWithdrawals: () -> Unit,
    onRequestWithdrawal: () -> Unit
) {
    // Collect data from the ViewModel
    val walletOverview by viewModel.walletOverview.collectAsState()
    val assets by viewModel.assets.collectAsState()
    val isLoadingOverview by viewModel.isLoadingOverview.collectAsState()
    val isLoadingAssets by viewModel.isLoadingAssets.collectAsState()
    val overviewError by viewModel.overviewError.collectAsState()
    val assetsError by viewModel.assetsError.collectAsState()
    
    // Track pull-to-refresh state
    val isRefreshing = isLoadingOverview || isLoadingAssets
    
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
                viewModel.refresh()
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
                item {
                    if (overviewError != null) {
                        ErrorDisplay(
                            message = overviewError!!,
                            onDismiss = { viewModel.clearOverviewError() }
                        )
                    } else {
                        BalanceCard(
                            walletOverview = walletOverview,
                            isLoading = isLoadingOverview,
                            onRequestWithdrawal = onRequestWithdrawal
                        )
                    }
                }
                
                // Quick Actions
                item {
                    QuickActions(
                        onNavigateToPortfolio = onNavigateToPortfolio,
                        onNavigateToAssets = onNavigateToAssets,
                        onNavigateToStatement = onNavigateToStatement,
                        onNavigateToWithdrawals = onNavigateToWithdrawals
                    )
                }
                
                // My Assets Section
                item {
                    Text(
                        text = "My Assets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = AppTheme.spacing.medium.dp,
                            vertical = AppTheme.spacing.small.dp
                        )
                    )
                }
                
                // Assets error or content
                if (assetsError != null) {
                    item {
                        ErrorDisplay(
                            message = assetsError!!,
                            onDismiss = { viewModel.clearAssetsError() }
                        )
                    }
                } else if (isLoadingAssets) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppTheme.spacing.large.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (assets.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppTheme.spacing.medium.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(AppTheme.spacing.large.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = AppIcons.Outlined.Portfolio,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
                                Text(
                                    text = "No Assets Yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Start investing to see your assets here",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    // Asset items
                    items(assets) { asset ->
                        AssetItem(asset = asset)
                    }
                }
                
                // View All Assets button
                if (assets.isNotEmpty()) {
                    item {
                        FilledTonalButton(
                            onClick = onNavigateToAssets,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppTheme.spacing.medium.dp)
                        ) {
                            Text("View All Assets")
                        }
                        
                        Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
                    }
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
    walletOverview: WalletOverview?,
    isLoading: Boolean,
    onRequestWithdrawal: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.medium.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.large.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "R${String.format("%.2f", walletOverview?.balance ?: 0.0)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
            
            // Additional balance information
            if (!isLoading && walletOverview != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Available",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "R${String.format("%.2f", walletOverview.availableBalance)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    if (walletOverview.totalPriceOfAssetsInWaitingList > 0) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Pending",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "R${String.format("%.2f", walletOverview.totalPriceOfAssetsInWaitingList)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
            }
            
            Button(
                onClick = onRequestWithdrawal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
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
//        item {
//            QuickActionItem(
//                icon = AppIcons.Filled.Folder,
//                label = "Portfolio",
//                onClick = onNavigateToPortfolio
//            )
//        }
//
//        item {
//            QuickActionItem(
//                icon = AppIcons.Filled.Home,
//                label = "Assets",
//                onClick = onNavigateToAssets
//            )
//        }
        
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
 * List item for an asset
 */
@Composable
private fun AssetItem(asset: MyAsset) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.medium.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.medium.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Asset icon and info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.Outlined.Portfolio,
                            contentDescription = "Asset",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(AppTheme.spacing.medium.dp))
                    
                    Column {
                        Text(
                            text = asset.productType.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Text(
                            text = asset.dateOfAllocation ?: "N/A",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Current Value
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "R${String.format("%.2f", asset.valueToday)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Current Value",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Additional asset details
            if (asset.dividendAmount > 0 || asset.priceOfAsset > 0) {
                Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
                Divider()
                Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (asset.priceOfAsset > 0) {
                        Column {
                            Text(
                                text = "Purchase Price",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "R${String.format("%.2f", asset.priceOfAsset)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    if (asset.dividendAmount > 0) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Dividend",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "R${String.format("%.2f", asset.dividendAmount)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }
}
