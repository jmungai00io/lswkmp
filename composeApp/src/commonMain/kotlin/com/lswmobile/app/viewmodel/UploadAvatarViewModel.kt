package com.lswmobile.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lswmobile.app.network.repository.UserRepository
import com.lswmobile.app.utils.ErrorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * ViewModel for handling avatar upload operations
 */
class UploadAvatarViewModel(
    private val userRepository: UserRepository,
    private val coroutineScope: CoroutineScope
) {
    
    // Upload state
    var isUploading by mutableStateOf(false)
        private set
    
    var uploadError by mutableStateOf<String?>(null)
        private set
    
    var uploadSuccess by mutableStateOf(false)
        private set

    /**
     * Upload avatar image
     */
    fun uploadAvatar(fileBytes: ByteArray, fileName: String) {
        coroutineScope.launch {
            try {
                isUploading = true
                uploadError = null
                uploadSuccess = false
                
                println("UploadAvatarViewModel: Starting avatar upload")
                println("UploadAvatarViewModel: File name: $fileName")
                println("UploadAvatarViewModel: File size: ${fileBytes.size} bytes")
                
                // Log first few bytes to verify it's a valid image
                val firstBytes = fileBytes.take(10).joinToString(" ") { byte ->
                    (byte.toInt() and 0xFF).toString(16).padStart(2, '0')
                }
                println("UploadAvatarViewModel: First 10 bytes: $firstBytes")
                
                // Check if it looks like a JPEG (starts with FF D8)
                if (fileBytes.size >= 2) {
                    val isJpeg = fileBytes[0] == 0xFF.toByte() && fileBytes[1] == 0xD8.toByte()
                    println("UploadAvatarViewModel: Appears to be JPEG: $isJpeg")
                }
                
                val result = userRepository.uploadAvatar(fileBytes, fileName)
                
                result.fold(
                    onSuccess = { response ->
                        println("UploadAvatarViewModel: Avatar upload successful")
                        println("UploadAvatarViewModel: Response size: ${response.size} bytes")
                        uploadSuccess = true
                        isUploading = false
                        
                        // Optionally refresh user data to get updated avatar URL
                        refreshUserData()
                    },
                    onFailure = { exception ->
                        println("UploadAvatarViewModel: Avatar upload failed: ${exception.message}")
                        exception.printStackTrace()
                        val errorException = if (exception is Exception) exception else Exception(exception.message, exception)
                        uploadError = ErrorUtils.extractErrorMessage(errorException, "Failed to upload avatar")
                        isUploading = false
                        uploadSuccess = false
                    }
                )
            } catch (e: Throwable) {
                println("UploadAvatarViewModel: Unexpected error during avatar upload: ${e.message}")
                e.printStackTrace()
                val errorException = if (e is Exception) e else Exception(e.message, e.cause)
                uploadError = ErrorUtils.extractErrorMessage(errorException, "An unexpected error occurred")
                isUploading = false
                uploadSuccess = false
            }
        }
    }
    
    /**
     * Clear upload state
     */
    fun clearState() {
        isUploading = false
        uploadError = null
        uploadSuccess = false
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        uploadError = null
    }
    
    /**
     * Refresh user data after successful upload
     */
    private fun refreshUserData() {
        coroutineScope.launch {
            try {
                userRepository.getCurrentUser()
                println("UploadAvatarViewModel: User data refreshed after avatar upload")
            } catch (e: Exception) {
                println("UploadAvatarViewModel: Failed to refresh user data: ${e.message}")
                // Don't show error for this as the upload was successful
            }
        }
    }
}
