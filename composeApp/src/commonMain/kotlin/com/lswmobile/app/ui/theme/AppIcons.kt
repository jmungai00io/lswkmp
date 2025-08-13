package com.lswmobile.app.ui.theme

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Cross-platform icon utilities for the Livestock Wealth app
 * 
 * This provides a consistent API for accessing Material icons across Android and iOS
 * Android uses the actual Material icons, while iOS uses a custom implementation
 */
object AppIcons {
    // Common icon interfaces
    interface FilledIcons {
        // Navigation
        val Home: ImageVector
        val ShoppingCart: ImageVector
        val Wallet: ImageVector
        val AccountCircle: ImageVector
        val List: ImageVector
        val Newspaper: ImageVector
        
        // Actions
        val Add: ImageVector
        val ArrowForward: ImageVector
        val ArrowDownward: ImageVector
        val ArrowUpward: ImageVector
        
        // Content
        val Description: ImageVector
        val Folder: ImageVector
        val Receipt: ImageVector
        
        // Status
        val Check: ImageVector
        val Clear: ImageVector
        val FilterList: ImageVector
        val Search: ImageVector
        val Schedule: ImageVector

        val LocationOn: ImageVector
        val Alarm: ImageVector
        val CheckCircle: ImageVector
        val Error: ImageVector

        val Refresh: ImageVector
        val Back: ImageVector

        val KeyboardArrowDown: ImageVector
        val KeyboardArrowUp: ImageVector
        val Favorite: ImageVector
        val Close: ImageVector
        val ChevronLeft: ImageVector
        val ChevronRight: ImageVector
    }
    
    interface OutlinedIcons {
        val Portfolio: ImageVector
        val Home: ImageVector
        val ShoppingCart: ImageVector
        val Wallet: ImageVector
        val AccountCircle: ImageVector
        val List: ImageVector
        val Newspaper: ImageVector
        val FavoriteBorder: ImageVector
    }

    // Provides the default implementation based on platform
    val Filled: FilledIcons = IconProvider.filledIcons
    val Outlined: OutlinedIcons = IconProvider.outlinedIcons
}

/**
 * Expect/actual pattern for platform-specific icon implementation
 */
internal expect object IconProvider {
    val filledIcons: AppIcons.FilledIcons
    val outlinedIcons: AppIcons.OutlinedIcons
}
