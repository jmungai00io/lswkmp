package com.lswmobile.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Light color palette for Livestock Wealth app
 */
private val LivestockWealthLightColors = lightColorScheme(
    primary = LightColors.Primary,
    onPrimary = LightColors.OnPrimary,
    primaryContainer = LightColors.PrimaryVariant,
    onPrimaryContainer = LightColors.OnPrimary,
    secondary = LightColors.Secondary,
    onSecondary = LightColors.OnSecondary,
    secondaryContainer = LightColors.SecondaryVariant,
    onSecondaryContainer = LightColors.OnSecondary,
    background = LightColors.Background,
    onBackground = LightColors.OnBackground,
    surface = LightColors.Surface,
    onSurface = LightColors.OnSurface,
    error = LightColors.Error,
    onError = LightColors.OnError
)

/**
 * Dark color palette for Livestock Wealth app
 */
private val LivestockWealthDarkColors = darkColorScheme(
    primary = DarkColors.Primary,
    onPrimary = DarkColors.OnPrimary,
    primaryContainer = DarkColors.PrimaryVariant,
    onPrimaryContainer = DarkColors.OnPrimary,
    secondary = DarkColors.Secondary,
    onSecondary = DarkColors.OnSecondary,
    secondaryContainer = DarkColors.SecondaryVariant,
    onSecondaryContainer = DarkColors.OnSecondary,
    background = DarkColors.Background,
    onBackground = DarkColors.OnBackground,
    surface = DarkColors.Surface,
    onSurface = DarkColors.OnSurface,
    error = DarkColors.Error,
    onError = DarkColors.OnError
)

/**
 * LivestockWealth Theme
 *
 * Material theme for the Livestock Wealth app that adapts to light/dark mode.
 * Applies consistent colors, typography, and shapes across the app UI.
 *
 * @param darkTheme Whether to use dark theme colors (defaults to system setting)
 * @param content The composable content to apply the theme to
 */
@Composable
fun LivestockWealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        LivestockWealthDarkColors
    } else {
        LivestockWealthLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LivestockWealthTypography,
        content = content
    )
}
