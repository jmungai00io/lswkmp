package com.lswmobile.app

import com.lswmobile.app.config.AppConfigFactory
import com.lswmobile.app.data.repository.InMemoryUserRepository
import com.lswmobile.app.data.repository.UserRepository
import com.lswmobile.app.network.KtorClient
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.SimpleTokenProvider
import com.lswmobile.app.network.repository.AuthRepository
import com.lswmobile.app.viewmodel.AuthViewModel
import com.lswmobile.app.viewmodel.UserViewModel

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
    private var userRepository: UserRepository? = null
    private var userViewModel: UserViewModel? = null
    
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
            userRepository = InMemoryUserRepository(api!!)
            userViewModel = UserViewModel(userRepository!!)
            
            isInitialized = true
            println("App initialization complete")
        } catch (e: Exception) {
            println("Error during initialization: ${e.message}")
            throw e
        }
    }
    
    /**
     * Recreates the KtorClient and API instances to pick up token changes
     * Call this after authentication completes to ensure tokens are used in requests
     */
    fun reinitializeNetworkClients() {
        ensureInitialized()
        
        try {
            println("Reinitializing network clients to pick up authentication changes")
            
            // Get platform-specific configuration
            val appConfig = AppConfigFactory.get()
            
            // Recreate the KtorClient with the current tokenProvider
            ktorClient = KtorClient(
                tokenProvider = tokenProvider!!,
                baseUrl = appConfig.baseUrl,
                enableLogging = appConfig.isDevelopment
            )
            
            // Recreate API with new client
            api = LivestockWealthApi(ktorClient!!)
            
            // Update auth repository with new API
            authRepository = AuthRepository(api!!, tokenProvider!!)
            authViewModel = AuthViewModel(authRepository!!)
            userRepository = InMemoryUserRepository(api!!)
            userViewModel = UserViewModel(userRepository!!)
            
            println("Network clients reinitialized successfully")
        } catch (e: Exception) {
            println("Error during network client reinitialization: ${e.message}")
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
    
    fun getUserRepository(): UserRepository {
        ensureInitialized()
        return userRepository!!
    }
    
    fun getUserViewModel(): UserViewModel {
        ensureInitialized()
        return userViewModel!!
    }
    
    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("AppInitializer has not been initialized. Call initialize() first.")
        }
    }
}
