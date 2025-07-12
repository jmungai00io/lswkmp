package com.lswmobile.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.launch

@Composable
fun CameraPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    content: @Composable () -> Unit
) {
    val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
    val permissionsController: PermissionsController = remember(factory) { 
        factory.createPermissionsController() 
    }

    BindEffect(permissionsController)

    var hasPermission by remember { mutableStateOf(false) }
    var isPermissionChecked by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Check permission on first load
    LaunchedEffect(Unit) {
        try {
            hasPermission = permissionsController.isPermissionGranted(Permission.CAMERA)
        } catch (e: Exception) {
            hasPermission = false
        }
        isPermissionChecked = true
    }

    if (isPermissionChecked) {
        if (hasPermission) {
            // Permission granted, show content
            content()
        } else {
            // Permission not granted, show permission request
            CameraPermissionRequest(
                onRequestPermission = {
                    showPermissionDialog = true
                }
            )
        }
    }

            // Permission request dialog
        if (showPermissionDialog) {
            CameraPermissionDialog(
                onGrantPermission = {
                    showPermissionDialog = false
                    scope.launch {
                        try {
                            permissionsController.providePermission(Permission.CAMERA)
                            hasPermission = true
                            onPermissionGranted()
                        } catch (e: Exception) {
                            hasPermission = false
                            onPermissionDenied()
                        }
                    }
                },
                onDismiss = {
                    showPermissionDialog = false
                    onPermissionDenied()
                }
            )
        }
}

@Composable
private fun CameraPermissionRequest(
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📷 Camera Permission Required",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Camera access is required to take photos of your documents and selfie for KYC verification.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Grant Camera Permission")
            }
        }
    }
}

@Composable
private fun CameraPermissionDialog(
    onGrantPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Camera Permission Required",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = { 
            Text(
                text = "This app needs camera access to take photos of your KYC documents. Please grant camera permission to continue."
            ) 
        },
        confirmButton = {
            TextButton(onClick = onGrantPermission) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 