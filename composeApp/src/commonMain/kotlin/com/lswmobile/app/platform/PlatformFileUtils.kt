package com.lswmobile.app.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Result of saving a file
data class SavedFileResult(
    val success: Boolean,
    val filePath: String? = null,
    val error: String? = null
)

// Cross-platform file and notification utilities
expect object PlatformFileUtils {
    // Save PDF bytes to a sensible user-visible location and return the path
    suspend fun savePdf(bytes: ByteArray, suggestedFileName: String): SavedFileResult

    // Show a local notification indicating a download completed; clicking should open the file
    suspend fun showDownloadCompletedNotification(title: String, message: String, filePath: String?)

    // Attempt to open a saved PDF file in a native viewer
    fun openPdf(filePath: String): Boolean
}
