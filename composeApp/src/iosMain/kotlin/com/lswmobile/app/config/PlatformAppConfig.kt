package com.lswmobile.app.config

/**
 * iOS implementation of AppConfig
 */
class IosAppConfig : AppConfig {
    // Default to development values for iOS
    private val currentEnv = "development" // This would ideally come from build settings
    
    override val baseUrl: String
        get() = when (currentEnv) {
            "development" -> "https://staging.api.livestockwealth.com"
            "staging" -> "https://staging.api.livestockwealth.com"
            "production" -> "https://api.livestockwealth.com"
            else -> "https://api.livestockwealth.com"
        }
        
    override val webBaseUrl: String
        get() = when (currentEnv) {
            "development", "staging" -> "https://staging.livestockwealth.com"
            "production" -> "https://livestockwealth.com"
            else -> "https://livestockwealth.com"
        }
        
    override val oneSignalAppId: String
        get() = when (currentEnv) {
            "development" -> "ios-development-app-id"
            "staging" -> "ios-staging-app-id"
            "production" -> "ios-production-app-id"
            else -> "ios-development-app-id"
        }
        
    override val isDevelopment: Boolean
        get() = currentEnv == "development"
        
    override val environmentName: String
        get() = currentEnv
}

/**
 * iOS implementation of PlatformAppConfigProvider
 */
actual class PlatformAppConfigProvider {
    actual fun getConfig(): AppConfig = IosAppConfig()
}
