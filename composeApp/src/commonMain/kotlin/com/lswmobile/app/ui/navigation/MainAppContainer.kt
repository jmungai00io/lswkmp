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
import com.lswmobile.app.ui.components.AdaptiveScaffold
import com.lswmobile.app.ui.screens.marketplace.MarketplaceScreen
import com.lswmobile.app.ui.screens.orders.MyOrdersScreen
import com.lswmobile.app.ui.screens.orders.OrderDetailScreen
import com.lswmobile.app.ui.screens.orders.OrderViewModel
import com.lswmobile.app.ui.screens.payment.DebitPaymentScreen
import com.lswmobile.app.ui.screens.payment.DebitPaymentViewModel
import com.lswmobile.app.ui.screens.payment.EftPaymentScreen
import com.lswmobile.app.ui.screens.payment.EftPaymentViewModel
import com.lswmobile.app.ui.screens.profile.ProfileScreen
import com.lswmobile.app.ui.screens.wallet.WalletScreen
import com.lswmobile.app.ui.theme.LivestockWealthTheme
import com.lswmobile.app.ui.utils.rememberWindowSizeInfo
import com.lswmobile.app.ui.screens.cart.CartScreen
import com.lswmobile.app.ui.screens.profile.KycProcessScreen
import com.lswmobile.app.ui.screens.profile.KycDocumentUploadScreen
import com.lswmobile.app.ui.screens.profile.AccountVerificationScreen
import com.lswmobile.app.ui.screens.profile.UpdateProfileScreen
import com.lswmobile.app.ui.screens.profile.AddBeneficiaryScreen
import com.lswmobile.app.ui.screens.profile.MyBeneficiariesScreen
import com.lswmobile.app.viewmodel.BeneficiaryViewModel
import com.lswmobile.app.viewmodel.UserViewModel
import com.lswmobile.app.viewmodel.KycViewModel
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
    val beneficiaryViewModel = koinInject<BeneficiaryViewModel>()
    val eftPaymentViewModel = koinInject<EftPaymentViewModel>()
    val debitPaymentViewModel = koinInject<DebitPaymentViewModel>()
    
//    val financeRepo = remember { SampleFinanceRepository.getInstance() }
    
    // Save only the route name string instead of the Screen object
    var currentRoute by rememberSaveable { mutableStateOf(Screen.MarketPlace.route) }
    
    // Selected order number for detail view
    var selectedOrderNumber by rememberSaveable { mutableStateOf<Int?>(null) }
    
    // Payment order number for payment screens
    var paymentOrderNumber by rememberSaveable { mutableStateOf<Int?>(null) }
    
    // Convert the route string to a Screen object
    val currentScreen = remember(currentRoute) {
        when {
            currentRoute.startsWith("eft_payment/") -> Screen.EftPayment
            currentRoute.startsWith("debit_payment/") -> Screen.DebitPayment
            currentRoute == Screen.MarketPlace.route -> Screen.MarketPlace
            currentRoute == Screen.MyOrders.route -> Screen.MyOrders
            currentRoute == Screen.Wallet.route -> Screen.Wallet
            currentRoute == Screen.Profile.route -> Screen.Profile
            currentRoute == Screen.NewsFeed.route -> Screen.NewsFeed
            currentRoute == Screen.Checkout.route -> Screen.Checkout
            currentRoute == Screen.ViewOrder.route -> Screen.ViewOrder
            currentRoute == Screen.MyPortfolio.route -> Screen.MyPortfolio
            currentRoute == Screen.MyAssets.route -> Screen.MyAssets
            currentRoute == Screen.MyStatement.route -> Screen.MyStatement
            currentRoute == Screen.RequestWithdrawal.route -> Screen.RequestWithdrawal
            currentRoute == Screen.MyWithdrawals.route -> Screen.MyWithdrawals
            currentRoute == Screen.ViewWithdrawal.route -> Screen.ViewWithdrawal
            currentRoute == Screen.AccountVerification.route -> Screen.AccountVerification
            currentRoute == Screen.KycProcess.route -> Screen.KycProcess
            currentRoute == Screen.UpdateProfile.route -> Screen.UpdateProfile
            currentRoute == Screen.AddBeneficiary.route -> Screen.AddBeneficiary
            currentRoute == Screen.MyBeneficiaries.route -> Screen.MyBeneficiaries
            currentRoute == Screen.UploadAvatar.route -> Screen.UploadAvatar
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
//                            repository = financeRepo,
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
                            onNavigateToAccountVerification = { onScreenSelected(Screen.AccountVerification) },
                            onNavigateToUpdateProfile = { onScreenSelected(Screen.UpdateProfile) },
                            onNavigateToAddBeneficiary = { onScreenSelected(Screen.AddBeneficiary) },
                            onNavigateToMyBeneficiaries = { onScreenSelected(Screen.MyBeneficiaries) },
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
                            },
                            onBack = { onScreenSelected(Screen.MarketPlace) }
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
                                },
                                onNavigateToEftPayment = { orderNum ->
                                    // Navigate to EFT payment with order number
                                    paymentOrderNumber = orderNum
                                    currentRoute = "eft_payment/$orderNum"
                                },
                                onNavigateToDebitPayment = { orderNum ->
                                    // Navigate to debit payment with order number
                                    paymentOrderNumber = orderNum
                                    currentRoute = "debit_payment/$orderNum"
                                },
                                onNavigateToWalletPayment = { orderNum ->
                                    // Handle wallet payment - this could be a direct API call
                                    // For now, we'll navigate to a wallet payment screen or handle it directly
                                    println("Wallet payment for order #$orderNum")
                                    // You could implement direct wallet payment here
                                }
                            )
                        } ?: run {
                            // Fallback if no order number is provided
                            ScreenUnderConstruction(currentScreen.titleRes)
                        }
                    }
                    
                    Screen.EftPayment -> {
                        // Show EFT payment screen if we have a valid order number
                        paymentOrderNumber?.let { orderNumber ->
                            EftPaymentScreen(
                                orderNumber = orderNumber,
                                viewModel = eftPaymentViewModel,
                                orderViewModel = orderViewModel,
                                onNavigateBack = {
                                    // Go back to order detail
                                    onScreenSelected(Screen.ViewOrder)
                                },
                                onPaymentSuccess = {
                                    // Navigate back to order detail after successful payment
                                    onScreenSelected(Screen.ViewOrder)
                                }
                            )
                        } ?: run {
                            // Fallback if no order number is provided
                            ScreenUnderConstruction(currentScreen.titleRes)
                        }
                    }
                    
                    Screen.DebitPayment -> {
                        // Show Debit payment screen if we have a valid order number
                        paymentOrderNumber?.let { orderNumber ->
                            DebitPaymentScreen(
                                orderNumber = orderNumber,
                                onNavigateBack = {
                                    // Go back to order detail
                                    onScreenSelected(Screen.ViewOrder)
                                },
                                onPaymentSuccess = {
                                    // Navigate back to order detail after successful payment
                                    onScreenSelected(Screen.ViewOrder)
                                },
                                viewModel = debitPaymentViewModel,
                                orderViewModel = orderViewModel
                            )
                        } ?: run {
                            // Fallback if no order number is provided
                            ScreenUnderConstruction(currentScreen.titleRes)
                        }
                    }

                    Screen.AccountVerification -> {
                        val user by userViewModel.user.collectAsState()
                        val kycViewModel = koinInject<KycViewModel>()
                        AccountVerificationScreen(
                            user = user,
                            kycViewModel = kycViewModel,
                            userViewModel = userViewModel,
                            onNavigateBack = { onScreenSelected(Screen.Profile) },
                            onNavigateToKycProcess = { onScreenSelected(Screen.KycProcess) }
                        )
                    }
                    
                    Screen.KycProcess -> {
                        val user by userViewModel.user.collectAsState()
                        val kycViewModel = koinInject<KycViewModel>()
                        KycDocumentUploadScreen(
                            user = user,
                            kycViewModel = kycViewModel,
                            userViewModel = userViewModel,
                            onNavigateBack = { onScreenSelected(Screen.AccountVerification) },
                            onNavigateToKycInfo = { onScreenSelected(Screen.KycProcess) }
                        )
                    }
                    
                    Screen.UpdateProfile -> {
                        val user by userViewModel.user.collectAsState()
                        UpdateProfileScreen(
                            user = user,
                            onNavigateBack = { onScreenSelected(Screen.Profile) }
                        )
                    }
                    
                    Screen.AddBeneficiary -> {
                        AddBeneficiaryScreen(
                            beneficiaryViewModel = beneficiaryViewModel,
                            onNavigateBack = { onScreenSelected(Screen.Profile) },
                            onSuccess = { onScreenSelected(Screen.Profile) }
                        )
                    }
                    
                    Screen.MyBeneficiaries -> {
                        MyBeneficiariesScreen(
                            beneficiaryViewModel = beneficiaryViewModel,
                            onNavigateBack = { onScreenSelected(Screen.Profile) },
                            onNavigateToAddBeneficiary = { onScreenSelected(Screen.AddBeneficiary) },
                            onEditBeneficiary = { beneficiaryId ->
                                // TODO: Implement edit beneficiary logic
                                println("Edit beneficiary: $beneficiaryId")
                            },
                            onDeleteBeneficiary = { beneficiaryId ->
                                // Show confirmation dialog and delete
                                beneficiaryViewModel.deleteBeneficiary(beneficiaryId)
                            }
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
