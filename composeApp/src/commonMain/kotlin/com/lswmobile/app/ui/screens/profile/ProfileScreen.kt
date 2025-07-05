package com.lswmobile.app.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.lswmobile.app.network.model.KycStatuses
import com.lswmobile.app.network.model.UserResponse
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.viewmodel.UserViewModel

/**
 * Profile screen showing user information and account settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    repository: UserViewModel? = null,
    user: UserResponse? = null,
    isLoading: Boolean = false,
    error: String? = null,
    isKYCVerified: Boolean = false,
    onNavigateToKycProcess: () -> Unit = {},
    onNavigateToUpdateUser: () -> Unit = {},
    onNavigateToUploadAvatar: () -> Unit = {},
    onRefreshUser: () -> Unit = {}
) {
    // Setup scrolling behavior for the large title (iOS-style)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ProfileTopBar(scrollBehavior = scrollBehavior)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = AppIcons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier.padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            if (isLoading) {
                Text(
                    text = "Loading user data...",
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
            } else if (user != null) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (isKYCVerified) "✅ KYC Verified" else "❌ KYC Not Verified",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = if (isKYCVerified) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                
                // Show KYC button if not VERIFIED
                val kycStatus = user.kycVerification?.status
                if (kycStatus != KycStatuses.VERIFIED) {
                    Spacer(modifier = Modifier.height(24.dp))
                    androidx.compose.material3.Button(
                        onClick = onNavigateToKycProcess,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .fillMaxSize(0.7f)
                    ) {
                        Text("Verify Identity (KYC)")
                    }
                }
            } else {
                Text(
                    text = "Profile Screen",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "This screen will show user profile information and settings",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = AppTheme.spacing.large.dp)
                )
            }
        }
    }
}

/**
 * Top app bar for the Profile screen with large title
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    LargeTopAppBar(
        title = { Text("Profile") },
        scrollBehavior = scrollBehavior
    )
}
