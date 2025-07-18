package com.lswmobile.app.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.OrderItem
import com.lswmobile.app.network.model.OrderWithFullUser
import com.lswmobile.app.ui.components.BackButton
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius
import kotlinx.datetime.toLocalDateTime

/**
 * Screen to display detailed information about a single order
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    viewModel: OrderViewModel,
    orderNumber: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEftPayment: (Int) -> Unit,
    onNavigateToDebitPayment: (Int) -> Unit,
    onNavigateToWalletPayment: (Int) -> Unit
) {
    val selectedOrder by viewModel.selectedOrder.collectAsState()
    val isLoading by viewModel.isLoadingOrderDetail.collectAsState()
    val errorMessage by viewModel.orderDetailErrorMessage.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()
    val isProcessingWalletPayment by viewModel.isProcessingWalletPayment.collectAsState()
    
    // Payment state
    var showPaymentBottomSheet by remember { mutableStateOf(false) }
    var showWalletConfirmationDialog by remember { mutableStateOf(false) }
    var showInsufficientBalanceDialog by remember { mutableStateOf(false) }
    
    // Load order details and wallet balance on initial composition
    LaunchedEffect(orderNumber) {
        viewModel.loadOrderDetails(orderNumber)
        viewModel.loadWalletBalance()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order #$orderNumber") },
                navigationIcon = {
                    BackButton(onClick = onNavigateBack)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                errorMessage != null -> {
                    // Error state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(AppTheme.spacing.medium.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.Filled.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = errorMessage ?: "Failed to load order details",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        FilledTonalIconButton(onClick = { viewModel.loadOrderDetails(orderNumber) }) {
                            Icon(
                                imageVector = AppIcons.Filled.Refresh,
                                contentDescription = "Retry"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retry")
                        }
                    }
                }
                
                selectedOrder != null -> {
                    // Order details
                    OrderDetailContent(
                        order = selectedOrder!!,
                        onPayClick = { showPaymentBottomSheet = true }
                    )
                }
                
                else -> {
                    // Empty state (should not happen)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No order details found",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
        
        // Payment Bottom Sheet
        if (showPaymentBottomSheet) {
            PaymentBottomSheet(
                order = selectedOrder,
                onDismiss = { showPaymentBottomSheet = false },
                onWalletPayment = {
                    showPaymentBottomSheet = false
                    // Check wallet balance first
                    if (selectedOrder != null) {
                        if (walletBalance >= selectedOrder!!.amount) {
                            showWalletConfirmationDialog = true
                        } else {
                            showInsufficientBalanceDialog = true
                        }
                    }
                },
                onDebitPayment = {
                    showPaymentBottomSheet = false
                    onNavigateToDebitPayment(orderNumber)
                },
                onEftPayment = {
                    showPaymentBottomSheet = false
                    onNavigateToEftPayment(orderNumber)
                }
            )
        }
        
                // Wallet Confirmation Dialog
        if (showWalletConfirmationDialog && selectedOrder != null) {
            WalletConfirmationDialog(
                order = selectedOrder!!,
                walletBalance = walletBalance,
                onConfirm = {
                    showWalletConfirmationDialog = false
                    if (selectedOrder != null) {
                        viewModel.processWalletPayment(orderNumber, selectedOrder!!.amount)
                    }
                },
                onDismiss = { showWalletConfirmationDialog = false }
            )
        }
        
        // Insufficient Balance Dialog
        if (showInsufficientBalanceDialog && selectedOrder != null) {
            InsufficientBalanceDialog(
                order = selectedOrder!!,
                walletBalance = walletBalance,
                onEftPayment = {
                    showInsufficientBalanceDialog = false
                    onNavigateToEftPayment(orderNumber)
                },
                onDebitPayment = {
                    showInsufficientBalanceDialog = false
                    onNavigateToDebitPayment(orderNumber)
                },
                onDismiss = { showInsufficientBalanceDialog = false }
            )
        }
    }
}

/**
 * Content of the order detail screen
 */
@Composable
fun OrderDetailContent(
    order: OrderWithFullUser,
    onPayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppTheme.spacing.medium.dp)
    ) {
        // Order status card
        StatusCard(order)
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
        
        // Order details card
        DetailsCard(order)
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
        
        // Items card
        OrderItemsSection(order)
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
        
        // Payment details card
        PaymentCard(order)
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
        
        // Payment action button for pending orders
        if (order.status?.lowercase() == "pending" && order.selectedPaymentMethod.isNullOrEmpty()) {
            PaymentActionButton(
                order = order,
                onClick = onPayClick
            )
        }
    }
}

/**
 * Payment action button for pending orders
 */
@Composable
fun PaymentActionButton(
    order: OrderWithFullUser,
    onClick: () -> Unit
) {
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
                .padding(AppTheme.spacing.medium.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Payment Required",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Text(
                text = "Order #${order.orderNumber} is pending payment",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
            
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = AppIcons.Filled.Wallet,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pay Now")
            }
        }
    }
}

/**
 * Payment options bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    order: OrderWithFullUser?,
    onDismiss: () -> Unit,
    onWalletPayment: () -> Unit,
    onDebitPayment: () -> Unit,
    onEftPayment: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.medium.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Choose Payment Method",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = AppIcons.Filled.Clear,
                        contentDescription = "Close"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
            
            // Payment amount
            if (order != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.spacing.medium.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Amount",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "R ${order.amount}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(AppTheme.spacing.medium.dp))
            }
            
            // Payment options
            PaymentOptionItem(
                icon = AppIcons.Filled.Wallet,
                title = "Wallet Payment",
                subtitle = "Pay using your wallet balance",
                onClick = onWalletPayment
            )
            
            PaymentOptionItem(
                icon = AppIcons.Filled.Receipt,
                title = "Debit Payment",
                subtitle = "Pay with your bank card",
                onClick = onDebitPayment
            )
            
            PaymentOptionItem(
                icon = AppIcons.Filled.Description,
                title = "EFT Payment",
                subtitle = "Pay via bank transfer",
                onClick = onEftPayment
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.large.dp))
        }
    }
}

/**
 * Individual payment option item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.medium.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(AppTheme.spacing.medium.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = AppIcons.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Wallet confirmation dialog
 */
@Composable
fun WalletConfirmationDialog(
    order: OrderWithFullUser,
    walletBalance: Double,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirm Wallet Payment",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = AppIcons.Filled.Wallet,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Your available wallet balance is R${walletBalance}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Pay R${order.amount} from your wallet?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Confirm Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Insufficient balance dialog
 */
@Composable
fun InsufficientBalanceDialog(
    order: OrderWithFullUser,
    walletBalance: Double,
    onEftPayment: () -> Unit,
    onDebitPayment: () -> Unit,
    onDismiss: () -> Unit
) {
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Insufficient Balance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = AppIcons.Filled.Error,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Your wallet balance (R${walletBalance}) is insufficient to pay R${order.amount}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Please choose an alternative payment method:",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Column {
                Button(
                    onClick = onEftPayment,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = AppIcons.Filled.Description,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pay with EFT")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = onDebitPayment,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = AppIcons.Filled.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pay with Debit Card")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Display order items section
 */
@Composable
private fun OrderItemsSection(order: OrderWithFullUser) {
    // Debug logging to see what's happening with items
    println("OrderDetailScreen: Displaying items for order #${order.orderNumber}")
    println("OrderDetailScreen: Number of items: ${order.items.size}")
    order.items.forEachIndexed { index, item ->
        println("OrderDetailScreen: Item $index - ID: ${item._id}, ProductType: ${item.productType}, Price: ${item.priceOfAsset}")
    }
    
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
                text = "Order Items",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Divider()
            
            if (order.items.isEmpty()) {
                Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
                Text(
                    text = "No items in this order",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                println("OrderDetailScreen: No items found in order")
            } else {
                order.items.forEach { item ->
                    OrderItemCard(item)
                }
            }
        }
    }
}

/**
 * Card showing order status
 */
@Composable
fun StatusCard(order: OrderWithFullUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DefaultCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.medium.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val statusColor = when (order.status?.lowercase()) {
                "completed" -> MaterialTheme.colorScheme.tertiary
                "pending" -> MaterialTheme.colorScheme.primary
                "processing" -> MaterialTheme.colorScheme.secondary
                "cancelled" -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (order.status?.lowercase()) {
                    "completed" -> AppIcons.Filled.CheckCircle
                    "pending", "processing" -> AppIcons.Filled.Schedule
                    else -> AppIcons.Filled.Error
                }
                
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = order.status?.uppercase() ?: "UNKNOWN",
                style = MaterialTheme.typography.titleLarge,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Order #${order.orderNumber}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (order.reference != null) {
                Text(
                    text = "Ref: ${order.reference}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Card showing order details
 */
@Composable
fun DetailsCard(order: OrderWithFullUser) {
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
                text = "Order Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            DetailRow("Order Date", formatFullDate(order.createdAt ?: ""))
            DetailRow("Payment Method", order.selectedPaymentMethod ?: "Not specified")
            DetailRow("Payment Type", order.paymentType ?: "Not specified")
            DetailRow("Auto Reinvest", if (order.isAutoReinvest) "Yes" else "No")
        }
    }
}

/**
 * Card showing order items
 */
@Composable
fun ItemsCard(order: OrderWithFullUser) {
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
                text = "Items",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Divider()
            
            if (order.items.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
                Text(
                    text = "No items found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                order.items.forEach { item ->
                    OrderItemCard(item)
                }
            }
        }
    }
}

/**
 * Display a single order item in a card
 */
@Composable
private fun OrderItemCard(item: OrderItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(DefaultCornerRadius),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Product: ${item.productType.capitalize()}",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Item ID: ${item._id}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "R ${item.priceOfAsset}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Text(
                text = "Status: ${if (item.isUnallocated) "Unallocated" else "Allocated"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Show current asset value if available
            item.assetValue?.current?.let { currentValue ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Current Value:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "R $currentValue",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Show payment status if available
            item.orderPaymentStatus?.let { paymentStatus ->
                Text(
                    text = "Payment Status: ${paymentStatus.capitalize()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Card showing payment details
 */
@Composable
fun PaymentCard(order: OrderWithFullUser) {
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
                text = "Payment Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Amount",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "R ${order.amount}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Helper function to display a detail row with label and value
 */
@Composable
fun DetailRow(label: String, value: String) {
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
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Format a date to a readable full string
 */
private fun formatFullDate(date: String): String {
    return try {
        val dateTime = kotlinx.datetime.Instant.parse(date)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        
        val day = dateTime.date.dayOfMonth
        val month = getFullMonthName(dateTime.date.monthNumber)
        val year = dateTime.date.year
        val hour = dateTime.time.hour.toString().padStart(2, '0')
        val minute = dateTime.time.minute.toString().padStart(2, '0')
        
        "$day $month $year, $hour:$minute"
    } catch (e: Exception) {
        "Invalid date"
    }
}

/**
 * Get full month name from month number
 */
private fun getFullMonthName(month: Int): String {
    return when (month) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> "Unknown"
    }
}
