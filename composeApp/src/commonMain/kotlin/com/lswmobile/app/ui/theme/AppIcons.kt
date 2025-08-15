package com.lswmobile.app.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Common-only icon facade: maps to Material Icons directly, no platform providers.
 * Keeps a stable API surface so screens don't change.
 */
object AppIcons {
    object Filled {
        // Navigation
        val Home: ImageVector = Icons.Filled.Home
        val ShoppingCart: ImageVector = Icons.Filled.ShoppingCart
        val Wallet: ImageVector = Icons.Filled.Wallet
        val AccountCircle: ImageVector = Icons.Filled.AccountCircle
        val List: ImageVector = Icons.Filled.List
        val Newspaper: ImageVector = Icons.Filled.Newspaper

        // Actions
        val Add: ImageVector = Icons.Filled.Add
        val ArrowForward: ImageVector = Icons.Filled.ArrowForward
        val ArrowDownward: ImageVector = Icons.Filled.ArrowDownward
        val ArrowUpward: ImageVector = Icons.Filled.ArrowUpward

        // Content
        val Description: ImageVector = Icons.Filled.Description
        val Folder: ImageVector = Icons.Filled.Folder
        val Receipt: ImageVector = Icons.Filled.Receipt

        // Status / misc
        val Check: ImageVector = Icons.Filled.Check
        val Clear: ImageVector = Icons.Filled.Clear
        val Error: ImageVector = Icons.Filled.Error
        val FilterList: ImageVector = Icons.Filled.FilterList
        val Search: ImageVector = Icons.Filled.Search
        val Schedule: ImageVector = Icons.Filled.Schedule
        val LocationOn: ImageVector = Icons.Filled.LocationOn
        val Alarm: ImageVector = Icons.Filled.Alarm
        val CheckCircle: ImageVector = Icons.Filled.CheckCircle
        val Back: ImageVector = Icons.Filled.ArrowBackIosNew
        val Refresh: ImageVector = Icons.Filled.Refresh
        val KeyboardArrowDown: ImageVector = Icons.Filled.KeyboardArrowDown
        val KeyboardArrowUp: ImageVector = Icons.Filled.KeyboardArrowUp
        val Favorite: ImageVector = Icons.Filled.Favorite
        val Close: ImageVector = Icons.Filled.Close
        val ChevronLeft: ImageVector = Icons.Filled.ChevronLeft
        val ChevronRight: ImageVector = Icons.Filled.ChevronRight
    }

    object Outlined {
        // Use MoneyOff as a temporary Portfolio fallback to preserve current behavior.
        val Portfolio: ImageVector = Icons.Outlined.MoneyOff
        val Home: ImageVector = Icons.Outlined.Home
        val ShoppingCart: ImageVector = Icons.Outlined.ShoppingCart
        val Wallet: ImageVector = Icons.Outlined.Wallet
        val AccountCircle: ImageVector = Icons.Outlined.AccountCircle
        val List: ImageVector = Icons.Outlined.List
        val Newspaper: ImageVector = Icons.Outlined.Newspaper
        val FavoriteBorder: ImageVector = Icons.Outlined.FavoriteBorder
    }
}
