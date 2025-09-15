package com.lswmobile.app.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.KycStatuses
import com.lswmobile.app.network.model.UserResponse
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.theme.NetworkImage
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
    onNavigateToAccountVerification: () -> Unit = {},
    onNavigateToUpdateProfile: () -> Unit = {},
    onNavigateToAddBeneficiary: () -> Unit = {},
    onNavigateToMyBeneficiaries: () -> Unit = {},
    onNavigateToUploadAvatar: () -> Unit = {},
    onRefreshUser: () -> Unit = {}
) {
    // Setup scrolling behavior for the large title (iOS-style)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    Scaffold(
        topBar = {
            ProfileTopBar(scrollBehavior = scrollBehavior)
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading profile...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = AppIcons.Filled.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error loading profile",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefreshUser) {
                        Text("Retry")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // User Profile Header
                UserProfileHeader(
                    user = user,
                    isKYCVerified = isKYCVerified,
                    onAvatarClick = onNavigateToUploadAvatar
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Account Management Section
                AccountManagementSection(
                    user = user,
                    onNavigateToAccountVerification = onNavigateToAccountVerification,
                    onNavigateToUpdateProfile = onNavigateToUpdateProfile
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Beneficiary Management Section
                BeneficiaryManagementSection(
                    onNavigateToAddBeneficiary = onNavigateToAddBeneficiary,
                    onNavigateToMyBeneficiaries = onNavigateToMyBeneficiaries
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Settings Section
                SettingsSection(
                    onNavigateToUploadAvatar = onNavigateToUploadAvatar
                )
            }
        }
    }
}

@Composable
private fun UserProfileHeader(
    user: UserResponse?,
    isKYCVerified: Boolean,
    onAvatarClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Avatar
            Card(
                modifier = Modifier.size(80.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (user?.avatarUrl?.isNotBlank() == true) {
                        NetworkImage(
                            url = user.avatarUrl,
                            contentDescription = "Profile avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .clickable { onAvatarClick() },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = AppIcons.Outlined.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // User Name
            Text(
                text = user?.let { "${it.firstName} ${it.lastName}" } ?: "User",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Email
            Text(
                text = user?.email ?: "user@example.com",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // KYC Status Badge
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isKYCVerified) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isKYCVerified) AppIcons.Filled.CheckCircle else AppIcons.Filled.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isKYCVerified) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isKYCVerified) "Verified" else "Verification Required",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isKYCVerified) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountManagementSection(
    user: UserResponse?,
    onNavigateToAccountVerification: () -> Unit,
    onNavigateToUpdateProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Account Management",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Account Verification
            ProfileMenuItem(
                icon = AppIcons.Filled.CheckCircle,
                title = "Account Verification",
                subtitle = if (user?.isKYCed == true) "Verified" else "Complete verification",
                onClick = onNavigateToAccountVerification,
                showBadge = user?.isKYCed != true
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Update Profile
            ProfileMenuItem(
                icon = AppIcons.Outlined.AccountCircle,
                title = "Update Profile",
                subtitle = "Edit personal information",
                onClick = onNavigateToUpdateProfile
            )
        }
    }
}

@Composable
private fun BeneficiaryManagementSection(
    onNavigateToAddBeneficiary: () -> Unit,
    onNavigateToMyBeneficiaries: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Beneficiary Management",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // My Beneficiaries
            ProfileMenuItem(
                icon = AppIcons.Filled.List,
                title = "My Beneficiaries",
                subtitle = "View and manage beneficiaries",
                onClick = onNavigateToMyBeneficiaries
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Add Beneficiary
            ProfileMenuItem(
                icon = AppIcons.Filled.Add,
                title = "Add Beneficiary",
                subtitle = "Add a new beneficiary",
                onClick = onNavigateToAddBeneficiary
            )
        }
    }
}

@Composable
private fun SettingsSection(
    onNavigateToUploadAvatar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Upload Avatar
            ProfileMenuItem(
                icon = AppIcons.Outlined.AccountCircle,
                title = "Profile Picture",
                subtitle = "Change your profile picture",
                onClick = onNavigateToUploadAvatar
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showBadge: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (showBadge) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.size(8.dp)
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Icon(
                imageVector = AppIcons.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
