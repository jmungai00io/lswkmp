package com.lswmobile.app

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Main entry point for iOS UI
 * Called from Swift to create the Compose UI
 */
fun MainViewController(): UIViewController {
    AppInitializer.initialize(isIOS = true)
    
    // Initialize Koin for dependency injection
    LivestockWealthApp.initialize()
    
    // Initialize any additional resources needed
    initializeResources()
    
    // Create the ComposeUIViewController with our App composable
    return ComposeUIViewController { 
        App()
    }
}

/**
 * Initialize any iOS-specific resources that might be needed
 */
private fun initializeResources() {

    try {
    } catch (e: Exception) {
    }
}