package com.lswmobile.app.ui.screens.marketplace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import com.lswmobile.app.data.sample.SampleMarketplaceRepository
import com.lswmobile.app.network.model.Farmaland
import com.lswmobile.app.network.model.ProductFarmland
import com.lswmobile.app.ui.components.PullToRefreshContainer
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.DefaultCornerRadius
import com.lswmobile.app.ui.theme.NetworkImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Marketplace screen showing available products for investment
 * Supports two product types: Farmland and regular Products
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    repository: SampleMarketplaceRepository,
    onNavigateToNewsScreen: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    // Collect products from the repository
    val productFarmlands by repository.productFarmlands.collectAsState()
    val farmlands by repository.farmlands.collectAsState()
    
    // Collect cart items to show badge count
    val farmlandCartItems by repository.farmlandCart.collectAsState()
    val regularCartItems by repository.regularCart.collectAsState()
    
    // Total cart items for badge
    val totalCartItems = farmlandCartItems.size + regularCartItems.size
    
    // Track pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    
    // Setup tab navigation
    val tabs = listOf("Farmland", "Products")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    
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
                    onRefresh = {
                        isRefreshing = true
                        // Simulate a refresh
                        kotlinx.coroutines.GlobalScope.launch {
                            delay(1500) // Simulate network delay
                            isRefreshing = false
                        }
                    }
                ) {
                    when (page) {
                        // Farmland Tab
                        0 -> FarmlandProductsGrid(
                            products = productFarmlands,
                            onProductClick = { /* Handle product click */ },
                            onAddToCart = { productId ->
                                repository.addProductFarmlandToCart(productId, 1)
                            }
                        )
                        
                        // Regular Products Tab
                        1 -> RegularProductsGrid(
                            products = farmlands,
                            onProductClick = { /* Handle product click */ },
                            onAddToCart = { farmlandId ->
                                repository.addRegularToCart(farmlandId, 1)
                            }
                        )
                    }
                }
            }
        }
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
                    imageVector = AppIcons.Filled.Newspaper,
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
    products: List<ProductFarmland>,
    onProductClick: (String) -> Unit,
    onAddToCart: (String) -> Unit
) {
    if (products.isEmpty()) {
        // Show loading or empty state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
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
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium.dp)
        ) {
            items(products) { product ->
                ProductFarmlandCard(
                    product = product,
                    onClick = { onProductClick(product._id) },
                    onAddToCart = { onAddToCart(product._id) }
                )
            }
        }
    }
}

/**
 * Grid of regular Farmland items
 */
@Composable
private fun RegularProductsGrid(
    products: List<Farmaland>,
    onProductClick: (String) -> Unit,
    onAddToCart: (String) -> Unit
) {
    if (products.isEmpty()) {
        // Show loading or empty state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
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
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium.dp)
        ) {
            items(products) { product ->
                FarmlandCard(
                    product = product,
                    onClick = { onProductClick(product._id) },
                    onAddToCart = { onAddToCart(product._id) }
                )
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
    product: ProductFarmland,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DefaultCornerRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column {
            // Product image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            ) {
                // Use our cross-platform NetworkImage component
                NetworkImage(
                    url = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
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
                
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status pill
                    val statusColor = when (product.status) {
                        "available" -> MaterialTheme.colorScheme.tertiary
                        "limited" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.error
                    }
                    
                    Text(
                        text = product.status.capitalize(),
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
                    
                    // Add to cart button
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(DefaultCornerRadius))
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = AppIcons.Filled.Add,
                            contentDescription = "Add to Cart",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
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
    product: Farmaland,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DefaultCornerRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column {
            // Product image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            ) {
                // Use our cross-platform NetworkImage component
                NetworkImage(
                    url = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Price tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(AppTheme.spacing.small.dp)
                        .clip(RoundedCornerShape(DefaultCornerRadius))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f))
                        .padding(
                            horizontal = AppTheme.spacing.small.dp,
                            vertical = AppTheme.spacing.extraSmall.dp
                        )
                ) {
                    Text(
                        text = "R${product.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )
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
                
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(AppTheme.spacing.small.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type pill for regular products
                    Text(
                        text = "Product",
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
                    
                    // Add to cart button
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(DefaultCornerRadius))
                            .background(MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(
                            imageVector = AppIcons.Filled.Add,
                            contentDescription = "Add to Cart",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(20.dp)
                        )
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

@Preview
@Composable
private fun MarketplaceScreenPreview() {
    val repository = SampleMarketplaceRepository.getInstance()
    
    MarketplaceScreen(
        repository = repository,
        onNavigateToNewsScreen = {},
        onNavigateToCheckout = {}
    )
}
