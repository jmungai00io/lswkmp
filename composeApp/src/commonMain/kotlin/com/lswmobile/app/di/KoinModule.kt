package com.lswmobile.app.di

import com.lswmobile.app.AppInitializer
import com.lswmobile.app.config.AppConfig
import com.lswmobile.app.config.AppConfigFactory
import com.lswmobile.app.data.repository.CartRepository
import com.lswmobile.app.data.repository.InMemoryUserRepository
import com.lswmobile.app.data.repository.KycRepository
import com.lswmobile.app.data.repository.LocalCartRepository
import com.lswmobile.app.data.repository.UserRepository
import com.lswmobile.app.network.KtorClient
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.SimpleTokenProvider
import com.lswmobile.app.network.TokenProvider
import com.lswmobile.app.network.repository.AuthRepository
import com.lswmobile.app.network.repository.MarketplaceRepository
import com.lswmobile.app.network.repository.NewsFeedRepository
import com.lswmobile.app.network.repository.UserRepository as NetworkUserRepository
import com.lswmobile.app.network.repository.WithdrawalRepository
import com.lswmobile.app.ui.screens.marketplace.MarketplaceViewModel
import com.lswmobile.app.viewmodel.AuthViewModel
import com.lswmobile.app.viewmodel.BeneficiaryViewModel
import com.lswmobile.app.viewmodel.KycViewModel
import com.lswmobile.app.viewmodel.NewsFeedViewModel
import com.lswmobile.app.viewmodel.UpdateProfileViewModel
import com.lswmobile.app.viewmodel.UserViewModel
import com.lswmobile.app.viewmodel.WithdrawalViewModel
import com.lswmobile.app.di.OrderModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
        // TokenProvider - Use the same instance from AppInitializer
        single<TokenProvider> { AppInitializer.getTokenProvider() }
        
        // AppConfig
        single<AppConfig> { AppConfigFactory.get() }
        
        // KtorClient - Use factory to always get current instance
        factory { 
            KtorClient(
                tokenProvider = get(),
                baseUrl = get<AppConfig>().baseUrl + "/api/v1", // Add the API path to the base URL
                enableLogging = get<AppConfig>().isDevelopment
            ) 
        }
        
        // API Service - Use factory to always get current instance
        factory { LivestockWealthApi(get()) }
        
        // CoroutineScope for ViewModels
        factory { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    }
    
    /**
     * Database module providing local storage dependencies
     */
    val databaseModule = module {
        // Use LocalCartRepository with in-memory storage (no persistence for now)
        single<CartRepository> { LocalCartRepository(currentUserId = "default_user") }
        
        // User repository
        single<UserRepository> { InMemoryUserRepository(get()) }
    }
    
    /**
     * Repository module providing data repositories
     */
    val repositoryModule = module {
        single { AuthRepository(get(), get()) }
        single { MarketplaceRepository(get()) }
        single { NewsFeedRepository(get()) }
        single { KycRepository(get()) }
        single { NetworkUserRepository(get()) }
        single { WithdrawalRepository(get()) }
    }
    
    /**
     * ViewModel module providing ViewModels
     */
    val viewModelModule = module {
        factory { AuthViewModel(get(), get()) }
        factory { MarketplaceViewModel(get(), get()) }
        factory { NewsFeedViewModel(get(), get()) }
        factory { UserViewModel(get()) }
        factory { KycViewModel(get(), get()) }
        factory { BeneficiaryViewModel(get(), get()) }
        factory { UpdateProfileViewModel(get<NetworkUserRepository>(), get()) }
        factory { WithdrawalViewModel(get(), get()) }
    }
    
    /**
     * All application modules combined
     */
    val allModules = listOf(
        networkModule, 
        databaseModule, 
        repositoryModule, 
        viewModelModule,
        OrderModule.orderModule // Include the order module from OrderModule.kt
    )
}
