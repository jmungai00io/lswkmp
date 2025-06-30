package com.lswmobile.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Additional colors for the Livestock Wealth app
 * These are complementary to the main Colors.kt definitions
 */
object AppColors {
    // Primary colors
    val AppPrimary = Color(0xFF176B36)
    val OnAppPrimary = Color.White
    val AppPrimaryVariant = Color(0xFFDCEFE2)
    
    // Secondary colors
    val AppSecondary = Color(0xFF2E7D32)
    val OnAppSecondary = Color.White
    val AppSecondaryVariant = Color(0xFFDCEFDC)
    
    // Background and surface
    val AppBackground = Color(0xFFF8F9FA)
    val OnAppBackground = Color(0xFF121212)
    val AppSurface = Color.White
    val OnAppSurface = Color(0xFF121212)
    
    // Error states
    val AppError = Color(0xFFB00020)
    val OnAppError = Color.White
    
    // Dark theme variants
    val DarkAppPrimary = Color(0xFF4CAF50)
    val DarkOnAppPrimary = Color.Black
    val DarkAppPrimaryVariant = Color(0xFF1B5E20)
    
    val DarkAppSecondary = Color(0xFF81C784)
    val DarkOnAppSecondary = Color.Black
    val DarkAppSecondaryVariant = Color(0xFF2E7D32)
    
    val DarkAppBackground = Color(0xFF121212)
    val DarkOnAppBackground = Color.White
    val DarkAppSurface = Color(0xFF1E1E1E)
    val DarkOnAppSurface = Color.White
    
    val DarkAppError = Color(0xFFCF6679)
    val DarkOnAppError = Color.Black
}

/**
 * Status colors for various UI elements
 */
object StatusColors {
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFFC107) 
    val Info = Color(0xFF2196F3)
    val Pending = Color(0xFFFF9800)
    val StatusError = Color(0xFFB00020)
}
