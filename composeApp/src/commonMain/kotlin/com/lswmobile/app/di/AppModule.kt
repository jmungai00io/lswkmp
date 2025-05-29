package com.lswmobile.app.di

import com.lswmobile.app.network.KtorClient
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.TokenProvider
import com.lswmobile.app.network.repository.AuthRepository

/**
 * Simple dependency injection container for the application
 * In a real app, you might want to use a proper DI framework like Koin or Kodein
 */
class AppModule(
    private val tokenProvider: TokenProvider,
    private val baseUrl: String,
    private val enableLogging: Boolean = true
) {
    // API Client
    private val ktorClient by lazy {
        KtorClient(tokenProvider, baseUrl, enableLogging)
    }
    
    // API Service
    val api by lazy {
        LivestockWealthApi(ktorClient)
    }
    
    // Repositories
    val authRepository by lazy {
        AuthRepository(api, tokenProvider)
    }
    
    companion object {
        // Default base URL, should be configurable in production
        const val DEFAULT_BASE_URL = "https://api.livestockwealth.com"
    }
}
