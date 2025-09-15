package com.lswmobile.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.lswmobile.app.ui.theme.AppIcons

/**
 * Sealed class representing all possible navigation destinations in the app
 */
sealed class Screen(
    val route: String,
    val titleRes: String,
    val hasBottomBar: Boolean = true,
    val isRootScreen: Boolean = false
) {
    // Main bottom navigation screens (root level)
    object MarketPlace : Screen("marketplace", "Marketplace", isRootScreen = true)
    object MyOrders : Screen("my_orders", "My Orders", isRootScreen = true)
    object Wallet : Screen("wallet", "Wallet", isRootScreen = true)
    object Profile : Screen("profile", "Profile", isRootScreen = true)
    
    // Market graph
    object NewsFeed : Screen("news_feed", "News Feed", hasBottomBar = true)
    object Checkout : Screen("checkout", "Checkout", hasBottomBar = false)
    
    // Orders graph
    object ViewOrder : Screen("view_order", "Order Details", hasBottomBar = false)
    
    // Finance graph
    object MyPortfolio : Screen("my_portfolio", "My Portfolio", hasBottomBar = true)
    object MyAssets : Screen("my_assets", "My Assets", hasBottomBar = true)
    object MyStatement : Screen("my_statement", "My Statement", hasBottomBar = true)
    object RequestWithdrawal : Screen("request_withdrawal", "Request Withdrawal", hasBottomBar = false)
    object MyWithdrawals : Screen("my_withdrawals", "My Withdrawals", hasBottomBar = true)
    object ViewWithdrawal : Screen("view_withdrawal", "Withdrawal Details", hasBottomBar = false)
    object AddBeneficiary : Screen("add_beneficiary", "Add Beneficiary", hasBottomBar = false)
    object MyBeneficiaries : Screen("my_beneficiaries", "My Beneficiaries", hasBottomBar = true)
    object EftPayment : Screen("eft_payment/{orderNumber}", "EFT Payment", hasBottomBar = false)
    object DebitPayment : Screen("debit_payment/{orderNumber}", "Debit Payment", hasBottomBar = false)
    
    // Profile graph
    object AccountVerification : Screen("account_verification", "Account Verification", hasBottomBar = false)
    object KycProcess : Screen("kyc_process", "KYC Process", hasBottomBar = false)
    object KycInformation : Screen("kyc_information", "KYC Information", hasBottomBar = false)
    object UpdateProfile : Screen("update_profile", "Update Profile", hasBottomBar = false)
    object UploadAvatar : Screen("upload_avatar", "Upload Profile Picture", hasBottomBar = false)
    
    // Global screens
    object NoInternet : Screen("no_internet", "No Internet Connection", hasBottomBar = false)
    object WebView : Screen("web_view", "Web View", hasBottomBar = false)
    object Logout : Screen("logout", "Logout", hasBottomBar = false)
}

/**
 * Extension function to get the icon for a bottom nav item
 * @param selected Whether the item is currently selected
 * @return The appropriate icon
 */
@Composable
fun Screen.icon(selected: Boolean): ImageVector {
    return when (this) {
        Screen.MarketPlace -> if (selected) AppIcons.Filled.Home else AppIcons.Outlined.Home
        Screen.MyOrders -> if (selected) AppIcons.Filled.List else AppIcons.Outlined.List
        Screen.Wallet -> if (selected) AppIcons.Filled.Wallet else AppIcons.Outlined.Wallet
        Screen.Profile -> if (selected) AppIcons.Filled.AccountCircle else AppIcons.Outlined.AccountCircle
        Screen.NewsFeed -> if (selected) AppIcons.Filled.Newspaper else AppIcons.Outlined.Newspaper
        Screen.Checkout -> AppIcons.Filled.ShoppingCart
        else -> AppIcons.Filled.Home // Default icon
    }
}

/**
 * Groups screens into navigation graphs
 */
object NavigationGraphs {
    val marketGraph = listOf(
        Screen.MarketPlace,
        Screen.NewsFeed,
        Screen.Checkout
    )
    
    val ordersGraph = listOf(
        Screen.MyOrders,
        Screen.ViewOrder
    )
    
    val financeGraph = listOf(
        Screen.Wallet,
        Screen.MyPortfolio,
        Screen.MyAssets,
        Screen.MyStatement,
        Screen.RequestWithdrawal,
        Screen.MyWithdrawals,
        Screen.ViewWithdrawal,
        Screen.AddBeneficiary,
        Screen.MyBeneficiaries,
        Screen.EftPayment,
        Screen.DebitPayment
    )
    
    val profileGraph = listOf(
        Screen.Profile,
        Screen.AccountVerification,
        Screen.KycProcess,
        Screen.KycInformation,
        Screen.UpdateProfile,
        Screen.UploadAvatar
    )
    
    val bottomNavItems = listOf(
        Screen.MarketPlace,
        Screen.MyOrders,
        Screen.Wallet,
        Screen.Profile
    )
    
    val drawerItems = listOf(
        Screen.MarketPlace,
        Screen.NewsFeed,
        Screen.MyOrders,
        Screen.Wallet,
        Screen.MyPortfolio,
        Screen.MyAssets,
        Screen.MyStatement,
        Screen.MyWithdrawals,
        Screen.MyBeneficiaries,
        Screen.Profile
    )
}
