package com.lswmobile.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Brand colors for Livestock Wealth app
 */
object BrandColors {
    val Black = Color(0xFF000000)
    val White = Color(0xFFFFFFFF)
    val PrimaryBrown = Color(0xFF8A5D26)
    val PrimaryBrownDark = Color(0xFF7A5221)
    val PrimaryBrownLight = Color(0xFF856E51)
    val SecondaryLightGrey = Color(0xFFEEEEEE)
    val SecondaryMediumGrey = Color(0xFFCCCCCC)
    val SecondaryDarkerGrey = Color(0xFF6C6C6C)
    val LightBrown = Color(0xFFF1D5B9)
    val Smoke = Color(0xFFF5F5F5)
    val Red = Color(0xFFD32F2F) // red[700]
    val Green = Color(0xFF4CAF50) // green[500]
    val Orange = Color(0xFFFF9800) // orange[500]
    val Blue = Color(0xFF2196F3) // blue[500]
    val FarmersClubGreen = Color(0xFF35472F)
    val GreySmoke = Color(0xFFF5F5F5)
}

/**
 * Semantic colors for UI elements in light mode
 */
object LightColors {
    val Primary = BrandColors.PrimaryBrown
    val PrimaryVariant = BrandColors.PrimaryBrownDark
    val Secondary = BrandColors.FarmersClubGreen
    val SecondaryVariant = BrandColors.SecondaryDarkerGrey
    val Background = BrandColors.White
    val Surface = BrandColors.White
    val Error = BrandColors.Red
    val OnPrimary = BrandColors.White
    val OnSecondary = BrandColors.White
    val OnBackground = BrandColors.Black
    val OnSurface = BrandColors.Black
    val OnError = BrandColors.White
}

/**
 * Semantic colors for UI elements in dark mode
 */
object DarkColors {
    val Primary = BrandColors.PrimaryBrown
    val PrimaryVariant = BrandColors.PrimaryBrownLight
    val Secondary = BrandColors.FarmersClubGreen
    val SecondaryVariant = BrandColors.SecondaryMediumGrey
    val Background = Color(0xFF121212)
    val Surface = Color(0xFF1E1E1E)
    val Error = BrandColors.Red
    val OnPrimary = BrandColors.White
    val OnSecondary = BrandColors.White
    val OnBackground = BrandColors.White
    val OnSurface = BrandColors.White
    val OnError = BrandColors.Black
}
