package com.lswmobile.app.ui.screens.payment

import com.lswmobile.app.data.repository.UserRepository
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.BankType
import com.lswmobile.app.network.model.DebitOrderBody
import com.lswmobile.app.network.model.OrderWithFullUser
import com.lswmobile.app.network.model.PaymentCompletionType
import com.lswmobile.app.network.repository.OrderRepository
import com.lswmobile.app.network.repository.WalletService
import com.lswmobile.app.utils.ErrorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for Debit payment operations
 */
class DebitPaymentViewModel(
    private val api: LivestockWealthApi,
    private val orderRepository: OrderRepository,
    private val walletService: WalletService,
    private val userRepository: UserRepository
) {
    
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    
    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isConfirming = MutableStateFlow(false)
    val isConfirming: StateFlow<Boolean> = _isConfirming.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Form data
    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()
    
    private val _accountNumber = MutableStateFlow("")
    val accountNumber: StateFlow<String> = _accountNumber.asStateFlow()
    
    private val _accountType = MutableStateFlow("")
    val accountType: StateFlow<String> = _accountType.asStateFlow()
    
    private val _debitOrderDate = MutableStateFlow("")
    val debitOrderDate: StateFlow<String> = _debitOrderDate.asStateFlow()
    
    private val _amountToDeduct = MutableStateFlow("")
    val amountToDeduct: StateFlow<String> = _amountToDeduct.asStateFlow()
    
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()
    
    private val _countryPhoneCode = MutableStateFlow("+27")
    val countryPhoneCode: StateFlow<String> = _countryPhoneCode.asStateFlow()
    
    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()
    
    private val _debitMandateSign = MutableStateFlow("")
    val debitMandateSign: StateFlow<String> = _debitMandateSign.asStateFlow()
    
    private val _selectedBank = MutableStateFlow(BankType("", ""))
    val selectedBank: StateFlow<BankType> = _selectedBank.asStateFlow()
    
    // Data state
    private val _banks = MutableStateFlow<List<BankType>>(emptyList())
    val banks: StateFlow<List<BankType>> = _banks.asStateFlow()
    
    private val _order = MutableStateFlow<OrderWithFullUser?>(null)
    val order: StateFlow<OrderWithFullUser?> = _order.asStateFlow()
    
    private val _walletBalance = MutableStateFlow(0.0)
    val walletBalance: StateFlow<Double> = _walletBalance.asStateFlow()
    
    // Validation state
    private val _fullNameError = MutableStateFlow("")
    val fullNameError: StateFlow<String> = _fullNameError.asStateFlow()
    
    private val _accountNumberError = MutableStateFlow("")
    val accountNumberError: StateFlow<String> = _accountNumberError.asStateFlow()
    
    private val _accountTypeError = MutableStateFlow("")
    val accountTypeError: StateFlow<String> = _accountTypeError.asStateFlow()
    
    private val _phoneError = MutableStateFlow("")
    val phoneError: StateFlow<String> = _phoneError.asStateFlow()
    
    private val _addressError = MutableStateFlow("")
    val addressError: StateFlow<String> = _addressError.asStateFlow()
    
    private val _debitDateError = MutableStateFlow("")
    val debitDateError: StateFlow<String> = _debitDateError.asStateFlow()
    
    private val _mandateError = MutableStateFlow("")
    val mandateError: StateFlow<String> = _mandateError.asStateFlow()
    
    private val _signTouched = MutableStateFlow(false)
    val signTouched: StateFlow<Boolean> = _signTouched.asStateFlow()
    
    /**
     * Set initial full name from user data
     */
    fun setInitialFullName(name: String) {
        _fullName.value = name
    }
    
    /**
     * Pre-populate phone number from user data
     */
    fun prePopulatePhoneNumber() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.currentUser.first()
                currentUser?.let { user ->
                    // Extract phone number without country code if it starts with +27
                    val phone = if (user.phoneNumber.startsWith("+27")) {
                        user.phoneNumber.substring(3) // Remove +27 prefix
                    } else {
                        user.phoneNumber
                    }
                    _phoneNumber.value = phone
                }
            } catch (e: Exception) {
                // Silent error for phone pre-population
            }
        }
    }
    
    /**
     * Load all required data for debit payment
     */
    fun loadDebitPaymentData(orderNumber: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Load data in parallel
                val banksJob = launch { fetchBanks() }
                val orderDetailsJob = launch { loadOrderDetails(orderNumber) }
                val walletBalanceJob = launch { loadWalletBalance() }
                val prePopulatePhoneJob = launch { prePopulatePhoneNumber() }
                
                // Wait for all to complete
                banksJob.join()
                orderDetailsJob.join()
                walletBalanceJob.join()
                prePopulatePhoneJob.join()
                
            } catch (e: Exception) {
                _errorMessage.value = ErrorUtils.extractErrorMessage(e, "Failed to load debit payment data")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetch banks list
     */
    private suspend fun fetchBanks() {
        try {
            val response = api.getLocalBanks()

            
            if (response.success) {
                _banks.value = response.banks
            } else {
                _errorMessage.value = "Failed to load banks"
            }
        } catch (e: Exception) {
            _errorMessage.value = ErrorUtils.extractErrorMessage(e, "Error loading banks")
        }
    }
    
    /**
     * Load order details
     */
    private suspend fun loadOrderDetails(orderNumber: Int) {
        try {
            orderRepository.getOrderDetails(orderNumber)
            // The order will be available through the repository's state
        } catch (e: Exception) {
            _errorMessage.value = ErrorUtils.extractErrorMessage(e, "Failed to load order details")
        }
    }
    
    /**
     * Load wallet balance
     */
    private suspend fun loadWalletBalance() {
        try {
            val balance = walletService.getWalletBalance()
            _walletBalance.value = balance
        } catch (e: Exception) {
            _errorMessage.value = ErrorUtils.extractErrorMessage(e, "Failed to load wallet balance")
        }
    }
    
    /**
     * Calculate payment amount based on payment type
     */
    fun calculatePaymentAmount(orderAmount: Double, walletBalance: Double, paymentType: PaymentType): Double {
        return when (paymentType) {
            PaymentType.FULL_PAYMENT -> orderAmount
            PaymentType.PARTIAL_TOPUP -> {
                val availableBalance = walletBalance
                if (availableBalance >= orderAmount) {
                    0.0 // No additional payment needed
                } else {
                    orderAmount - availableBalance
                }
            }
            PaymentType.TOPUP_ONLY -> 0.0 // Only topup, no order payment
        }
    }
    
    /**
     * Form field change handlers
     */
    fun onFullNameChange(name: String) {
        _fullName.value = name
        _fullNameError.value = if (name.isEmpty()) "Must enter full name" else ""
    }
    
    fun onAccountNumberChange(accountNo: String) {
        _accountNumber.value = accountNo
        _accountNumberError.value = if (accountNo.isEmpty()) "Must enter account number" else ""
    }
    
    fun onAccountTypeChange(type: String) {
        _accountType.value = type
        _accountTypeError.value = if (type.isEmpty()) "Must select account type" else ""
    }
    
    fun onDebitDateChange(date: String) {
        _debitOrderDate.value = date
        _debitDateError.value = if (date.isEmpty()) "Must choose debit order date" else ""
    }
    
    fun onAmountChange(amount: String) {
        _amountToDeduct.value = amount
    }
    
    fun onPhoneChange(phone: String) {
        _phoneNumber.value = phone
        _phoneError.value = if (phone.isEmpty()) "Must enter phone number" else ""
    }
    
    fun onAddressChange(addr: String) {
        _address.value = addr
        _addressError.value = if (addr.isEmpty()) "Must enter address" else ""
    }
    
    fun onMandateSignChange(signature: String) {
        _signTouched.value = true
        _debitMandateSign.value = signature
        _mandateError.value = if (signature.isEmpty() || !signature.isAllCaps()) "Full name in all caps" else ""
    }
    
    fun onBankChange(bank: BankType) {
        _selectedBank.value = bank
    }
    
    fun onCountryPhoneCodeChange(code: String) {
        _countryPhoneCode.value = code
    }
    
    /**
     * Check if form is valid for step 1
     */
    fun isStep1Valid(): Boolean {
        return _fullName.value.isNotEmpty() &&
                _amountToDeduct.value.isNotEmpty() &&
                _selectedBank.value.name.isNotEmpty() &&
                _accountNumber.value.isNotEmpty() &&
                _accountType.value.isNotEmpty() &&
                _phoneNumber.value.isNotEmpty() &&
                _debitOrderDate.value.isNotEmpty() &&
                _address.value.isNotEmpty()
    }
    
    /**
     * Check if form is valid for step 2
     */
    fun isStep2Valid(): Boolean {
        return _debitMandateSign.value.isNotEmpty() &&
                _debitMandateSign.value.isAllCaps() &&
                _signTouched.value
    }
    
    /**
     * Confirm debit payment
     */
    fun confirmDebitPayment(orderNumber: Int, paymentType: PaymentType, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _isConfirming.value = true
                _errorMessage.value = null
                
                // Create payment body with correct payment type string
                val paymentTypeString = when (paymentType) {
                    PaymentType.TOPUP_ONLY -> PaymentCompletionType.TOPUP_ONLY
                    PaymentType.PARTIAL_TOPUP -> PaymentCompletionType.PARTIAL_TOPUP
                    PaymentType.FULL_PAYMENT -> PaymentCompletionType.FULL_PAYMENT
                }
                
                val debitOrderBody = DebitOrderBody(
                    accountName = _fullName.value,
                    accountNumber = _accountNumber.value,
                    debitOrderDate = _debitOrderDate.value,
                    orderNumber = orderNumber.toString(),
                    amount = _amountToDeduct.value,
                    branchCode = _selectedBank.value.code,
                    address = _address.value,
                    phoneNumber = "${_countryPhoneCode.value}${_phoneNumber.value}",
                    signature = _debitMandateSign.value,
                    accountType = _accountType.value.lowercase(),
                    paymentType = paymentTypeString
                )
                
                // Make the actual API call
                val response = api.setupDebitOrder(debitOrderBody)
                
                onSuccess()
                
            } catch (e: Exception) {
                _errorMessage.value = ErrorUtils.extractErrorMessage(e, "Payment failed")
            } finally {
                _isConfirming.value = false
            }
        }
    }
    
    /**
     * Clear error messages
     */
    fun clearErrorMessages() {
        _errorMessage.value = null
    }
    
    /**
     * Extension function to check if string is all caps
     */
    private fun String.isAllCaps(): Boolean {
        return this == this.uppercase() && this.isNotEmpty()
    }
} 