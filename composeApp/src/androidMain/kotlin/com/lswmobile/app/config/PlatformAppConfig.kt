package com.lswmobile.app.config

import com.lswmobile.app.BuildConfig

/**
 * Android implementation of AppConfig
 */
class AndroidAppConfig : AppConfig {
    override val baseUrl: String
        get() = BuildConfig.BASE_URL
        
    override val webBaseUrl: String
        get() = BuildConfig.WEB_BASE_URL
        
    override val oneSignalAppId: String
        get() = BuildConfig.ONESIGNAL_APP_ID
        
    override val isDevelopment: Boolean
        get() = BuildConfig.ENVIRONMENT_NAME == "development"
        
    override val environmentName: String
        get() = BuildConfig.ENVIRONMENT_NAME
}

/**
 * Android implementation of PlatformAppConfigProvider
 */
actual class PlatformAppConfigProvider {
    actual fun getConfig(): AppConfig = AndroidAppConfig()
}
