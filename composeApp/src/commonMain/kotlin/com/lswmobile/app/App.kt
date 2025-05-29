package com.lswmobile.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.lswmobile.app.network.KtorClient
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.SimpleTokenProvider
import com.lswmobile.app.network.repository.AuthRepository
import com.lswmobile.app.ui.navigation.AppNavigation
import com.lswmobile.app.ui.theme.LivestockWealthTheme
import com.lswmobile.app.viewmodel.AuthViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

/**
 * Main app entry point
 */
@Composable
fun App() {
    // Initialize app dependencies if needed
    // This would be better in a platform-specific initialization point
    // LivestockWealthApp.initialize()
    
    LivestockWealthTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Manual dependency injection (for now)
            val tokenProvider = remember { SimpleTokenProvider() }
            val ktorClient = remember { 
                KtorClient(
                    tokenProvider = tokenProvider,
                    baseUrl = "https://api.livestockwealth.com/api/v1",
                    enableLogging = true
                ) 
            }
            val api = remember { LivestockWealthApi(ktorClient) }
            val authRepository = remember { AuthRepository(api, tokenProvider) }
            val authViewModel = remember { AuthViewModel(authRepository) }
            
            // Use our navigation component
            AppNavigation(authViewModel = authViewModel)
        }
    }
}

/**
 * Alternative version using Koin for dependency injection
 * To use this, you need to initialize Koin first with LivestockWealthApp.initialize()
 */
@Composable
fun AppWithKoin() {
    LivestockWealthTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Use Koin for dependency injection
            val authViewModel: AuthViewModel = koinInject()
            
            // Use our navigation component
            AppNavigation(authViewModel = authViewModel)
        }
    }
}

@Composable
@Preview
fun AppPreview() {
    App()
}