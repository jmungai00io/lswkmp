package com.lswmobile.app

import com.lswmobile.app.platform.CurrentActivityHolder
import android.app.Application

/**
 * Android Application class for the Livestock Wealth app
 */
class LivestockWealthApplication : Application() {
    companion object {
        lateinit var instance: LivestockWealthApplication
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        CurrentActivityHolder.init(this)
        
        // Initialize Koin for dependency injection
        LivestockWealthApp.initialize()
    }
}
