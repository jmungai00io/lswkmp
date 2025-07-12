package com.lswmobile.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lswmobile.app.ui.components.CameraView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onPhotoTaken: (ByteArray) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var showCamera by remember { mutableStateOf(true) }
    var photoData by remember { mutableStateOf<ByteArray?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Camera") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("← Back")
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
                CameraView(
                    onPhotoTaken = { photoBytes ->
                        photoData = photoBytes
                        showCamera = false
                        onPhotoTaken(photoBytes)
                    },
                    onError = { error ->
                        errorMessage = error
                    }
                )
            } else {
                // Show photo preview or success message
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Photo Captured!",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Photo size: ${photoData?.size ?: 0} bytes",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { showCamera = true }
                    ) {
                        Text("Take Another Photo")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onNavigateBack
                    ) {
                        Text("Done")
                    }
                }
            }
            
            // Error message
            errorMessage?.let { error ->
                AlertDialog(
                    onDismissRequest = { errorMessage = null },
                    title = { Text("Camera Error") },
                    text = { Text(error) },
                    confirmButton = {
                        TextButton(onClick = { errorMessage = null }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
} 