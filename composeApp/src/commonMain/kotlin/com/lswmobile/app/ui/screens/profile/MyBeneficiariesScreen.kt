package com.lswmobile.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.Beneficiary
import com.lswmobile.app.ui.components.ErrorToast
import com.lswmobile.app.ui.components.SuccessToast
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.ui.utils.formatIdType
import com.lswmobile.app.ui.utils.formatName
import com.lswmobile.app.viewmodel.BeneficiaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBeneficiariesScreen(
    beneficiaryViewModel: BeneficiaryViewModel? = null,
    onNavigateBack: () -> Unit = {},
    onNavigateToAddBeneficiary: () -> Unit = {},
    onEditBeneficiary: (String) -> Unit = {},
    onDeleteBeneficiary: (String) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    // Collect ViewModel state
    val beneficiaries = beneficiaryViewModel?.beneficiaries ?: emptyList()
    val isLoading = beneficiaryViewModel?.isLoading ?: false
    val error = beneficiaryViewModel?.error
    val successMessage = beneficiaryViewModel?.successMessage
    
    // Fetch beneficiaries on first load
    LaunchedEffect(Unit) {
        beneficiaryViewModel?.fetchBeneficiaries()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Beneficiaries") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = AppIcons.Filled.Back,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAddBeneficiary) {
                        Icon(
                            imageVector = AppIcons.Filled.Add,
                            contentDescription = "Add Beneficiary"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddBeneficiary
            ) {
                Icon(
                    imageVector = AppIcons.Filled.Add,
                    contentDescription = "Add Beneficiary"
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && beneficiaries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (beneficiaries.isEmpty() && !isLoading) {
                        // Empty state
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = AppIcons.Filled.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "No Beneficiaries Yet",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Add beneficiaries to ensure your investments benefit your loved ones",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Button(
                                onClick = onNavigateToAddBeneficiary
                            ) {
                                Icon(
                                    imageVector = AppIcons.Filled.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Your First Beneficiary")
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                // Header card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Your Beneficiaries",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        Text(
                                            text = "${beneficiaries.size} beneficiary${if (beneficiaries.size != 1) "s" else ""}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                            
                            items(beneficiaries) { beneficiary ->
                                BeneficiaryCard(
                                    beneficiary = beneficiary,
                                    onEdit = { onEditBeneficiary(beneficiary._id) },
                                    onDelete = { 
                                        // Show confirmation dialog
                                        onDeleteBeneficiary(beneficiary._id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Error and Success Toasts (overlay)
            ErrorToast(
                errorMessage = error,
                onDismiss = { beneficiaryViewModel?.clearError() }
            )
            
            SuccessToast(
                successMessage = successMessage,
                onDismiss = { beneficiaryViewModel?.clearSuccessMessage() }
            )
        }
    }
}

@Composable
private fun BeneficiaryCard(
    beneficiary: Beneficiary,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = formatName("${beneficiary.firstName} ${beneficiary.lastName}"),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // ID Information
                    Text(
                        text = "${formatIdType(beneficiary.ID.type.name)}: ${beneficiary.ID.value}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Contact info
                    Text(
                        text = beneficiary.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = beneficiary.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // Action buttons
                    Row {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = AppIcons.Filled.Description,
                                contentDescription = "Edit",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = AppIcons.Filled.Clear,
                                contentDescription = "Delete",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
} 