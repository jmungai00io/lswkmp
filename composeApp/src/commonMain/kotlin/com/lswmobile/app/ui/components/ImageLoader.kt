package com.lswmobile.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun rememberImageBitmapFromBytes(bytes: ByteArray?): ImageBitmap?

@Composable
fun loadImageFromBytes(bytes: ByteArray?): ImageBitmap? {
    return rememberImageBitmapFromBytes(bytes)
} 