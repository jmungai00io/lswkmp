package com.lswmobile.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitViewController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class CameraDelegate(
    private val onPhotoTaken: (ByteArray) -> Unit,
    private val onError: (String) -> Unit,
    private val onDismiss: () -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
    
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        
        if (image != null) {
            // Convert UIImage to JPEG data
            val imageData = UIImageJPEGRepresentation(image, 0.8) // 80% quality
            if (imageData != null) {
                val length = imageData.length.toInt()
                val byteArray = ByteArray(length)
                
                byteArray.usePinned { pinned ->
                    imageData.bytes?.let { bytes ->
                        platform.posix.memcpy(pinned.addressOf(0), bytes, length.toULong())
                    }
                }
                
                onPhotoTaken(byteArray)
            } else {
                onError("Failed to convert image to JPEG")
            }
        } else {
            onError("No image selected")
        }
        
        picker.dismissViewControllerAnimated(true, null)
        onDismiss()
    }
    
    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, null)
        onDismiss()
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CameraView(
    onPhotoTaken: (ByteArray) -> Unit,
    onError: (String) -> Unit
) {
    var showImagePicker by remember { mutableStateOf(false) }
    
    // Check if camera is available
    val isCameraAvailable = remember {
        UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)
    }
    
    if (!isCameraAvailable) {
        // Show simulator mock or no camera available message
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
                        text = "📷",
                        fontSize = 40.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "Camera Not Available",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "This device doesn't have a camera",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "or you're running on a simulator",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.size(32.dp))
                
                // Mock capture button for simulator/no camera
                FloatingActionButton(
                    onClick = {
                        // Simulate photo capture with dummy data
                        val dummyImageData = ByteArray(2048) { (it % 256).toByte() }
                        onPhotoTaken(dummyImageData)
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Text("📷", fontSize = 24.sp)
                }
            }
        }
        return
    }

    // Real camera implementation using UIImagePickerController
    if (showImagePicker) {
        val delegate = remember {
            CameraDelegate(
                onPhotoTaken = onPhotoTaken,
                onError = onError,
                onDismiss = { showImagePicker = false }
            )
        }
        
        UIKitViewController(
            factory = {
                val picker = UIImagePickerController()
                picker.sourceType =
                    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                picker.cameraCaptureMode =
                    UIImagePickerControllerCameraCaptureMode.UIImagePickerControllerCameraCaptureModePhoto
                picker.cameraDevice =
                    UIImagePickerControllerCameraDevice.UIImagePickerControllerCameraDeviceRear
                picker.delegate = delegate
                picker
            },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // Show camera preview placeholder with capture button
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.DarkGray, CircleShape)
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📷",
                        fontSize = 60.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.size(24.dp))
                Text(
                    text = "Ready to Capture",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Tap the button below to take a photo",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.size(48.dp))
                
                // Launch camera button
                FloatingActionButton(
                    onClick = {
                        showImagePicker = true
                    },
                    modifier = Modifier.size(80.dp)
                ) {
                    Text("📷", fontSize = 32.sp)
                }
            }
        }
    }
}