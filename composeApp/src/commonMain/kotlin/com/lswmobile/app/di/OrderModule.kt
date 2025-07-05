package com.lswmobile.app.di

import com.lswmobile.app.network.repository.OrderRepository
import com.lswmobile.app.ui.screens.orders.OrderViewModel
import org.koin.dsl.module

/**
 * Koin module for order-related dependencies
 */
object OrderModule {
    val orderModule = module {
        // Create a single instance of OrderRepository
        single { OrderRepository(get()) }
        
        // Create a single instance of OrderViewModel
        single { OrderViewModel(get()) }
    }
}
