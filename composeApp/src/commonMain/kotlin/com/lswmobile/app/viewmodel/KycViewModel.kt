package com.lswmobile.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lswmobile.app.data.repository.KycRepository
import com.lswmobile.app.data.repository.UserRepository
import com.lswmobile.app.network.model.KycStatuses
import com.lswmobile.app.network.model.KycUploadResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for KYC verification process
 */
class KycViewModel(
    private val kycRepository: KycRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    // Document states
    private val _governmentIdBytes = MutableStateFlow<ByteArray?>(null)
    val governmentIdBytes: StateFlow<ByteArray?> = _governmentIdBytes.asStateFlow()
    
    private val _proofOfAddressBytes = MutableStateFlow<ByteArray?>(null)
    val proofOfAddressBytes: StateFlow<ByteArray?> = _proofOfAddressBytes.asStateFlow()
    
    private val _selfieBytes = MutableStateFlow<ByteArray?>(null)
    val selfieBytes: StateFlow<ByteArray?> = _selfieBytes.asStateFlow()
    
    // File names for upload
    private val _governmentIdFileName = MutableStateFlow<String?>(null)
    val governmentIdFileName: StateFlow<String?> = _governmentIdFileName.asStateFlow()
    
    private val _proofOfAddressFileName = MutableStateFlow<String?>(null)
    val proofOfAddressFileName: StateFlow<String?> = _proofOfAddressFileName.asStateFlow()
    
    private val _selfieFileName = MutableStateFlow<String?>(null)
    val selfieFileName: StateFlow<String?> = _selfieFileName.asStateFlow()
    
    // UI states
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()
    
    private val _uploadMessage = MutableStateFlow<String?>(null)
    val uploadMessage: StateFlow<String?> = _uploadMessage.asStateFlow()
    
    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess: StateFlow<Boolean> = _uploadSuccess.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Camera permission state
    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission: StateFlow<Boolean> = _hasCameraPermission.asStateFlow()
    
    /**
     * Set government ID document
     */
    fun setGovernmentId(bytes: ByteArray, fileName: String) {
        _governmentIdBytes.value = bytes
        _governmentIdFileName.value = fileName
        clearError()
    }
    
    /**
     * Set proof of address document
     */
    fun setProofOfAddress(bytes: ByteArray, fileName: String) {
        _proofOfAddressBytes.value = bytes
        _proofOfAddressFileName.value = fileName
        clearError()
    }
    
    /**
     * Set selfie document
     */
    fun setSelfie(bytes: ByteArray, fileName: String) {
        _selfieBytes.value = bytes
        _selfieFileName.value = fileName
        clearError()
    }
    
    /**
     * Remove government ID document
     */
    fun removeGovernmentId() {
        _governmentIdBytes.value = null
        _governmentIdFileName.value = null
    }
    
    /**
     * Remove proof of address document
     */
    fun removeProofOfAddress() {
        _proofOfAddressBytes.value = null
        _proofOfAddressFileName.value = null
    }
    
    /**
     * Remove selfie document
     */
    fun removeSelfie() {
        _selfieBytes.value = null
        _selfieFileName.value = null
    }
    
    /**
     * Set camera permission status
     */
    fun setCameraPermission(granted: Boolean) {
        _hasCameraPermission.value = granted
    }
    
    /**
     * Check if all documents are ready for submission
     */
    fun areDocumentsReady(): Boolean {
        return _governmentIdBytes.value != null && 
               _proofOfAddressBytes.value != null && 
               _selfieBytes.value != null
    }
    
    /**
     * Submit KYC documents
     */
    fun submitDocuments() {
        if (!areDocumentsReady()) {
            _error.value = "Please upload all required documents"
            return
        }
        
        viewModelScope.launch {
            try {
                _isSubmitting.value = true
                _error.value = null
                _uploadMessage.value = "Uploading documents..."
                
                val govtIdBytes = _governmentIdBytes.value!!
                val proofOfAddressBytes = _proofOfAddressBytes.value!!
                val selfieBytes = _selfieBytes.value!!
                val govtIdFileName = _governmentIdFileName.value!!
                val proofOfAddressFileName = _proofOfAddressFileName.value!!
                val selfieFileName = _selfieFileName.value!!
                
                val result = kycRepository.uploadKycDocuments(
                    governmentIdBytes = govtIdBytes,
                    proofOfAddressBytes = proofOfAddressBytes,
                    selfieBytes = selfieBytes,
                    governmentIdFileName = govtIdFileName,
                    proofOfAddressFileName = proofOfAddressFileName,
                    selfieFileName = selfieFileName
                )
                
                result.fold(
                    onSuccess = { response ->
                        _uploadMessage.value = "Documents submitted successfully"
                        _uploadSuccess.value = true
                        
                        // Refresh user data to get updated KYC status
                        userRepository.fetchUser()
                        
                        // Clear documents after successful upload
                        clearDocuments()
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Failed to upload documents"
                        _uploadSuccess.value = false
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred"
                _uploadSuccess.value = false
            } finally {
                _isSubmitting.value = false
            }
        }
    }
    
    /**
     * Clear all documents
     */
    private fun clearDocuments() {
        _governmentIdBytes.value = null
        _proofOfAddressBytes.value = null
        _selfieBytes.value = null
        _governmentIdFileName.value = null
        _proofOfAddressFileName.value = null
        _selfieFileName.value = null
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Reset upload state
     */
    fun resetUploadState() {
        _uploadMessage.value = null
        _uploadSuccess.value = false
        _error.value = null
    }
    
    /**
     * Get KYC status message based on status
     */
    fun getKycStatusMessage(status: String?): String {
        return when (status) {
            KycStatuses.VERIFIED -> "Your KYC verification is complete and approved."
            KycStatuses.SUBMITTED_DOCUMENTS -> "Your documents have been submitted and are under review."
            KycStatuses.REJECTED -> "Your KYC verification was rejected. Please check the requirements and try again."
            KycStatuses.UNVERIFIED -> "Please complete your KYC verification to access all features."
            else -> "Please complete your KYC verification to access all features."
        }
    }
    
    /**
     * Check if user can proceed with KYC upload
     */
    fun canProceedWithKyc(currentStatus: String?): Boolean {
        return currentStatus == KycStatuses.UNVERIFIED || currentStatus.isNullOrEmpty()
    }
} 