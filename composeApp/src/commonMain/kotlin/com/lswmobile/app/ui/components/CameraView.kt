package com.lswmobile.app.ui.components

import androidx.compose.runtime.Composable

/**
 * Simple camera view interface for KMP
 */
@Composable
expect fun CameraView(
    onPhotoTaken: (ByteArray) -> Unit = {},
    onError: (String) -> Unit = {}
) 