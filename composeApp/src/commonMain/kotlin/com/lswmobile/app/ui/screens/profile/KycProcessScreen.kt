package com.lswmobile.app.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.UserResponse
import com.lswmobile.app.ui.theme.AppTheme

/**
 * KYC Process screen for user verification
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycProcessScreen(
    user: UserResponse? = null,
    isLoading: Boolean = false,
    error: String? = null,
    isKYCVerified: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onRefreshUser: () -> Unit = {}
) {
    // Setup scrolling behavior for the large title (iOS-style)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            KycProcessTopBar(
                scrollBehavior = scrollBehavior,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                Text(
                    text = "Loading KYC status...",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            } else if (error != null) {
                Text(
                    text = "Error: $error",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (isKYCVerified) {
                Text(
                    text = "✅ KYC Verification Complete",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Your account has been verified successfully.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = AppTheme.spacing.large.dp)
                )
            } else {
                Text(
                    text = "KYC Verification Required",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Please complete your KYC verification to access all features.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = AppTheme.spacing.large.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "This screen will contain the KYC form and document upload functionality.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = AppTheme.spacing.large.dp)
                )
            }
        }
    }
}

/**
 * Top app bar for the KYC Process screen with large title
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KycProcessTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigateBack: () -> Unit
) {
    LargeTopAppBar(
        title = { Text("KYC Verification") },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            // Add back button here if needed
        }
    )
} 