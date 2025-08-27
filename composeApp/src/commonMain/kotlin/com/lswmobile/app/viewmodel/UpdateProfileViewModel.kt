package com.lswmobile.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lswmobile.app.network.model.UserResponse
import com.lswmobile.app.network.repository.UserRepository
import com.lswmobile.app.ui.utils.isValidPhoneNumber
import com.lswmobile.app.utils.ErrorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class UpdateProfileViewModel(
    private val userRepository: UserRepository,
    private val coroutineScope: CoroutineScope
) {
    
    // Form state
    var firstName by mutableStateOf("")
        private set
    
    var lastName by mutableStateOf("")
        private set
    
    var phoneNumber by mutableStateOf("")
        private set
    
    var dateOfBirth by mutableStateOf("")
        private set
    
    var country by mutableStateOf("")
        private set
    
    var town by mutableStateOf("")
        private set
    
    var address by mutableStateOf("")
        private set
    
    var zipCode by mutableStateOf("")
        private set
    
    var gender by mutableStateOf("")
        private set
    
    // UI state
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var successMessage by mutableStateOf<String?>(null)
        private set
    
    // Validation state
    var isPhoneNumberValid by mutableStateOf(true)
        private set
    
    var isFormValid by mutableStateOf(false)
        private set
    
    // Gender options
    val genderOptions = listOf("MALE", "FEMALE")
    
    /**
     * Initialize form with user data
     */
    fun initializeForm(user: UserResponse?) {
        user?.let {
            firstName = it.firstName ?: ""
            lastName = it.lastName ?: ""
            phoneNumber = it.phoneNumber ?: ""
            dateOfBirth = it.dateOfBirth ?: ""
            country = it.country ?: ""
            town = it.town ?: ""
            address = it.address ?: ""
            zipCode = it.zipCode ?: ""
            gender = it.gender ?: ""
        }
        validateForm()
    }
    
    /**
     * Update first name
     */
    fun updateFirstName(value: String) {
        firstName = value
        validateForm()
    }
    
    /**
     * Update last name
     */
    fun updateLastName(value: String) {
        lastName = value
        validateForm()
    }
    
    /**
     * Update phone number with validation
     */
    fun updatePhoneNumber(value: String) {
        phoneNumber = value
        isPhoneNumberValid = value.isEmpty() || isValidPhoneNumber(value)
        validateForm()
    }
    
    /**
     * Update date of birth
     */
    fun updateDateOfBirth(value: String) {
        dateOfBirth = value
        validateForm()
    }
    
    /**
     * Update country
     */
    fun updateCountry(value: String) {
        country = value
        validateForm()
    }
    
    /**
     * Update town
     */
    fun updateTown(value: String) {
        town = value
        validateForm()
    }
    
    /**
     * Update address
     */
    fun updateAddress(value: String) {
        address = value
        validateForm()
    }
    
    /**
     * Update zip code
     */
    fun updateZipCode(value: String) {
        zipCode = value
        validateForm()
    }
    
    /**
     * Update gender
     */
    fun updateGender(value: String) {
        gender = value
        validateForm()
    }
    
    /**
     * Validate form
     */
    private fun validateForm() {
        isFormValid = firstName.isNotBlank() && 
                     lastName.isNotBlank() && 
                     phoneNumber.isNotBlank() && 
                     isPhoneNumberValid &&
                     dateOfBirth.isNotBlank() &&
                     gender.isNotBlank()
    }
    
    /**
     * Update profile
     */
    fun updateProfile(onSuccess: () -> Unit = {}) {
        if (!isFormValid) {
            errorMessage = "Please fill in all required fields correctly"
            return
        }
        
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            
            try {
                val result = userRepository.updateProfile(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    dateOfBirth = dateOfBirth,
                    country = country,
                    town = town,
                    address = address,
                    zipCode = zipCode,
                    gender = gender
                )
                
                if (result.isSuccess) {
                    successMessage = "Profile updated successfully"
                    onSuccess()
                } else {
                    val exception = result.exceptionOrNull()
                    val errorException = if (exception is Exception) exception else Exception(exception?.message, exception)
                    errorMessage = ErrorUtils.extractErrorMessage(errorException, "Failed to update profile")
                }
            } catch (e: Throwable) {
                val errorException = if (e is Exception) e else Exception(e.message, e)
                errorMessage = ErrorUtils.extractErrorMessage(errorException, "An unexpected error occurred")
            } finally {
                isLoading = false
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        errorMessage = null
    }
    
    /**
     * Clear success message
     */
    fun clearSuccess() {
        successMessage = null
    }
} 