package com.lswmobile.app.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.ui.components.CameraView
import com.lswmobile.app.ui.components.loadImageFromBytes
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.theme.NetworkImage
import com.lswmobile.app.viewmodel.UploadAvatarViewModel
import kotlinx.datetime.Clock

/**
 * Screen for uploading profile picture using camera with ViewModel integration
 */
@Composable
fun UploadAvatarScreen(
    viewModel: UploadAvatarViewModel,
    onNavigateBack: () -> Unit = {},
    avatarUrl: String? = null
) {
    // Observe ViewModel state
    val isUploading = viewModel.isUploading
    val uploadError = viewModel.uploadError
    val uploadSuccess = viewModel.uploadSuccess
    
    // Clear state when screen is first composed
    LaunchedEffect(Unit) {
        viewModel.clearState()
    }
    
    UploadAvatarContent(
        isUploading = isUploading,
        uploadError = uploadError,
        uploadSuccess = uploadSuccess,
        onNavigateBack = onNavigateBack,
        avatarUrl = avatarUrl,
        onUploadAvatar = { fileBytes, fileName ->
            viewModel.uploadAvatar(fileBytes, fileName)
        },
        onClearError = {
            viewModel.clearError()
        },
        onAvatarUploaded = onNavigateBack
    )
}

/**
 * Screen for uploading profile picture using camera
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadAvatarContent(
    isUploading: Boolean = false,
    uploadError: String? = null,
    uploadSuccess: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onAvatarUploaded: () -> Unit = {},
    avatarUrl: String? = null,
    onUploadAvatar: (ByteArray, String) -> Unit = { _, _ -> },
    onClearError: () -> Unit = {}
) {
    var showCamera by remember { mutableStateOf(false) }
    var capturedPhoto by remember { mutableStateOf<ByteArray?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Picture") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = AppIcons.Filled.Back,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showCamera) {
                // Show camera view
                CameraView(
                    onPhotoTaken = { photoBytes ->
                        capturedPhoto = photoBytes
                        showCamera = false
                    },
                    onError = { error ->
                        onClearError()
                        // Handle camera error - could show in a snackbar or dialog
                        showCamera = false
                    }
                )
            } else {
                // Show main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (uploadSuccess) {
                        // Success state
                        SuccessContent(
                            onDone = onAvatarUploaded
                        )
                    } else if (capturedPhoto != null) {
                        // Photo preview state
                        PhotoPreviewContent(
                            photoBytes = capturedPhoto!!,
                            isUploading = isUploading,
                            onRetakePhoto = {
                                capturedPhoto = null
                                showCamera = true
                            },
                            onUploadPhoto = {
                                val fileName = "avatar_${Clock.System.now().toEpochMilliseconds()}.jpg"
                                onUploadAvatar(capturedPhoto!!, fileName)
                            }
                        )
                    } else {
                        // Initial state - ready to take photo
                        InitialContent(
                            onTakePhoto = { showCamera = true },
                            avatarUrl = avatarUrl
                        )
                    }
                }
            }

            // Error dialog
            uploadError?.let { error ->
                AlertDialog(
                    onDismissRequest = onClearError,
                    title = { Text("Upload Error") },
                    text = { Text(error) },
                    confirmButton = {
                        TextButton(onClick = onClearError) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun InitialContent(
    onTakePhoto: () -> Unit,
    avatarUrl: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Profile picture placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            if (!avatarUrl.isNullOrBlank()) {
                NetworkImage(
                    url = avatarUrl,
                    contentDescription = "Current avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = AppIcons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Upload Profile Picture",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Take a photo to set as your profile picture",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onTakePhoto,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Icon(
                imageVector = AppIcons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Take Photo")
        }
    }
}

@Composable
private fun PhotoPreviewContent(
    photoBytes: ByteArray,
    isUploading: Boolean,
    onRetakePhoto: () -> Unit,
    onUploadPhoto: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Preview Your Photo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Photo preview in circular frame
        Card(
            modifier = Modifier.size(200.dp),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            val imageBitmap = loadImageFromBytes(photoBytes)
            
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Profile Picture Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback if image can't be displayed
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = AppIcons.Filled.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Photo Ready",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // File size info
        val fileSize = photoBytes.size
        val fileSizeText = when {
            fileSize > 1_000_000 -> "${fileSize / 1_000_000}MB"
            fileSize > 1_000 -> "${fileSize / 1_000}KB"
            else -> "${fileSize} bytes"
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "Size: $fileSizeText",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            OutlinedButton(
                onClick = onRetakePhoto,
                modifier = Modifier.weight(1f),
                enabled = !isUploading
            ) {
                Icon(
                    imageVector = AppIcons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retake")
            }

            Button(
                onClick = onUploadPhoto,
                modifier = Modifier.weight(1f),
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Uploading...")
                } else {
                    Icon(
                        imageVector = AppIcons.Filled.ArrowUpward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload")
                }
            }
        }
    }
}

@Composable
private fun SuccessContent(
    onDone: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✅",
                style = MaterialTheme.typography.displayLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Profile Picture Updated!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your profile picture has been successfully updated",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Done")
        }
    }
}
