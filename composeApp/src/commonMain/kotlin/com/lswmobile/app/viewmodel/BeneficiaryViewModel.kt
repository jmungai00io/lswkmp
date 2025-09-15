package com.lswmobile.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.Beneficiary
import com.lswmobile.app.network.model.IdClass
import com.lswmobile.app.network.model.IdType
import com.lswmobile.app.utils.ErrorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BeneficiaryViewModel(
    private val api: LivestockWealthApi,
    private val coroutineScope: CoroutineScope
) {
    
    // State
    private var _beneficiaries by mutableStateOf<List<Beneficiary>>(emptyList())
    val beneficiaries: List<Beneficiary> get() = _beneficiaries
    
    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading
    
    private var _error by mutableStateOf<String?>(null)
    val error: String? get() = _error
    
    private var _successMessage by mutableStateOf<String?>(null)
    val successMessage: String? get() = _successMessage
    
    // ID Types for dropdown
    val idTypes = listOf(IdType.PASSPORT, IdType.NATIONAL_ID)
    
    /**
     * Fetch all beneficiaries
     */
    fun fetchBeneficiaries(page: Int = 1, limit: Int = 10) {
        coroutineScope.launch {
            try {
                _isLoading = true
                _error = null
                

                val response = withContext(Dispatchers.IO) {
                    api.getBeneficiaries(page = page, limit = limit)
                }
                
                if (response.success) {
                    _beneficiaries = response.data
                } else {
                    _error = "Failed to fetch beneficiaries"
                }
            } catch (e: Exception) {
                _error = ErrorUtils.extractErrorMessage(e, "Failed to fetch beneficiaries")
            } finally {
                _isLoading = false
            }
        }
    }
    
    /**
     * Add a new beneficiary
     */
    fun addBeneficiary(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        idType: IdType,
        idValue: String
    ) {
        coroutineScope.launch {
            try {
                _isLoading = true
                _error = null
                _successMessage = null
                
                val beneficiary = Beneficiary(
                    _id = "", // Will be set by backend
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    ID = IdClass(type = idType, value = idValue),
                    createdAt = "",
                    updatedAt = ""
                )
                
                val response = withContext(Dispatchers.IO) {
                    api.addBeneficiary(beneficiary)
                }
                
                if (response.success) {
                    _successMessage = "Beneficiary added successfully"
                    // Refresh the list
                    fetchBeneficiaries()
                } else {
                    _error = "Failed to add beneficiary"
                }
            } catch (e: Exception) {
                _error = ErrorUtils.extractErrorMessage(e, "Failed to add beneficiary")
            } finally {
                _isLoading = false
            }
        }
    }
    
    /**
     * Update an existing beneficiary
     */
    fun updateBeneficiary(
        beneficiaryId: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        idType: IdType,
        idValue: String
    ) {
        coroutineScope.launch {
            try {
                _isLoading = true
                _error = null
                _successMessage = null
                
                val beneficiary = Beneficiary(
                    _id = beneficiaryId,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    ID = IdClass(type = idType, value = idValue),
                    createdAt = "",
                    updatedAt = ""
                )
                
                val response = withContext(Dispatchers.IO) {
                    api.updateBeneficiary(beneficiaryId, beneficiary)
                }
                
                if (response.success) {
                    _successMessage = "Beneficiary updated successfully"
                    // Refresh the list
                    fetchBeneficiaries()
                } else {
                    _error = "Failed to update beneficiary"
                }
            } catch (e: Exception) {
                _error = ErrorUtils.extractErrorMessage(e, "Failed to update beneficiary")
            } finally {
                _isLoading = false
            }
        }
    }
    
    /**
     * Delete a beneficiary
     */
    fun deleteBeneficiary(beneficiaryId: String) {
        coroutineScope.launch {
            try {
                _isLoading = true
                _error = null
                _successMessage = null
                
                withContext(Dispatchers.IO) {
                    api.deleteBeneficiary(beneficiaryId)
                }
                
                _successMessage = "Beneficiary deleted successfully"
                // Refresh the list
                fetchBeneficiaries()
            } catch (e: Exception) {
                _error = ErrorUtils.extractErrorMessage(e, "Failed to delete beneficiary")
            } finally {
                _isLoading = false
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _error = null
    }
    
    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        _successMessage = null
    }
} 