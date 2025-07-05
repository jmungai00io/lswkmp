package com.lswmobile.app.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lswmobile.app.AppInitializer
import com.lswmobile.app.data.sample.SampleFinanceRepository
import com.lswmobile.app.ui.components.AdaptiveScaffold
import com.lswmobile.app.ui.screens.marketplace.MarketplaceScreen
import com.lswmobile.app.ui.screens.orders.MyOrdersScreen
import com.lswmobile.app.ui.screens.orders.OrderDetailScreen
import com.lswmobile.app.ui.screens.orders.OrderViewModel
import com.lswmobile.app.ui.screens.profile.ProfileScreen
import com.lswmobile.app.ui.screens.wallet.WalletScreen
import com.lswmobile.app.ui.theme.LivestockWealthTheme
import com.lswmobile.app.ui.utils.rememberWindowSizeInfo
import com.lswmobile.app.ui.screens.cart.CartScreen
import com.lswmobile.app.ui.screens.profile.KycProcessScreen
import com.lswmobile.app.viewmodel.UserViewModel
import org.koin.compose.koinInject

/**
 * Main container for the app after authentication
 * Handles adaptive navigation based on screen size and provides the
 * appropriate screen content
 */
@Composable
fun MainAppContainer() {
    // Get the ViewModels from Koin DI
    val marketplaceViewModel = koinInject<com.lswmobile.app.ui.screens.marketplace.MarketplaceViewModel>()
    val orderViewModel = koinInject<OrderViewModel>()
    val userViewModel = koinInject<UserViewModel>()
    
    val financeRepo = remember { SampleFinanceRepository.getInstance() }
    
    // Save only the route name string instead of the Screen object
    var currentRoute by rememberSaveable { mutableStateOf(Screen.MarketPlace.route) }
    
    // Selected order number for detail view
    var selectedOrderNumber by rememberSaveable { mutableStateOf<Int?>(null) }
    
    // Convert the route string to a Screen object
    val currentScreen = remember(currentRoute) {
        when (currentRoute) {
            Screen.MarketPlace.route -> Screen.MarketPlace
            Screen.MyOrders.route -> Screen.MyOrders
            Screen.Wallet.route -> Screen.Wallet
            Screen.Profile.route -> Screen.Profile
            Screen.NewsFeed.route -> Screen.NewsFeed
            Screen.Checkout.route -> Screen.Checkout
            Screen.ViewOrder.route -> Screen.ViewOrder
            Screen.MyPortfolio.route -> Screen.MyPortfolio
            Screen.MyAssets.route -> Screen.MyAssets
            Screen.MyStatement.route -> Screen.MyStatement
            Screen.RequestWithdrawal.route -> Screen.RequestWithdrawal
            Screen.MyWithdrawals.route -> Screen.MyWithdrawals
            Screen.ViewWithdrawal.route -> Screen.ViewWithdrawal
            Screen.KycProcess.route -> Screen.KycProcess
            Screen.UpdateUser.route -> Screen.UpdateUser
            Screen.UploadAvatar.route -> Screen.UploadAvatar
            else -> Screen.MarketPlace // Default fallback
        }
    }
    
    // Function to update the current screen by saving the route
    val onScreenSelected = { screen: Screen ->
        currentRoute = screen.route
    }
    
    // Fetch user data when the container is first loaded
    LaunchedEffect(Unit) {
        println("MainAppContainer: Fetching user data after authentication")
        userViewModel.fetchUser()
    }
    
    // Apply our custom theme
    LivestockWealthTheme {
        // BoxWithConstraints allows us to measure available space and adapt UI
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            // Calculate window size information for responsive UI
            val windowSizeInfo = rememberWindowSizeInfo(
                width = maxWidth,
                height = maxHeight
            )
            
            // Our adaptive scaffold handles different screen sizes automatically
            AdaptiveScaffold(
                windowSizeInfo = windowSizeInfo,
                selectedScreen = currentScreen,
                onScreenSelected = onScreenSelected
            ) {
                // Content for the current screen
                when (currentScreen) {
                    // Main bottom nav screens
                    Screen.MarketPlace -> {
                        MarketplaceScreen(
                            viewModel = marketplaceViewModel,
                            onNavigateToNewsScreen = { onScreenSelected(Screen.NewsFeed) },
                            onNavigateToCheckout = { onScreenSelected(Screen.Checkout) }
                        )
                    }
                    
                    Screen.MyOrders -> {
                        MyOrdersScreen(
                            viewModel = orderViewModel,
                            onNavigateToOrderDetails = { orderNumber ->
                                selectedOrderNumber = orderNumber
                                onScreenSelected(Screen.ViewOrder)
                            }
                        )
                    }
                    
                    Screen.Wallet -> {
                        WalletScreen(
                            repository = financeRepo,
                            onNavigateToPortfolio = { onScreenSelected(Screen.MyPortfolio) },
                            onNavigateToAssets = { onScreenSelected(Screen.MyAssets) },
                            onNavigateToStatement = { onScreenSelected(Screen.MyStatement) },
                            onNavigateToWithdrawals = { onScreenSelected(Screen.MyWithdrawals) },
                            onRequestWithdrawal = { onScreenSelected(Screen.RequestWithdrawal) }
                        )
                    }
                    
                    Screen.Profile -> {
                        val user by userViewModel.user.collectAsState()
                        val isLoading by userViewModel.isLoading.collectAsState()
                        val error by userViewModel.error.collectAsState()
                        val isKYCVerified by userViewModel.isKYCVerified.collectAsState()
                        
                        ProfileScreen(
                            user = user,
                            isLoading = isLoading,
                            error = error,
                            isKYCVerified = isKYCVerified,
                            onNavigateToKycProcess = { onScreenSelected(Screen.KycProcess) },
                            onNavigateToUpdateUser = { onScreenSelected(Screen.UpdateUser) },
                            onNavigateToUploadAvatar = { onScreenSelected(Screen.UploadAvatar) },
                            onRefreshUser = { userViewModel.fetchUser() }
                        )
                    }
                    
                    Screen.Checkout -> {
                        val productCartItems by marketplaceViewModel.productCartItems.collectAsState()
                        val farmlandCartItems by marketplaceViewModel.farmlandCartItems.collectAsState()
                        val cartSummary by marketplaceViewModel.cartSummary.collectAsState()
                        
                        // Order states
                        val isOrdering by marketplaceViewModel.isOrdering.collectAsState()
                        val orderMessage by marketplaceViewModel.orderMessage.collectAsState()
                        val orderSuccess by marketplaceViewModel.orderSuccess.collectAsState()
                        
                        CartScreen(
                            productCartItems = productCartItems,
                            farmlandCartItems = farmlandCartItems,
                            cartSummary = cartSummary,
                            onUpdateQuantity = { itemId, quantity ->
                                marketplaceViewModel.updateCartItemQuantity(itemId, quantity)
                            },
                            onRemoveItem = { itemId ->
                                marketplaceViewModel.removeFromCart(itemId)
                            },
                            onClearCart = {
                                marketplaceViewModel.clearCart()
                            },
                            onCheckout = {
                                // Create separate orders for products and farmlands
                                val hasProducts = marketplaceViewModel.productCartItems.value.isNotEmpty()
                                val hasFarmlands = marketplaceViewModel.farmlandCartItems.value.isNotEmpty()
                                
                                if (hasProducts) {
                                    marketplaceViewModel.createProductOrder()
                                }
                                
                                if (hasFarmlands) {
                                    marketplaceViewModel.createFarmlandOrder()
                                }
                                
                                if (!hasProducts && !hasFarmlands) {
                                    println("Checkout pressed but cart is empty")
                                }
                            },
                            isOrdering = isOrdering,
                            orderMessage = orderMessage,
                            orderSuccess = orderSuccess,
                            onDismissOrderDialog = {
                                marketplaceViewModel.resetOrderState()
                                // Clear cart if order was successful
                                if (orderSuccess) {
                                    marketplaceViewModel.clearCart()
                                }
                            }
                        )
                    }
                   
                    Screen.ViewOrder -> {
                        // Only show order detail if we have a valid order number
                        selectedOrderNumber?.let { orderNumber ->
                            OrderDetailScreen(
                                viewModel = orderViewModel,
                                orderNumber = orderNumber,
                                onNavigateBack = {
                                    // Go back to order list
                                    onScreenSelected(Screen.MyOrders)
                                }
                            )
                        } ?: run {
                            // Fallback if no order number is provided
                            ScreenUnderConstruction(currentScreen.titleRes)
                        }
                    }

                    Screen.KycProcess -> {
                        val user by userViewModel.user.collectAsState()
                        val isLoading by userViewModel.isLoading.collectAsState()
                        val error by userViewModel.error.collectAsState()
                        val isKYCVerified by userViewModel.isKYCVerified.collectAsState()
                        
                        KycProcessScreen(
                            user = user,
                            isLoading = isLoading,
                            error = error,
                            isKYCVerified = isKYCVerified,
                            onNavigateBack = { onScreenSelected(Screen.Profile) },
                            onRefreshUser = { userViewModel.fetchUser() }
                        )
                    }
                    
                    // Other screens would be implemented in a real app
                    // For now, just show a placeholder
                    else -> {
                        ScreenUnderConstruction(currentScreen.titleRes)
                    }
                }
            }
        }
    }
}

/**
 * Simple placeholder for screens that are not yet implemented
 */
@Composable
private fun ScreenUnderConstruction(screenTitle: String) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "$screenTitle screen is under construction",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
        )
    }
}
