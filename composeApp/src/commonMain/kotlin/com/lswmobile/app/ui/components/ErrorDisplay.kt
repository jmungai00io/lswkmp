package com.lswmobile.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

/**
 * Toast positioning options
 */
enum class ToastPosition {
    TOP,
    CENTER,
    BOTTOM
}

/**
 * Reusable error/success display component
 * Can be used as toast (auto-dismiss) or modal (manual dismiss)
 */
@Composable
fun ErrorDisplay(
    message: String?,
    isError: Boolean = true,
    isModal: Boolean = false,
    position: ToastPosition = ToastPosition.TOP,
    autoHideDuration: Long = 4000L,
    onDismiss: () -> Unit
) {
    val isVisible = !message.isNullOrBlank()
    
    // Auto-hide for toast mode
    LaunchedEffect(message) {
        if (isVisible && !isModal && autoHideDuration > 0) {
            delay(autoHideDuration)
            onDismiss()
        }
    }
    
    if (isModal) {
        // Modal Dialog
        if (isVisible) {
            Dialog(
                onDismissRequest = onDismiss,
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                ErrorCard(
                    message = message!!,
                    isError = isError,
                    onDismiss = onDismiss,
                    isModal = true
                )
            }
        }
    } else {
        // Toast (overlay)
        val alignment = when (position) {
            ToastPosition.TOP -> Alignment.TopCenter
            ToastPosition.CENTER -> Alignment.Center
            ToastPosition.BOTTOM -> Alignment.BottomCenter
        }
        
        val topPadding = when (position) {
            ToastPosition.TOP -> 80.dp // Extra padding for top position to avoid top bar
            ToastPosition.CENTER -> 0.dp
            ToastPosition.BOTTOM -> 0.dp
        }
        
        val bottomPadding = when (position) {
            ToastPosition.TOP -> 0.dp
            ToastPosition.CENTER -> 0.dp
            ToastPosition.BOTTOM -> 100.dp // Extra padding for bottom position to avoid bottom nav
        }
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = alignment
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = when (position) {
                    ToastPosition.TOP -> slideInVertically(initialOffsetY = { -it }) + fadeIn()
                    ToastPosition.CENTER -> fadeIn()
                    ToastPosition.BOTTOM -> slideInVertically(initialOffsetY = { it }) + fadeIn()
                },
                exit = when (position) {
                    ToastPosition.TOP -> slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                    ToastPosition.CENTER -> fadeOut()
                    ToastPosition.BOTTOM -> slideOutVertically(targetOffsetY = { it }) + fadeOut()
                }
            ) {
                Box(
                    modifier = Modifier.padding(top = topPadding, bottom = bottomPadding)
                ) {
                    ErrorCard(
                        message = message ?: "",
                        isError = isError,
                        onDismiss = onDismiss,
                        isModal = false
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit,
    isModal: Boolean
) {
    val backgroundColor = if (isError) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        Color(0xFF4CAF50).copy(alpha = 0.1f)
    }
    
    val contentColor = if (isError) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        Color(0xFF2E7D32)
    }
    
    // Use simple text symbols instead of icons for testing
    val iconText = if (isError) "⚠️" else "✅"
    
    Card(
        modifier = Modifier
            .fillMaxWidth(if (isModal) 0.9f else 0.95f)
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Use Text instead of Icon for testing
            Text(
                text = iconText,
                fontSize = 24.sp,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = message,
                color = contentColor,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f),
                textAlign = if (isModal) TextAlign.Center else TextAlign.Start
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Use simple text X instead of close icon
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Text(
                    text = "✕",
                    color = contentColor,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Convenience composable for error messages
 */
@Composable
fun ErrorToast(
    errorMessage: String?,
    position: ToastPosition = ToastPosition.BOTTOM,
    onDismiss: () -> Unit
) {
    ErrorDisplay(
        message = errorMessage,
        isError = true,
        isModal = false,
        position = position,
        onDismiss = onDismiss
    )
}

/**
 * Convenience composable for success messages
 */
@Composable
fun SuccessToast(
    successMessage: String?,
    position: ToastPosition = ToastPosition.BOTTOM,
    onDismiss: () -> Unit
) {
    ErrorDisplay(
        message = successMessage,
        isError = false,
        isModal = false,
        position = position,
        onDismiss = onDismiss
    )
}

/**
 * Convenience composable for error modal
 */
@Composable
fun ErrorModal(
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    ErrorDisplay(
        message = errorMessage,
        isError = true,
        isModal = true,
        onDismiss = onDismiss
    )
}
