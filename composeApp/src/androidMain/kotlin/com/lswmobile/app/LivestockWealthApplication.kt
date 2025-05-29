package com.lswmobile.app

import android.app.Application

/**
 * Android Application class for the Livestock Wealth app
 */
class LivestockWealthApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin for dependency injection
        LivestockWealthApp.initialize()
    }
}
