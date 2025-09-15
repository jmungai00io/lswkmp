package com.lswmobile.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * App-specific spacing scale
 */
class AppSpacing(
    val extraSmall: Int = 4,
    val small: Int = 8,
    val medium: Int = 16,
    val large: Int = 24,
    val extraLarge: Int = 32,
    val xxLarge: Int = 48
)

/**
 * Composition local to provide spacing values
 */
val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }

/**
 * Rounded corner size for the app, consistent with iOS-first design
 */
val DefaultCornerRadius = 12.dp

// Note: LivestockWealthShapes is defined in Shapes.kt

/**
 * Light color palette for Livestock Wealth app
 */
private val LivestockWealthLightColors = lightColorScheme(
    primary = Color(0xFF8A5D26), // PrimaryBrown
    onPrimary = Color(0xFFFFFFFF), // White
    primaryContainer = Color(0xFFF1D5B9), // LightBrown
    onPrimaryContainer = Color(0xFF000000), // Black
    secondary = Color(0xFF35472F), // FarmersClubGreen
    onSecondary = Color(0xFFFFFFFF), // White
    secondaryContainer = Color(0xFFEEEEEE), // SecondaryLightGrey
    onSecondaryContainer = Color(0xFF000000), // Black
    background = Color(0xFFFFFFFF), // White
    onBackground = Color(0xFF000000), // Black
    surface = Color(0xFFFFFFFF), // White
    onSurface = Color(0xFF000000), // Black
    error = Color(0xFFD32F2F), // Red
    onError = Color(0xFFFFFFFF), // White
    // iOS-friendly surfaces - using surface properties with slightly different tints
    surfaceVariant = Color(0xFFFFFFFF).copy(alpha = 0.95f), // White with alpha
    inverseSurface = Color(0xFF000000) // Black
)

/**
 * Dark color palette for Livestock Wealth app
 */
private val LivestockWealthDarkColors = darkColorScheme(
    primary = Color(0xFF8A5D26), // PrimaryBrown
    onPrimary = Color(0xFFFFFFFF), // White
    primaryContainer = Color(0xFF7A5221), // PrimaryBrownDark
    onPrimaryContainer = Color(0xFFFFFFFF), // White
    secondary = Color(0xFF35472F), // FarmersClubGreen
    onSecondary = Color(0xFFFFFFFF), // White
    secondaryContainer = Color(0xFF6C6C6C), // SecondaryDarkerGrey
    onSecondaryContainer = Color(0xFFFFFFFF), // White
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF), // White
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFFFFFFF), // White
    error = Color(0xFFD32F2F), // Red
    onError = Color(0xFFFFFFFF), // White
    // iOS-friendly surfaces - using surface properties with slightly different tints
    surfaceVariant = Color(0xFF1E1E1E).copy(alpha = 0.85f),
    inverseSurface = Color(0xFFFFFFFF) // White
)

/**
 * LivestockWealth Theme
 *
 * Material theme for the Livestock Wealth app that adapts to light/dark mode.
 * Applies consistent colors, typography, and shapes across the app UI.
 * Enhanced with iOS-native feel and consistent spacing.
 *
 * @param darkTheme Whether to use dark theme colors (defaults to system setting)
 * @param content The composable content to apply the theme to
 */
@Composable
fun LivestockWealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LivestockWealthDarkColors else LivestockWealthLightColors
    val spacing = remember { AppSpacing() }
    
    CompositionLocalProvider(
        LocalAppSpacing provides spacing
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = LivestockWealthShapes,
            typography = LivestockWealthTypography,
            content = {
                Surface(content = content)
            }
        )
    }
}

/**
 * Access the current spacing values from composition
 */
object AppTheme {
    val spacing: AppSpacing
        @Composable
        get() = LocalAppSpacing.current
}
