package com.lswmobile.app.ui.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

@Composable
actual fun rememberImageBitmapFromBytes(bytes: ByteArray?): ImageBitmap? {
    return remember(bytes) {
        bytes?.let { byteArray ->
            try {
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                bitmap?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }
} 