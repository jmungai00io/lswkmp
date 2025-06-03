package com.lswmobile.app

import com.lswmobile.app.config.AppConfigFactory
import com.lswmobile.app.network.KtorClient
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.SimpleTokenProvider
import com.lswmobile.app.network.repository.AuthRepository
import com.lswmobile.app.viewmodel.AuthViewModel

/**
 * Centralizes initialization of app components to ensure they're only created once
 * and properly configured for each platform
 */
object AppInitializer {
    // Singleton instances
    private var tokenProvider: SimpleTokenProvider? = null
    private var ktorClient: KtorClient? = null
    private var api: LivestockWealthApi? = null
    private var authRepository: AuthRepository? = null
    private var authViewModel: AuthViewModel? = null
    
    // Track initialization state
    private var isInitialized = false
    
    /**
     * Initialize the app components
     * @param isIOS Flag to indicate if running on iOS platform
     */
    fun initialize(isIOS: Boolean = false) {
        if (isInitialized) return
        
        try {
            println("Initializing app components")
            
            // Get platform-specific configuration
            val appConfig = AppConfigFactory.get()
            
            // Create dependencies
            tokenProvider = SimpleTokenProvider()
            
            // Configure client with platform-specific settings
            ktorClient = KtorClient(
                tokenProvider = tokenProvider!!,
                baseUrl = appConfig.baseUrl, // Use baseUrl from AppConfig
                enableLogging = appConfig.isDevelopment // Enable logging based on environment
            )
            
            println("Using API baseUrl: ${appConfig.baseUrl}, environment: ${appConfig.environmentName}")
            
            api = LivestockWealthApi(ktorClient!!)
            authRepository = AuthRepository(api!!, tokenProvider!!)
            authViewModel = AuthViewModel(authRepository!!)
            
            isInitialized = true
            println("App initialization complete")
        } catch (e: Exception) {
            println("Error during initialization: ${e.message}")
            throw e
        }
    }
    
    // Accessor methods
    fun getTokenProvider(): SimpleTokenProvider {
        ensureInitialized()
        return tokenProvider!!
    }
    
    fun getKtorClient(): KtorClient {
        ensureInitialized()
        return ktorClient!!
    }
    
    fun getApi(): LivestockWealthApi {
        ensureInitialized()
        return api!!
    }
    
    fun getAuthRepository(): AuthRepository {
        ensureInitialized()
        return authRepository!!
    }
    
    fun getAuthViewModel(): AuthViewModel {
        ensureInitialized()
        return authViewModel!!
    }
    
    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("AppInitializer has not been initialized. Call initialize() first.")
        }
    }
}
