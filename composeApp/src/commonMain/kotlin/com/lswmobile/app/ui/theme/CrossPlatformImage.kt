package com.lswmobile.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

/**
 * Cross-platform image resource state
 */
sealed class ImageResource {
    object Loading : ImageResource()
    data class Success(val painter: Painter) : ImageResource()
    data class Error(val throwable: Throwable? = null) : ImageResource()
}

/**
 * Load an image asynchronously from a URL
 * Platform-specific implementations will handle this differently
 */
@Composable
expect fun rememberAsyncImagePainter(url: String): ImageResource

/**
 * Cross-platform image component that can load images from URLs
 */
@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    alpha: Float = 1.0f,
    loadingColor: Color = MaterialTheme.colorScheme.primary,
    errorColor: Color = MaterialTheme.colorScheme.error
) {
    val imageResource = rememberAsyncImagePainter(url)
    
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = alignment
    ) {
        when (imageResource) {
            is ImageResource.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center),
                    color = loadingColor
                )
            }
            is ImageResource.Success -> {
                androidx.compose.foundation.Image(
                    painter = imageResource.painter,
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    alignment = alignment,
                    alpha = alpha,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is ImageResource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(errorColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Unable to load image",
                        color = errorColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
