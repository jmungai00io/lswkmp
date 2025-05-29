package com.lswmobile.app.config

/**
 * Common interface for app configuration across platforms
 */
interface AppConfig {
    /**
     * The base URL for API requests
     */
    val baseUrl: String
    
    /**
     * The base web URL for web links
     */
    val webBaseUrl: String
    
    /**
     * OneSignal App ID for push notifications
     */
    val oneSignalAppId: String
    
    /**
     * Is this a development build
     */
    val isDevelopment: Boolean
    
    /**
     * Environment name (development, staging, production)
     */
    val environmentName: String
}

/**
 * Entry point to get the platform-specific configuration
 */
expect class PlatformAppConfigProvider() {
    fun getConfig(): AppConfig
}

/**
 * Factory for accessing the platform-specific config
 */
object AppConfigFactory {
    private val provider by lazy { PlatformAppConfigProvider() }
    
    /**
     * Get the platform-specific AppConfig implementation
     */
    fun get(): AppConfig = provider.getConfig()
}
