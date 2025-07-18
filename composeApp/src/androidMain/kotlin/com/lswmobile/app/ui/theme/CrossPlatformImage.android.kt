package com.lswmobile.app.ui.theme

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.asImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of async image loading using Ktor
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
 * Load a network image and convert to ImageBitmap using Ktor and BitmapFactory
 */
private suspend fun loadNetworkImageBitmap(url: String): ImageBitmap {
    return withContext(Dispatchers.Default) {
        val httpClient = HttpClient()
        try {
            val response = httpClient.get(url)
            val imageData = response.body<ByteArray>()
            
            // Decode the image data using Android's BitmapFactory
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            
            // Convert to Compose ImageBitmap
            bitmap.asImageBitmap()
        } finally {
            httpClient.close()
        }
    }
}
