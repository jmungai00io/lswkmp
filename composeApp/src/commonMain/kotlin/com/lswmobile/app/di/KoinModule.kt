package com.lswmobile.app.di

import com.lswmobile.app.config.AppConfig
import com.lswmobile.app.config.AppConfigFactory
import com.lswmobile.app.network.KtorClient
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.SimpleTokenProvider
import com.lswmobile.app.network.TokenProvider
import com.lswmobile.app.network.repository.AuthRepository
import com.lswmobile.app.viewmodel.AuthViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module definitions for dependency injection
 */
object KoinModule {
    // Base URL is now dynamic from AppConfig
    
    /**
     * Network module providing API and HTTP client dependencies
     */
    val networkModule = module {
        // TokenProvider
        single<TokenProvider> { SimpleTokenProvider() }
        
        // AppConfig
        single<AppConfig> { AppConfigFactory.get() }
        
        // KtorClient
        single { 
            KtorClient(
                tokenProvider = get(),
                baseUrl = get<AppConfig>().baseUrl + "/api/v1", // Add the API path to the base URL
                enableLogging = get<AppConfig>().isDevelopment
            ) 
        }
        
        // API Service
        single { LivestockWealthApi(get()) }
    }
    
    /**
     * Repository module providing data repositories
     */
    val repositoryModule = module {
        single { AuthRepository(get(), get()) }
    }
    
    /**
     * ViewModel module providing ViewModels
     */
    val viewModelModule = module {
        factory { AuthViewModel(get()) }
    }
    
    /**
     * All application modules combined
     */
    val allModules = listOf(networkModule, repositoryModule, viewModelModule)
}
