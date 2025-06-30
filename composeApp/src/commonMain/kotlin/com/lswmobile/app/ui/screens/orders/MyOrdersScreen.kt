package com.lswmobile.app.ui.screens.orders

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.lswmobile.app.data.sample.SampleOrdersRepository
import com.lswmobile.app.network.model.OrderItem
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

/**
 * Screen showing the user's orders
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    repository: SampleOrdersRepository,
    onNavigateToOrderDetails: (String) -> Unit
) {
    // Collect orders from the repository
    val orders by repository.orders.collectAsState(initial = emptyList())
    
    // Track pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    
    // Search query state
    var searchQuery by remember { mutableStateOf("") }
    
    // Filter orders based on search query
    val filteredOrders = remember(orders, searchQuery) {
        if (searchQuery.isBlank()) {
            orders
        } else {
            orders.filter { order ->
                order.productId.contains(searchQuery, ignoreCase = true) ||
                order._id.contains(searchQuery, ignoreCase = true) ||
                order.status.contains(searchQuery, ignoreCase = true)
            }
        }
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
                // Simulate a refresh
                kotlinx.coroutines.GlobalScope.launch {
                    delay(1500) // Simulate network delay
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
                    onValueChange = { searchQuery = it },
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
                            IconButton(onClick = { searchQuery = "" }) {
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
                
                if (filteredOrders.isEmpty()) {
                    // Empty state
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
                } else {
                    // Orders list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = AppTheme.spacing.medium.dp,
                            vertical = AppTheme.spacing.small.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium.dp)
                    ) {
                        items(filteredOrders) { order ->
                            OrderCard(
                                order = order,
                                onClick = { onNavigateToOrderDetails(order._id) }
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

/**
 * Top app bar for the Orders screen with large title (iOS style)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrdersTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onFilterClick: () -> Unit
) {
    LargeTopAppBar(
        title = {
            Text(
                text = "My Orders",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        actions = {
            FilledTonalIconButton(
                onClick = onFilterClick,
                modifier = Modifier.padding(end = AppTheme.spacing.small.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Filled.FilterList,
                    contentDescription = "Filter Orders"
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Card component for displaying an order
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderCard(
    order: OrderItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppTheme.spacing.medium.dp,
                vertical = AppTheme.spacing.small.dp
            ),
        shape = RoundedCornerShape(DefaultCornerRadius),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.medium.dp)
        ) {
            // Top row with order ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order._id.takeLast(6)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                OrderStatusChip(status = order.status)
            }
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
            
            // Order details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Order info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Product ID: ${order.productId}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Amount: $${order.amount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Created: ${formatDate(order.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Arrow icon
                Icon(
                    imageVector = AppIcons.Filled.ArrowForward,
                    contentDescription = "View Details",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Chip component for displaying order status
 */
@Composable
private fun OrderStatusChip(status: String) {
    // Define explicit types to avoid ambiguous destructuring
    val statusInfo: Pair<Color, ImageVector> = when (status.lowercase()) {
        "completed" -> Pair(MaterialTheme.colorScheme.tertiary, AppIcons.Filled.Check)
        "processing" -> Pair(MaterialTheme.colorScheme.primary, AppIcons.Filled.Schedule)
        "pending" -> Pair(MaterialTheme.colorScheme.secondary, AppIcons.Filled.Schedule)
        else -> Pair(MaterialTheme.colorScheme.error, AppIcons.Filled.Error)
    }
    
    val color = statusInfo.first
    val icon = statusInfo.second
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.small.dp,
                vertical = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = status.capitalize(),
                style = MaterialTheme.typography.labelMedium,
                color = color
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

/**
 * Extension function to capitalize a string
 */
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

@Preview
@Composable
private fun MyOrdersScreenPreview() {
    val repository = SampleOrdersRepository.getInstance()
    
    MyOrdersScreen(
        repository = repository,
        onNavigateToOrderDetails = {}
    )
}
