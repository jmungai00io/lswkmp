package com.lswmobile.app.config

import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo

/**
 * iOS implementation of AppConfig
 */
class IosAppConfig : AppConfig {
    private val currentEnv: String = determineEnvironment()
    
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

    private fun determineEnvironment(): String {
        val processEnv = (NSProcessInfo.processInfo.environment["APP_ENV"] as? String)?.ifBlank { null }
        val bundleEnv = (NSBundle.mainBundle?.objectForInfoDictionaryKey("AppEnvironment") as? String)?.ifBlank { null }
        return normalizeEnvironment(processEnv ?: bundleEnv)
    }

    private fun normalizeEnvironment(value: String?): String {
        return when (value?.trim()?.lowercase()) {
            "production", "prod", "release" -> "production"
            "staging", "stage", "preprod" -> "staging"
            "development", "dev", "debug" -> "development"
            else -> "development"
        }
    }
}

/**
 * iOS implementation of PlatformAppConfigProvider
 */
actual class PlatformAppConfigProvider {
    actual fun getConfig(): AppConfig = IosAppConfig()
}
