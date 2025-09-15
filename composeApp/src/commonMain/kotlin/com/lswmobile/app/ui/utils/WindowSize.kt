package com.lswmobile.app.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Window size class to determine appropriate layouts
 * - Compact: Phone portrait (< 600dp)
 * - Medium: Large phones, small tablets (600dp - 840dp)
 * - Expanded: Tablets, desktops (> 840dp)
 */
enum class WindowSizeClass {
    COMPACT,
    MEDIUM,
    EXPANDED
}

/**
 * A set of window size breakpoints for responsive layouts
 */
object WindowSize {
    val COMPACT_WIDTH = 600.dp
    val MEDIUM_WIDTH = 840.dp
}

/**
 * Calculates window size class based on width
 *
 * @param width Width in dp
 * @return WindowSizeClass (COMPACT, MEDIUM, or EXPANDED)
 */
fun calculateWindowSizeClass(width: Dp): WindowSizeClass = when {
    width < WindowSize.COMPACT_WIDTH -> WindowSizeClass.COMPACT
    width < WindowSize.MEDIUM_WIDTH -> WindowSizeClass.MEDIUM
    else -> WindowSizeClass.EXPANDED
}

/**
 * Utility class to hold window size information and provide helper functions
 */
class WindowSizeInfo(
    val widthSizeClass: WindowSizeClass,
    val heightSizeClass: WindowSizeClass,
    val widthDp: Dp,
    val heightDp: Dp
) {
    val isCompactWidth = widthSizeClass == WindowSizeClass.COMPACT
    val isMediumWidth = widthSizeClass == WindowSizeClass.MEDIUM
    val isExpandedWidth = widthSizeClass == WindowSizeClass.EXPANDED
    
    // Helpers for deciding on layout configuration
    val shouldShowBottomBar: Boolean = isCompactWidth
    val shouldShowNavigationRail: Boolean = !isCompactWidth
    val shouldShowPermanentDrawer: Boolean = isExpandedWidth
    
    // iOS-specific helper for large title style
    val shouldUseLargeTitle: Boolean = true
}

/**
 * Remember the window size information based on current dimensions
 */
@Composable
fun rememberWindowSizeInfo(
    width: Dp,
    height: Dp
): WindowSizeInfo {
    return remember(width, height) {
        WindowSizeInfo(
            widthSizeClass = calculateWindowSizeClass(width),
            heightSizeClass = calculateWindowSizeClass(height),
            widthDp = width,
            heightDp = height
        )
    }
}
