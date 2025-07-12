package com.lswmobile.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap

@Composable
actual fun rememberImageBitmapFromBytes(bytes: ByteArray?): ImageBitmap? {
    return remember(bytes) {
        // For now, return null on iOS to show placeholder
        // TODO: Implement proper image loading when iOS interop is available
        null
    }
} 