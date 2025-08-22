package com.lswmobile.app.ui.screens.marketplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lswmobile.app.AppInitializer
import com.lswmobile.app.network.model.Farmland
import com.lswmobile.app.network.model.ProductClassic
import com.lswmobile.app.ui.components.PullToRefreshContainer
import com.lswmobile.app.ui.resources.ResourceHelper
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Marketplace screen showing available products for investment
 * Supports two product types: Farmland and regular Products
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToNewsScreen: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    // Collect products and farmlands from the ViewModel
    val products by viewModel.products.collectAsState()
    val farmlands by viewModel.farmlands.collectAsState()

    // Collect cart items to show badge count
    val productCartItems by viewModel.productCartItems.collectAsState()
    val farmlandCartItems by viewModel.farmlandCartItems.collectAsState()

    // Total cart items for badge
    val totalCartItems by viewModel.totalCartItems.collectAsState()

    // Loading states
    val isLoadingProducts by viewModel.isLoadingProducts.collectAsState()
    val isLoadingFarmlands by viewModel.isLoadingFarmlands.collectAsState()

    // Add preorder state
    val preorderMessage by viewModel.preorderMessage.collectAsState()
    val isPreordering by viewModel.isPreordering.collectAsState()

    // Track pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }

    // Setup tab navigation
    val tabs = listOf("Farmland", "Products")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    // Create LazyGridState for each tab to track scroll position
    val farmlandGridState = rememberLazyGridState()
    val productsGridState = rememberLazyGridState()

    // Check token and load data
    LaunchedEffect(Unit) {
        val token = AppInitializer.getTokenProvider().getAccessToken()
        println("MarketplaceScreen: Current access token: ${token?.take(10)}...")
        viewModel.loadProducts()
        viewModel.loadFarmlands()
    }

    // Setup scrolling behavior for the large title (iOS-style)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MarketplaceTopBar(
                scrollBehavior = scrollBehavior,
                onNewsClick = onNavigateToNewsScreen,
                onCartClick = onNavigateToCheckout,
                cartItemCount = totalCartItems
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCheckout,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                BadgedBox(
                    badge = {
                        if (totalCartItems > 0) {
                            Badge {
                                Text(text = totalCartItems.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = AppIcons.Filled.ShoppingCart,
                        contentDescription = "View Cart"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (pagerState.currentPage == index)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Content Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                PullToRefreshContainer(
                    isRefreshing = isRefreshing,
                    lazyGridState = when (page) {
                        0 -> farmlandGridState
                        1 -> productsGridState
                        else -> null
                    },
                    onRefresh = {
                        isRefreshing = true
                        coroutineScope.launch {
                            when (page) {
                                0 -> viewModel.loadFarmlands()
                                1 -> viewModel.loadProducts()
                            }
                            delay(500) // Small delay for UI feedback
                            isRefreshing = false
                        }
                    }
                ) {
                    when (page) {
                        // Farmland Tab
                        0 -> FarmlandProductsGrid(
                            products = farmlands,
                            isLoading = isLoadingFarmlands,
                            onProductClick = { /* Handle product click */ },
                            onAddToCart = { productId ->
                                viewModel.addFarmlandToCart(productId, 1)

                            },
                            viewModel = viewModel,
                            lazyGridState = farmlandGridState
                        )

                        // Regular Products Tab
                        1 -> RegularProductsGrid(
                            products = products,
                            isLoading = isLoadingProducts,
                            onProductClick = { /* Handle product click */ },
                            onAddToCart = { productId ->
                                viewModel.addProductToCart(productId, 1)
                            },
                            viewModel = viewModel,
                            lazyGridState = productsGridState
                        )
                    }
                }
            }
        }
    }

    // Show preorder feedback dialog
    val openDialog = remember { mutableStateOf(false) }

    // Show dialog when preorder message changes
    LaunchedEffect(preorderMessage) {
        if (preorderMessage != null) {
            openDialog.value = true
        }
    }

    // Preorder dialog
    if (openDialog.value && preorderMessage != null) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                viewModel.resetPreorderState()
            },
            title = { Text("Preorder Status") },
            text = { Text(preorderMessage ?: "") },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        viewModel.resetPreorderState()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Loading dialog for preorders
    if (isPreordering) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Processing") },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Text("Processing your preorder...")
                }
            },
            confirmButton = { }
        )
    }
}

/**
 * Top app bar for the Marketplace screen with large title (iOS style)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarketplaceTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onNewsClick: () -> Unit,
    onCartClick: () -> Unit,
    cartItemCount: Int
) {
    LargeTopAppBar(
        title = {
            Text(
                text = "Marketplace",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        actions = {
            IconButton(onClick = onNewsClick) {
                Icon(
                    imageVector = AppIcons.Outlined.Newspaper,
                    contentDescription = "News Feed"
                )
            }
            IconButton(onClick = onCartClick) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge {
                                Text(text = cartItemCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = AppIcons.Filled.ShoppingCart,
                        contentDescription = "Shopping Cart"
                    )
                }
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
 * Grid of ProductFarmland items
 */
@Composable
private fun FarmlandProductsGrid(
    products: List<Farmland>,
    isLoading: Boolean,
    onProductClick: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    viewModel: MarketplaceViewModel,
    lazyGridState: LazyGridState
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            products.isEmpty() -> Text(
                text = "No farmlands available",
                style = MaterialTheme.typography.bodyLarge
            )
            else -> {
                // Show product grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 280.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = AppTheme.spacing.medium.dp,
                        end = AppTheme.spacing.medium.dp,
                        top = AppTheme.spacing.medium.dp,
                        bottom = 80.dp // Extra padding for FAB
                    ),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium.dp),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium.dp),
                    state = lazyGridState
                ) {
                    items(products) { product ->
                        FarmlandCard(
                            product = product,
                            onClick = { onProductClick(product._id) },
                            onAddToCart = { 
                                // Check if product is out of stock
                                if (product.inStock == false) {
                                    // If farmland is a type of product, get the type for preorder
                                    val productType = product.productType ?: "farmland"
                                    viewModel.preorderProduct(productType)
                                } else {
                                    onAddToCart(product._id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Grid of regular Farmland items
 */
@Composable
private fun RegularProductsGrid(
    products: List<ProductClassic>,
    isLoading: Boolean,
    onProductClick: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    viewModel: MarketplaceViewModel,
    lazyGridState: LazyGridState
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            products.isEmpty() -> Text(
                text = "No products available",
                style = MaterialTheme.typography.bodyLarge
            )
            else -> {
                // Show product grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 280.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = AppTheme.spacing.medium.dp,
                        end = AppTheme.spacing.medium.dp,
                        top = AppTheme.spacing.medium.dp,
                        bottom = 80.dp // Extra padding for FAB
                    ),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium.dp),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium.dp),
                    state = lazyGridState
                ) {
                    items(products) { product ->
                        ProductFarmlandCard(
                            product = product,
                            onClick = { onProductClick(product._id) },
                            onAddToCart = { 
                                // Check if product is out of stock
                                if (product.inStock == false) {
                                    // Get product type (if available) or default to "macadamia"
                                    val productType = product.productType ?: "macadamia"
                                    viewModel.preorderProduct(productType)
                                } else {
                                    onAddToCart(product._id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card component for displaying a ProductFarmland
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductFarmlandCard(
    product: ProductClassic,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DefaultCornerRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            // Product image with stock status indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.8f)
                    .padding(AppTheme.spacing.medium.dp)
            ) {
                Image(
                    painter = ResourceHelper.loadProductImage(product.productType),
                    contentDescription = product.name ?: product.productName ?: "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // Price tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(AppTheme.spacing.small.dp)
                        .clip(RoundedCornerShape(DefaultCornerRadius))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f))
                        .padding(
                            horizontal = AppTheme.spacing.small.dp,
                            vertical = AppTheme.spacing.extraSmall.dp
                        )
                ) {
                    Text(
                        text = "R${product.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Stock status indicator
                if (product.inStock == false) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(AppTheme.spacing.small.dp)
                            .clip(RoundedCornerShape(DefaultCornerRadius))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.85f))
                            .padding(
                                horizontal = AppTheme.spacing.small.dp,
                                vertical = AppTheme.spacing.extraSmall.dp
                            )
                    ) {
                        Text(
                            text = "Out of Stock",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }

            // Product info
            Column(
                modifier = Modifier.padding(AppTheme.spacing.medium.dp)
            ) {
                Text(
                    text = product.name ?: product.productName ?: "Unnamed Product",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(AppTheme.spacing.extraSmall.dp))

                // Location if available
                product.location?.let { location ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Investment details
                Row(
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    product.profitRate?.let { rate ->
                        Text(
                            text = "$rate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    product.profitCycle?.let { cycle ->
                        Text(
                            text = " / $cycle",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Investment term if available
                product.investmentTerm?.let { term ->
                    Text(
                        text = "Term: $term",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status pill - show product type or "available"/"unavailable" based on stock
                    val statusText = product.productType ?: if (product.inStock == true) "Available" else "Unavailable"
                    val statusColor = when {
                        product.inStock == false -> MaterialTheme.colorScheme.error
                        product.productType != null -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.tertiary
                    }

                    Text(
                        text = statusText.capitalize(),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusColor.copy(alpha = 0.1f))
                            .padding(
                                horizontal = AppTheme.spacing.small.dp,
                                vertical = 2.dp
                            )
                    )

                    // Add to cart button or preorder button
                    val isOutOfStock = product.inStock == false

                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.spacing.medium.dp)
                            .padding(bottom = AppTheme.spacing.medium.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOutOfStock)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isOutOfStock) AppIcons.Filled.Alarm else AppIcons.Filled.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(if (isOutOfStock) "Preorder" else "Add to Cart")
                    }
                }
            }
        }
    }
}

/**
 * Card component for displaying a regular Farmland
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FarmlandCard(
    product: Farmland,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DefaultCornerRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            // Product image with stock status indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.8f)
                    .padding(AppTheme.spacing.medium.dp)
            ) {
                Image(
                    painter = ResourceHelper.loadProductImage(product.productType),
                    contentDescription = product.name ?: "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // Price tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(AppTheme.spacing.small.dp)
                        .clip(RoundedCornerShape(DefaultCornerRadius))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f))
                        .padding(
                            horizontal = AppTheme.spacing.small.dp,
                            vertical = AppTheme.spacing.extraSmall.dp
                        )
                ) {
                    Text(
                        text = "R${product.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Stock status indicator
                if (product.inStock == false) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(AppTheme.spacing.small.dp)
                            .clip(RoundedCornerShape(DefaultCornerRadius))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.85f))
                            .padding(
                                horizontal = AppTheme.spacing.small.dp,
                                vertical = AppTheme.spacing.extraSmall.dp
                            )
                    ) {
                        Text(
                            text = "Out of Stock",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }

            // Product info
            Column(
                modifier = Modifier.padding(AppTheme.spacing.medium.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(AppTheme.spacing.extraSmall.dp))

                // Location
                product.location?.let { location ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Profit info
                Row(
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    product.profitRate?.let { rate ->
                        Text(
                            text = "$rate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    product.profitCycle?.let { cycle ->
                        Text(
                            text = " / $cycle",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type pill for farmland products
                    val productType = product.productType ?: "Farmland"
                    Text(
                        text = productType,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                            .padding(
                                horizontal = AppTheme.spacing.small.dp,
                                vertical = 2.dp
                            )
                    )

                    // Add to cart button or preorder button
                    val isOutOfStock = product.inStock == false

                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.spacing.medium.dp)
                            .padding(bottom = AppTheme.spacing.medium.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOutOfStock)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isOutOfStock) AppIcons.Filled.Alarm else AppIcons.Filled.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(if (isOutOfStock) "Preorder" else "Add to Cart")
                    }
                }
            }
        }
    }
}

/**
 * Extension function to capitalize a string
 */
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
