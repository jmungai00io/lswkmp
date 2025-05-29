package com.lswmobile.app.di

import com.lswmobile.app.network.TokenProvider

/**
 * Platform-specific factory interface for creating AppModule
 */
interface AppModuleFactory {
    /**
     * Create a new AppModule instance with the given configuration
     */
    fun createAppModule(
        baseUrl: String = AppModule.DEFAULT_BASE_URL,
        enableLogging: Boolean = true
    ): AppModule
}
