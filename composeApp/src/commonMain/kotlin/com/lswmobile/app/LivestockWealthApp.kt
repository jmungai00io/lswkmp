package com.lswmobile.app

import com.lswmobile.app.di.KoinModule
import org.koin.core.context.startKoin

/**
 * Application initialization
 * This is called at app startup to initialize dependencies
 */
object LivestockWealthApp {
    /**
     * Initialize the application
     */
    fun initialize() {
        // Initialize AppInitializer first to ensure token provider is available
        AppInitializer.initialize()
        
        // Then initialize Koin
        initializeKoin()
    }
    
    /**
     * Initialize Koin for dependency injection
     */
    private fun initializeKoin() {
        startKoin {
            modules(KoinModule.allModules)
        }
    }
}
