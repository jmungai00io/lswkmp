package com.lswmobile.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.KycStatuses
import com.lswmobile.app.network.model.UserResponse
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.viewmodel.KycViewModel
import com.lswmobile.app.viewmodel.UserViewModel

/**
 * KYC Document Upload Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycDocumentUploadScreen(
    user: UserResponse? = null,
    kycViewModel: KycViewModel,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToKycInfo: () -> Unit = {}
) {
    // Collect states from ViewModel
    val isSubmitting by kycViewModel.isSubmitting.collectAsState()
    val uploadMessage by kycViewModel.uploadMessage.collectAsState()
    val uploadSuccess by kycViewModel.uploadSuccess.collectAsState()
    val error by kycViewModel.error.collectAsState()
    val hasCameraPermission by kycViewModel.hasCameraPermission.collectAsState()
    
    // Document states
    val governmentIdBytes by kycViewModel.governmentIdBytes.collectAsState()
    val proofOfAddressBytes by kycViewModel.proofOfAddressBytes.collectAsState()
    val selfieBytes by kycViewModel.selfieBytes.collectAsState()
    
    // File names
    val governmentIdFileName by kycViewModel.governmentIdFileName.collectAsState()
    val proofOfAddressFileName by kycViewModel.proofOfAddressFileName.collectAsState()
    val selfieFileName by kycViewModel.selfieFileName.collectAsState()
    
    // Check if user can proceed with KYC
    val currentKycStatus = user?.kycVerification?.status
    val canProceed = kycViewModel.canProceedWithKyc(currentKycStatus)
    
    // Handle upload success
    LaunchedEffect(uploadSuccess) {
        if (uploadSuccess) {
            // Navigate to KYC info screen after successful upload
            onNavigateToKycInfo()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KYC Verification") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("← Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // KYC Status Check
            item {
                KycStatusCard(
                    user = user,
                    canProceed = canProceed,
                    onProceed = { /* Will be handled by the UI flow */ }
                )
            }
            
            // Only show upload section if user can proceed
            if (canProceed) {
                // Information Section
                item {
                    KycInformationCard()
                }
                
                // Camera Permission Section
                item {
                    CameraPermissionCard(
                        hasPermission = hasCameraPermission,
                        onRequestPermission = {
                            // This will be handled by platform-specific code
                            kycViewModel.setCameraPermission(true)
                        }
                    )
                }
                
                // Document Upload Sections
                item {
                    DocumentUploadSection(
                        title = "Government ID",
                        description = "Upload a clear photo of your government-issued ID (passport, driver's license, national ID)",
                        hasDocument = governmentIdBytes != null,
                        fileName = governmentIdFileName,
                        onUpload = {
                            // This will be handled by platform-specific camera/gallery picker
                            // For now, we'll simulate with dummy data
                            val dummyBytes = "dummy_id_data".encodeToByteArray()
                            kycViewModel.setGovernmentId(dummyBytes, "government_id.jpg")
                        },
                        onRemove = { kycViewModel.removeGovernmentId() }
                    )
                }
                
                item {
                    DocumentUploadSection(
                        title = "Proof of Address",
                        description = "Upload a recent utility bill, bank statement, or lease agreement",
                        hasDocument = proofOfAddressBytes != null,
                        fileName = proofOfAddressFileName,
                        onUpload = {
                            val dummyBytes = "dummy_address_data".encodeToByteArray()
                            kycViewModel.setProofOfAddress(dummyBytes, "proof_of_address.jpg")
                        },
                        onRemove = { kycViewModel.removeProofOfAddress() }
                    )
                }
                
                item {
                    DocumentUploadSection(
                        title = "Selfie with ID",
                        description = "Take a selfie while holding your government ID",
                        hasDocument = selfieBytes != null,
                        fileName = selfieFileName,
                        onUpload = {
                            val dummyBytes = "dummy_selfie_data".encodeToByteArray()
                            kycViewModel.setSelfie(dummyBytes, "selfie_with_id.jpg")
                        },
                        onRemove = { kycViewModel.removeSelfie() }
                    )
                }
                
                // Submit Button
                item {
                    SubmitButton(
                        isSubmitting = isSubmitting,
                        canSubmit = kycViewModel.areDocumentsReady(),
                        onSubmit = { kycViewModel.submitDocuments() }
                    )
                }
                
                // Error Message
                if (error != null) {
                    item {
                        ErrorCard(
                            message = error!!,
                            onDismiss = { kycViewModel.clearError() }
                        )
                    }
                }
                
                // Upload Message
                if (uploadMessage != null) {
                    item {
                        UploadMessageCard(
                            message = uploadMessage!!,
                            isSuccess = uploadSuccess
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KycStatusCard(
    user: UserResponse?,
    canProceed: Boolean,
    onProceed: () -> Unit
) {
    val status = user?.kycVerification?.status
    val statusMessage = when (status) {
        KycStatuses.VERIFIED -> "✅ KYC Verified"
        KycStatuses.SUBMITTED_DOCUMENTS -> "⏳ Documents Under Review"
        KycStatuses.REJECTED -> "❌ KYC Rejected"
        KycStatuses.UNVERIFIED -> "⚠️ KYC Required"
        else -> "⚠️ KYC Required"
    }
    
    val statusColor = when (status) {
        KycStatuses.VERIFIED -> Color(0xFF4CAF50)
        KycStatuses.SUBMITTED_DOCUMENTS -> Color(0xFFFF9800)
        KycStatuses.REJECTED -> Color(0xFFF44336)
        else -> Color(0xFFFF9800)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.headlineSmall,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = when (status) {
                    KycStatuses.VERIFIED -> "Your account has been verified successfully."
                    KycStatuses.SUBMITTED_DOCUMENTS -> "Your documents are being reviewed. This usually takes 1-3 business days."
                    KycStatuses.REJECTED -> "Your verification was rejected. Please check the requirements and try again."
                    KycStatuses.UNVERIFIED -> "Please complete KYC verification to access all features."
                    else -> "Please complete KYC verification to access all features."
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            
            if (canProceed) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onProceed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start KYC Process")
                }
            }
        }
    }
}

@Composable
private fun KycInformationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Why KYC is Required",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "KYC (Know Your Customer) verification is required by law to prevent fraud and ensure secure financial transactions. This helps protect both you and our platform.",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Required Documents:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val documents = listOf(
                "Government-issued ID (passport, driver's license, national ID)",
                "Proof of address (utility bill, bank statement, lease agreement)",
                "Selfie holding your government ID"
            )
            
            documents.forEach { document ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("✓ ", color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = document,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraPermissionCard(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hasPermission) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (hasPermission) "📷 Camera Access Granted" else "📷 Camera Permission Required",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (hasPermission) 
                    "You can now take photos for your KYC documents." 
                else 
                    "Camera access is required to take photos of your documents and selfie.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            
            if (!hasPermission) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Grant Camera Permission")
                }
            }
        }
    }
}

@Composable
private fun DocumentUploadSection(
    title: String,
    description: String,
    hasDocument: Boolean,
    fileName: String?,
    onUpload: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hasDocument) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (hasDocument) {
                // Document uploaded state
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✓", color = MaterialTheme.colorScheme.primary)
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Document Uploaded",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        fileName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    TextButton(onClick = onRemove) {
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                }
            } else {
                // Upload button
                OutlinedButton(
                    onClick = onUpload,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true
                ) {
                    Text("Upload $title")
                }
            }
        }
    }
}

@Composable
private fun SubmitButton(
    isSubmitting: Boolean,
    canSubmit: Boolean,
    onSubmit: () -> Unit
) {
    Button(
        onClick = onSubmit,
        modifier = Modifier.fillMaxWidth(),
        enabled = canSubmit && !isSubmitting
    ) {
        if (isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Submitting...")
        } else {
            Text("Submit Documents")
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("❌", color = MaterialTheme.colorScheme.error)
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    }
}

@Composable
private fun UploadMessageCard(
    message: String,
    isSuccess: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSuccess) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isSuccess) "✅" else "ℹ️",
                color = if (isSuccess) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
} 