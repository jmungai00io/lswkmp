package com.lswmobile.app

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Main entry point for iOS UI
 * Called from Swift to create the Compose UI
 */
fun MainViewController(): UIViewController {
    // Initialize any iOS-specific code here before creating the ComposeUIViewController
    println("Initializing iOS ViewController")
    
    // Initialize the AppInitializer - this is critical!
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
    // Log to help with debugging
    println("Setting up iOS resources")
    
    try {
        // Any additional iOS-specific initialization can go here
        println("iOS resources initialized successfully")
    } catch (e: Exception) {
        println("Error during iOS initialization: ${e.message}")
    }
}