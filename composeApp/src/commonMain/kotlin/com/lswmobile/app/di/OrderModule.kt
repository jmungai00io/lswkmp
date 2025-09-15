package com.lswmobile.app.di

import com.lswmobile.app.data.repository.UserRepository
import com.lswmobile.app.network.repository.OrderRepository
import com.lswmobile.app.network.repository.WalletService
import com.lswmobile.app.ui.screens.orders.OrderViewModel
import com.lswmobile.app.ui.screens.payment.DebitPaymentViewModel
import com.lswmobile.app.ui.screens.payment.EftPaymentViewModel
import org.koin.dsl.module

/**
 * Koin module for order-related dependencies
 */
object OrderModule {
    val orderModule = module {
        // Create a single instance of OrderRepository
        single { OrderRepository(get()) }
        
        // Create a single instance of WalletService
        single { WalletService(get()) }
        
        // Create a single instance of OrderViewModel
        single { OrderViewModel(get(), get()) }
        
        // Create a single instance of EftPaymentViewModel
        single { EftPaymentViewModel(get(), get(), get()) }
        
        // Create a single instance of DebitPaymentViewModel
        single { DebitPaymentViewModel(get(), get(), get(), get()) }
    }
}
