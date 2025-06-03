package com.lswmobile.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lswmobile.app.ui.navigation.AppNavigation
import com.lswmobile.app.ui.theme.LivestockWealthTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Main app entry point
 */
@Composable
fun App() {
    // Initialize app dependencies here is not needed anymore
    // Initialization is now handled by AppInitializer in platform-specific code
    
    LivestockWealthTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Get dependencies from our initializer instead of creating them here
            // This prevents UI thread blocking and memory issues on iOS
            val authViewModel = AppInitializer.getAuthViewModel()
            
            // Use our navigation component
            AppNavigation(authViewModel = authViewModel)
        }
    }
}

/**
 * Preview of the app (for Android Studio)
 */
@Preview
@Composable
fun AppPreview() {
    App()
}