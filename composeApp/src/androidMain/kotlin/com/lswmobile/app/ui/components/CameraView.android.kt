package com.lswmobile.app.ui.components

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.lswmobile.app.ui.theme.AppColors
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun CameraView(
    onPhotoTaken: (ByteArray) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Camera permission state
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    when {
        cameraPermissionState.status.isGranted -> {
            // Permission granted, show camera
            CameraContent(
                onPhotoTaken = onPhotoTaken,
                onError = onError
            )
        }
        cameraPermissionState.status.shouldShowRationale -> {
            // Permission denied but can show rationale
            PermissionRationaleContent(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
            )
        }
        else -> {
            // Permission not requested yet or permanently denied
            PermissionRequestContent(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
            )
        }
    }
}

@Composable
private fun PermissionRequestContent(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.DarkGray, CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔒",
                    fontSize = 40.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = "Camera Permission Required",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "This app needs camera access to take photos",
                color = Color.Gray,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.size(32.dp))
            
            Button(
                onClick = onRequestPermission
            ) {
                Text("Grant Camera Permission")
            }
        }
    }
}

@Composable
private fun PermissionRationaleContent(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.DarkGray, CircleShape)
                    .border(2.dp, AppColors.DarkAppPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 40.sp,
                    color = AppColors.DarkAppPrimaryVariant
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = "Camera Access Needed",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Camera permission is required to take photos",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Please grant permission to continue",
                color = Color.Gray,
                fontSize = 12.sp
            )
            
            Spacer(modifier = Modifier.size(32.dp))
            
            Button(
                onClick = onRequestPermission
            ) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
private fun CameraContent(
    onPhotoTaken: (ByteArray) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    
    LaunchedEffect(cameraProviderFuture) {
        cameraProvider = cameraProviderFuture.get()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                cameraProvider?.let { provider ->
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(view.surfaceProvider)
                    
                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    
                    try {
                        provider.unbindAll()
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        onError("Failed to bind camera: ${e.message}")
                    }
                }
            }
        )
        
        // Capture Button
        FloatingActionButton(
            onClick = {
                val executor = ContextCompat.getMainExecutor(context)
                takePhoto(
                    imageCapture = imageCapture,
                    executor = executor,
                    onPhotoTaken = onPhotoTaken,
                    onError = onError
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text("📷")
        }
    }
}

private fun takePhoto(
    imageCapture: ImageCapture,
    executor: Executor,
    onPhotoTaken: (ByteArray) -> Unit,
    onError: (String) -> Unit
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                try {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    
                    // Convert to JPEG if needed
                    val outputStream = ByteArrayOutputStream()
                    // For simplicity, we'll use the raw bytes
                    // In a real app, you might want to convert to JPEG
                    onPhotoTaken(bytes)
                } catch (e: Exception) {
                    onError("Failed to process image: ${e.message}")
                } finally {
                    image.close()
                }
            }
            
            override fun onError(exception: ImageCaptureException) {
                onError("Failed to capture photo: ${exception.message}")
            }
        }
    )
}