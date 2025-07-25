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
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AVFoundation.*
import platform.CoreGraphics.CGRect
import platform.Foundation.NSData
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CameraView(
    onPhotoTaken: (ByteArray) -> Unit,
    onError: (String) -> Unit
) {
    val device = AVCaptureDevice.devicesWithMediaType(AVMediaTypeVideo).firstOrNull { device ->
        (device as AVCaptureDevice).position == AVCaptureDevicePositionBack
    } as? AVCaptureDevice

    if (device == null) {
        // Show simulator camera mock when camera is not available
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
                    text = "Camera Preview",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Running in iOS Simulator",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Use a real device to test camera functionality",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.size(32.dp))
                
                // Mock capture button for simulator
                FloatingActionButton(
                    onClick = {
                        // Simulate photo capture with dummy data
                        val dummyImageData = ByteArray(1024) { 0x42.toByte() }
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

    val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null) as? AVCaptureDeviceInput

    if (input == null) {
        // Show error message if camera input cannot be created
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Cannot access camera",
                color = Color.White
            )
        }
        return
    }

    val output = AVCaptureStillImageOutput()
    output.outputSettings = mapOf(AVVideoCodecKey to AVVideoCodecJPEG)

    val session = AVCaptureSession()
    session.sessionPreset = AVCaptureSessionPresetPhoto
    session.addInput(input)
    session.addOutput(output)

    val cameraPreviewLayer = remember { AVCaptureVideoPreviewLayer(session = session) }

    // Function to capture photo
    fun capturePhoto() {
        val videoConnection = output.connectionWithMediaType(AVMediaTypeVideo)
        if (videoConnection != null) {
            output.captureStillImageAsynchronouslyFromConnection(
                videoConnection
            ) { sampleBuffer, error ->
                if (error != null) {
                    onError("Failed to capture photo: ${error.toString()}")
                    return@captureStillImageAsynchronouslyFromConnection
                }
                
                if (sampleBuffer != null) {
                    try {
                        // Convert CMSampleBuffer to JPEG data
                        val imageData = AVCaptureStillImageOutput.jpegStillImageNSDataRepresentation(sampleBuffer)
                        if (imageData != null) {
                            // Convert NSData to ByteArray
                            val length = imageData.length.toInt()
                            val byteArray = ByteArray(length)
                            
                            byteArray.usePinned { pinned ->
                                imageData.bytes?.let { bytes ->
                                    platform.posix.memcpy(pinned.addressOf(0), bytes, length.toULong())
                                }
                            }
                            
                            onPhotoTaken(byteArray)
                        } else {
                            onError("Failed to convert image to JPEG data")
                        }
                    } catch (e: Exception) {
                        onError("Error processing captured image: ${e.message}")
                    }
                } else {
                    onError("No image data received from camera")
                }
            }
        } else {
            onError("No video connection available")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            background = Color.Black,
            factory = {
                val container = UIView()
                container.layer.addSublayer(cameraPreviewLayer)
                cameraPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
                session.startRunning()
                container
            },
            onResize = { container: UIView, rect: CValue<CGRect> ->
                CATransaction.begin()
                CATransaction.setValue(true, kCATransactionDisableActions)
                container.layer.setFrame(rect)
                cameraPreviewLayer.setFrame(rect)
                CATransaction.commit()
            }
        )
        
        // Capture Button
        FloatingActionButton(
            onClick = {
                capturePhoto()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text("📷")
        }
    }
}