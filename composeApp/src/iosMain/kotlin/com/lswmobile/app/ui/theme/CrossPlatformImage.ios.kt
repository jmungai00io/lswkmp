package com.lswmobile.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIImage
import org.jetbrains.skia.Image
import platform.Foundation.create

/**
 * iOS implementation of async image loading using Ktor and UIKit
 */
@Composable
actual fun rememberAsyncImagePainter(url: String): ImageResource {
    var imageResource by remember { mutableStateOf<ImageResource>(ImageResource.Loading) }

    LaunchedEffect(url) {
        try {
            val bitmap = loadNetworkImageBitmap(url)
            val painter = BitmapPainter(bitmap)
            imageResource = ImageResource.Success(painter)
        } catch (e: Exception) {
            imageResource = ImageResource.Error(e)
        }
    }

    return imageResource
}

/**
 * Load a network image and convert to ImageBitmap using Ktor and UIKit
 */
@OptIn(ExperimentalForeignApi::class)
private suspend fun loadNetworkImageBitmap(url: String): ImageBitmap {
    return withContext(Dispatchers.Default) {
        val httpClient = HttpClient()
        try {
            val response = httpClient.get(url)
            val imageData = response.body<ByteArray>()
            
            // Convert ByteArray to NSData using pinned memory
            val nsData = imageData.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = imageData.size.toULong())
            }
            
            // Create UIImage from NSData
            val uiImage = UIImage.imageWithData(nsData)
                ?: throw IllegalArgumentException("Failed to decode image data")
            
            // Convert to Skia Image and then to Compose ImageBitmap
            val skiaImage = Image.makeFromEncoded(imageData)
            skiaImage.toComposeImageBitmap()
        } finally {
            httpClient.close()
        }
    }
}