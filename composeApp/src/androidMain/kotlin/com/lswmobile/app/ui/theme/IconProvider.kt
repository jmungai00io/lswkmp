package com.lswmobile.app.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Android implementation of IconProvider using Material icons
 */
internal actual object IconProvider {
    actual val filledIcons: AppIcons.FilledIcons = AndroidFilledIcons
    actual val outlinedIcons: AppIcons.OutlinedIcons = AndroidOutlinedIcons
    
    /**
     * Android implementation of Filled icons using Material icons
     */
    private object AndroidFilledIcons : AppIcons.FilledIcons {
        override val Home: ImageVector = Icons.Filled.Home
        override val ShoppingCart: ImageVector = Icons.Filled.ShoppingCart
        override val Wallet: ImageVector = Icons.Filled.Wallet
        override val AccountCircle: ImageVector = Icons.Filled.AccountCircle
        override val List: ImageVector = Icons.Filled.List
        override val Newspaper: ImageVector = Icons.Filled.Newspaper
        
        override val Add: ImageVector = Icons.Filled.Add
        override val ArrowForward: ImageVector = Icons.Filled.ArrowForward
        override val ArrowDownward: ImageVector = Icons.Filled.ArrowDownward
        override val ArrowUpward: ImageVector = Icons.Filled.ArrowUpward
        
        override val Description: ImageVector = Icons.Filled.Description
        override val Folder: ImageVector = Icons.Filled.Folder
        override val Receipt: ImageVector = Icons.Filled.Receipt
        
        override val Check: ImageVector = Icons.Filled.Check
        override val Clear: ImageVector = Icons.Filled.Clear
        override val Error: ImageVector = Icons.Filled.Error
        override val FilterList: ImageVector = Icons.Filled.FilterList
        override val Search: ImageVector = Icons.Filled.Search
        override val Schedule: ImageVector = Icons.Filled.Schedule
        override val LocationOn: ImageVector = Icons.Filled.LocationOn
        override val Alarm: ImageVector = Icons.Filled.Alarm
        override val CheckCircle: ImageVector = Icons.Filled.CheckCircle
        override val Back: ImageVector = Icons.Filled.ArrowBackIosNew
        override val Refresh: ImageVector = Icons.Filled.Refresh
        override val KeyboardArrowDown: ImageVector = Icons.Filled.KeyboardArrowDown
    }
    
    /**
     * Android implementation of Outlined icons using Material icons
     */
    private object AndroidOutlinedIcons : AppIcons.OutlinedIcons {
        override val Home: ImageVector = Icons.Outlined.Home
        override val ShoppingCart: ImageVector = Icons.Outlined.ShoppingCart
        override val Wallet: ImageVector = Icons.Outlined.Wallet
        override val AccountCircle: ImageVector = Icons.Outlined.AccountCircle
        override val List: ImageVector = Icons.Outlined.List
        override val Newspaper: ImageVector = Icons.Outlined.Newspaper
    }
}
