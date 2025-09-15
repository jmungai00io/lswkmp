package com.lswmobile.app.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lswmobile.app.data.model.CartItem
import com.lswmobile.app.data.model.CartItemType
import com.lswmobile.app.data.model.CartSummary
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius

/**
 * Cart screen showing all cart items with separate sections for products and farmlands
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    productCartItems: List<CartItem>,
    farmlandCartItems: List<CartItem>,
    cartSummary: CartSummary,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: () -> Unit,
    isOrdering: Boolean = false,
    orderMessage: String? = null,
    orderSuccess: Boolean = false,
    onDismissOrderDialog: () -> Unit = {},
    onBack: () -> Unit
) {
    // Show order dialog if there's an order message or if ordering is in progress
    val showOrderDialog = isOrdering || orderMessage != null
    
    // Display order dialog if needed
    if (showOrderDialog) {
        AlertDialog(
            onDismissRequest = {
                // Only allow dismissing if not in loading state
                if (!isOrdering) {
                    onDismissOrderDialog()
                }
            },
            title = {
                Text(
                    if (isOrdering) "Processing Order"
                    else if (orderSuccess) "Order Successful"
                    else "Order Status"
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isOrdering) {
                        // Show loading indicator
                        CircularProgressIndicator()
                        Text("Processing your order...")
                    } else {
                        // Show order message
                        Text(orderMessage ?: "")
                        
                        if (orderSuccess) {
                            Icon(
                                imageVector = AppIcons.Filled.CheckCircle,
                                contentDescription = "Success",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                        } else {
                            Icon(
                                imageVector = AppIcons.Filled.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                // Only show button if not loading
                if (!isOrdering) {
                    TextButton(
                        onClick = onDismissOrderDialog
                    ) {
                        Text("OK")
                    }
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Shopping Cart",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = AppIcons.Filled.Back,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (cartSummary.itemCount > 0) {
                        TextButton(onClick = onClearCart) {
                            Text("Clear All")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (cartSummary.itemCount > 0) {
                CartBottomBar(
                    cartSummary = cartSummary,
                    onCheckout = onCheckout
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (cartSummary.itemCount == 0) {
                item {
                    EmptyCartMessage()
                }
            } else {
                // Products section
                if (productCartItems.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Products",
                            itemCount = productCartItems.size
                        )
                    }
                    
                    items(productCartItems) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onUpdateQuantity = onUpdateQuantity,
                            onRemoveItem = onRemoveItem
                        )
                    }
                }
                
                // Farmlands section
                if (farmlandCartItems.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Farmlands",
                            itemCount = farmlandCartItems.size
                        )
                    }
                    
                    items(farmlandCartItems) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onUpdateQuantity = onUpdateQuantity,
                            onRemoveItem = onRemoveItem
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCartMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = AppIcons.Filled.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Your cart is empty",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Add some products or farmlands to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    itemCount: Int
) {
    Text(
        text = "$title ($itemCount)",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DefaultCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Item details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = cartItem.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    cartItem.location?.let { location ->
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    cartItem.profitRate?.let { rate ->
                        Text(
                            text = "Profit Rate: $rate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = "R${cartItem.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Remove button
                IconButton(onClick = { onRemoveItem(cartItem.id) }) {
                    Icon(
                        imageVector = AppIcons.Filled.Clear,
                        contentDescription = "Remove item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Quantity controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quantity:",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (cartItem.quantity > 1) {
                                onUpdateQuantity(cartItem.id, cartItem.quantity - 1)
                            }
                        },
                        enabled = cartItem.quantity > 1
                    ) {
                        Icon(
                            imageVector = AppIcons.Filled.ArrowDownward,
                            contentDescription = "Decrease quantity"
                        )
                    }
                    
                    Text(
                        text = cartItem.quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = {
                            onUpdateQuantity(cartItem.id, cartItem.quantity + 1)
                        }
                    ) {
                        Icon(
                            imageVector = AppIcons.Filled.Add,
                            contentDescription = "Increase quantity"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtotal
            Text(
                text = "Subtotal: R${(cartItem.price * cartItem.quantity).toInt()}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CartBottomBar(
    cartSummary: CartSummary,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total (${cartSummary.itemCount} items):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "R${cartSummary.totalPrice.toInt()}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Button(
                onClick = onCheckout,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Checkout")
            }
        }
    }
} 