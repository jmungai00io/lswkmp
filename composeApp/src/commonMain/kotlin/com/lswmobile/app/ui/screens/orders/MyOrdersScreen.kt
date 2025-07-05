package com.lswmobile.app.ui.screens.orders

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.OrderWithUserId
import com.lswmobile.app.ui.components.PullToRefreshContainer
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius
import com.lswmobile.app.ui.utils.formatDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen showing the user's orders
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    viewModel: OrderViewModel,
    onNavigateToOrderDetails: (Int) -> Unit
) {
    // Collect orders from the viewModel
    val orders by viewModel.filteredOrders.collectAsState()
    val isLoading by viewModel.isLoadingOrders.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    // Track pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    
    // Load orders on initial composition
    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }
    
    // Setup scrolling behavior for the large title (iOS-style)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            OrdersTopBar(
                scrollBehavior = scrollBehavior,
                onFilterClick = { /* Show filter options */ }
            )
        }
    ) { paddingValues ->
        PullToRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                // Fetch orders
                viewModel.loadOrders()
                // Simulate minimum refresh time for better UX
                kotlinx.coroutines.GlobalScope.launch {
                    delay(1000) // Ensure minimum refresh animation time
                    isRefreshing = false
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.spacing.medium.dp)
                        .padding(bottom = AppTheme.spacing.medium.dp),
                    placeholder = { Text("Search orders") },
                    leadingIcon = { 
                        Icon(
                            imageVector = AppIcons.Filled.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = AppIcons.Filled.Clear,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(DefaultCornerRadius)
                )
                
                when {
                    // Show loading state
                    isLoading && orders.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    
                    // Show error state
                    errorMessage != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(AppTheme.spacing.medium.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = AppIcons.Filled.Error,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                )
                                
                                Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
                                
                                Text(
                                    text = errorMessage ?: "Failed to load orders",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(AppTheme.spacing.large.dp))
                                
                                FilledTonalIconButton(onClick = { viewModel.loadOrders() }) {
                                    Icon(
                                        imageVector = AppIcons.Filled.Refresh,
                                        contentDescription = "Retry"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    
                    // Show empty state
                    orders.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(AppTheme.spacing.medium.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = AppIcons.Filled.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                                
                                Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
                                
                                Text(
                                    text = if (searchQuery.isNotEmpty()) 
                                        "No orders matching '$searchQuery'" 
                                    else 
                                        "You don't have any orders yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    // Show orders list
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = AppTheme.spacing.medium.dp,
                                vertical = AppTheme.spacing.small.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium.dp)
                        ) {
                            items(orders) { order ->
                                OrderCard(
                                    order = order,
                                    onClick = { order.orderNumber?.let { onNavigateToOrderDetails(it) } }
                                )
                            }
                            
                            // Bottom spacing
                            item {
                                Spacer(modifier = Modifier.height(AppTheme.spacing.large.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Top app bar for the Orders screen with large title (iOS style)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onFilterClick: () -> Unit
) {
    LargeTopAppBar(
        title = { Text("My Orders") },
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = AppIcons.Filled.FilterList,
                    contentDescription = "Filter"
                )
            }
        }
    )
}

/**
 * Card component for displaying an order
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCard(
    order: OrderWithUserId,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(DefaultCornerRadius)
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
                Text(
                    text = "Order #${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                OrderStatusChip(status = order.status ?: "Unknown")
            }
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            // Order details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    
                    Text(
                        text = "R ${order.amount?.toString() ?: "0.00"}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    
                    Text(
                        text = formatDate(order.createdAt ?: ""),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
            
            // Reference
            if (!order.reference.isNullOrBlank()) {
                Text(
                    text = "Ref: ${order.reference}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Chip component for displaying order status
 */
@Composable
fun OrderStatusChip(status: String) {
    val (backgroundColor, contentColor) = when (status.lowercase()) {
        "completed" -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        "pending" -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        "processing" -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        "cancelled" -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = CircleShape,
        modifier = Modifier.clip(CircleShape)
    ) {
        Text(
            text = status.capitalize(),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

