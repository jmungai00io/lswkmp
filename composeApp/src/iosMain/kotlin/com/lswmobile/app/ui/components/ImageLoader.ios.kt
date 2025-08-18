package com.lswmobile.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIImage
import org.jetbrains.skia.Image

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImageBitmapFromBytes(bytes: ByteArray?): ImageBitmap? {
    return remember(bytes) {
        bytes?.let { imageBytes ->
            try {
                // Convert ByteArray to NSData
                val nsData = imageBytes.usePinned { pinned ->
                    NSData.dataWithBytes(pinned.addressOf(0), imageBytes.size.toULong())
                }

                // Convert NSData to UIImage
                val uiImage = UIImage.imageWithData(nsData)

                if (uiImage != null) {
                    // Convert UIImage back to JPEG NSData for Skia
                    val jpegData = platform.UIKit.UIImageJPEGRepresentation(uiImage, 1.0)

                    if (jpegData != null) {
                        // Convert NSData to ByteArray for Skia
                        val length = jpegData.length.toInt()
                        val skiaBytes = ByteArray(length)

                        skiaBytes.usePinned { pinnedSkia ->
                            jpegData.bytes?.let { dataBytes ->
                                platform.posix.memcpy(pinnedSkia.addressOf(0), dataBytes, length.toULong())
                            }
                        }

                        // Use Skia to create ImageBitmap
                        val skiaImage = Image.makeFromEncoded(skiaBytes)
                        skiaImage.toComposeImageBitmap()
                    } else {
                        null
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}