package com.lswmobile.app.di

import com.lswmobile.app.network.IosTokenProvider

/**
 * iOS-specific implementation of AppModuleFactory
 */
class IosAppModuleFactory : AppModuleFactory {
    
    override fun createAppModule(baseUrl: String, enableLogging: Boolean): AppModule {
        val tokenProvider = IosTokenProvider()
        return AppModule(tokenProvider, baseUrl, enableLogging)
    }
}
