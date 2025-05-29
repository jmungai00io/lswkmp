package com.lswmobile.app.di

import android.content.Context
import com.lswmobile.app.network.AndroidTokenProvider

/**
 * Android-specific implementation of AppModuleFactory
 */
class AndroidAppModuleFactory(private val context: Context) : AppModuleFactory {
    
    override fun createAppModule(baseUrl: String, enableLogging: Boolean): AppModule {
        val tokenProvider = AndroidTokenProvider(context)
        return AppModule(tokenProvider, baseUrl, enableLogging)
    }
}
