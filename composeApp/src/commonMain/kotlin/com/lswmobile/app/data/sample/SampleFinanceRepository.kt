package com.lswmobile.app.data.sample

import com.lswmobile.app.network.model.BankType
import com.lswmobile.app.network.model.EftDetailsType
import com.lswmobile.app.network.model.Statement
import com.lswmobile.app.network.model.UserResponse
import com.lswmobile.app.network.model.WalletOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

/**
 * Sample repository for financial data with hardcoded data
 * Will be replaced with actual API integration later
 */
class SampleFinanceRepository {
    
    // Wallet data
    private val _walletOverview = MutableStateFlow(
        WalletOverview(
            success = true,
            balance = 43500.0,
            availableBalance = 38750.0,
            totalPriceOfAssetsInWaitingList = 4750.0
        )
    )
    val walletOverview: Flow<WalletOverview> = _walletOverview.asStateFlow()
    
    // Statement data
    private val _statements = MutableStateFlow<List<Statement>>(generateSampleStatements())
    val statements: Flow<List<Statement>> = _statements.asStateFlow()
    
    // Beneficiaries data
    private val _beneficiaries = MutableStateFlow<List<BeneficiaryAccount>>(generateSampleBeneficiaries())
    val beneficiaries: Flow<List<BeneficiaryAccount>> = _beneficiaries.asStateFlow()
    
    // Bank details for EFT
    private val _bankDetails = MutableStateFlow(
        EftDetailsType(
            bankName = "Livestock Wealth",
            accountNumber = "1234567890",
            accountType = "Current",
            branchCode = "250655",
            accountHolderName = "Livestock Wealth Ltd"
        )
    )
    val bankDetails: Flow<EftDetailsType> = _bankDetails.asStateFlow()
    
    // Available banks for debit orders
    private val _availableBanks = MutableStateFlow<List<BankType>>(generateSampleBanks())
    val availableBanks: Flow<List<BankType>> = _availableBanks.asStateFlow()
    
    // Withdrawals
    private val _withdrawals = MutableStateFlow<List<Withdrawal>>(generateSampleWithdrawals())
    val withdrawals: Flow<List<Withdrawal>> = _withdrawals.asStateFlow()
    
    /**
     * Get statement by ID
     */
    fun getStatementById(statementId: String): Statement? {
        return _statements.value.find { it._id == statementId }
    }
    
    /**
     * Get beneficiary by ID
     */
    fun getBeneficiaryById(beneficiaryId: String): BeneficiaryAccount? {
        return _beneficiaries.value.find { it.id == beneficiaryId }
    }
    
    /**
     * Get withdrawal by ID
     */
    fun getWithdrawalById(withdrawalId: String): Withdrawal? {
        return _withdrawals.value.find { it.id == withdrawalId }
    }
    
    /**
     * Add beneficiary (for sample UI interaction)
     */
    fun addBeneficiary(beneficiary: BeneficiaryAccount) {
        val currentList = _beneficiaries.value.toMutableList()
        currentList.add(beneficiary.copy(id = "ben_${currentList.size + 1}"))
        _beneficiaries.value = currentList
    }
    
    /**
     * Request withdrawal (for sample UI interaction)
     */
    fun requestWithdrawal(amount: Double, beneficiaryId: String): Withdrawal {
        val beneficiary = getBeneficiaryById(beneficiaryId)
        val withdrawal = Withdrawal(
            id = "with_${_withdrawals.value.size + 1}",
            amount = amount,
            status = "pending",
            beneficiaryId = beneficiaryId,
            beneficiaryName = beneficiary?.accountHolderName ?: "Unknown",
            beneficiaryBank = beneficiary?.bankName ?: "Unknown",
            requestDate = formatDate(Clock.System.now()),
            processDate = null
        )
        
        val currentList = _withdrawals.value.toMutableList()
        currentList.add(withdrawal)
        _withdrawals.value = currentList
        
        // Update wallet balance
        val currentWallet = _walletOverview.value
        _walletOverview.value = currentWallet.copy(
            availableBalance = currentWallet.availableBalance - amount,
            balance = currentWallet.balance
        )
        
        return withdrawal
    }
    
    /**
     * Generate sample statements
     */
    private fun generateSampleStatements(): List<Statement> {
        val baseTime = Clock.System.now()
        val user = UserResponse(
            _id = "user_1",
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            customerRef = "CUST-12345",
            isKYCed = true,
            phoneNumber = "+27123456789",
            enabled = true,
            role = "user",
            dateCreated = baseTime.toString(),
            __v = 0,
            dateVerified = baseTime.toString(),
            lastLoginDate = baseTime.toString(),
            customerId = 12345,
            _preferences = UserResponse.Preferences(
                shouldAutoReinvest = true,
                shouldReceiveSmsNotifications = true
            ),
            kycVerification = UserResponse.KycVerification(status = "verified"),
            updatedAt = baseTime.toString(),
            kycDocumentsSubmissionDate = baseTime.toString(),
            lastLoginLocation = "Johannesburg, South Africa",
            avatarUrl = "https://randomuser.me/api/portraits/men/1.jpg"
        )
        
        return listOf(
            Statement(
                _id = "stmt_1",
                userId = "user_1",
                reference = "INV-12345",
                label = "Investment Purchase",
                dateOfTransaction = formatDate(baseTime - 30.days),
                amount = -25000.0,
                balance = 25000.0,
                transactionType = "purchase",
                paymentMethod = "eft",
                paymentRef = "EFT-12345",
                note = "Purchase of Premium Cattle Investment",
                user = user
            ),
            Statement(
                _id = "stmt_2",
                userId = "user_1",
                reference = "DIV-12345",
                label = "Dividend Payment",
                dateOfTransaction = formatDate(baseTime - 20.days),
                amount = 1250.0,
                balance = 26250.0,
                transactionType = "dividend",
                paymentMethod = "internal",
                paymentRef = "DIV-12345",
                note = "Quarterly dividend from Premium Cattle Investment",
                user = user
            ),
            Statement(
                _id = "stmt_3",
                userId = "user_1",
                reference = "INV-23456",
                label = "Investment Purchase",
                dateOfTransaction = formatDate(baseTime - 15.days),
                amount = -10000.0,
                balance = 16250.0,
                transactionType = "purchase",
                paymentMethod = "debit",
                paymentRef = "DEBIT-23456",
                note = "Purchase of Organic Vegetable Farm",
                user = user
            ),
            Statement(
                _id = "stmt_4",
                userId = "user_1",
                reference = "DEP-34567",
                label = "Deposit",
                dateOfTransaction = formatDate(baseTime - 10.days),
                amount = 30000.0,
                balance = 46250.0,
                transactionType = "deposit",
                paymentMethod = "eft",
                paymentRef = "EFT-34567",
                note = "Deposit to wallet",
                user = user
            ),
            Statement(
                _id = "stmt_5",
                userId = "user_1",
                reference = "INV-34567",
                label = "Investment Purchase",
                dateOfTransaction = formatDate(baseTime - 5.days),
                amount = -18000.0,
                balance = 28250.0,
                transactionType = "purchase",
                paymentMethod = "wallet",
                paymentRef = "WALLET-34567",
                note = "Purchase of Aquaponics Fish Farm",
                user = user
            ),
            Statement(
                _id = "stmt_6",
                userId = "user_1",
                reference = "DIV-23456",
                label = "Dividend Payment",
                dateOfTransaction = formatDate(baseTime - 2.days),
                amount = 500.0,
                balance = 28750.0,
                transactionType = "dividend",
                paymentMethod = "internal",
                paymentRef = "DIV-23456",
                note = "Monthly dividend from Organic Vegetable Farm",
                user = user
            )
        )
    }
    
    /**
     * Sample beneficiary account for banking operations
     */
    data class BeneficiaryAccount(
        val id: String,
        val accountHolderName: String,
        val accountNumber: String,
        val bankName: String,
        val branchCode: String,
        val accountType: String,
        val isDefault: Boolean = false
    )
    
    /**
     * Generate sample beneficiaries
     */
    private fun generateSampleBeneficiaries(): List<BeneficiaryAccount> {
        return listOf(
            BeneficiaryAccount(
                id = "ben_1",
                accountHolderName = "John Doe",
                accountNumber = "9876543210",
                bankName = "Standard Bank",
                branchCode = "051001",
                accountType = "Current",
                isDefault = true
            ),
            BeneficiaryAccount(
                id = "ben_2",
                accountHolderName = "John Doe",
                accountNumber = "1122334455",
                bankName = "First National Bank",
                branchCode = "250655",
                accountType = "Savings",
                isDefault = false
            )
        )
    }
    
    /**
     * Sample withdrawal model
     */
    data class Withdrawal(
        val id: String,
        val amount: Double,
        val status: String, // pending, completed, rejected
        val beneficiaryId: String,
        val beneficiaryName: String,
        val beneficiaryBank: String,
        val requestDate: String,
        val processDate: String?
    )
    
    /**
     * Generate sample withdrawals
     */
    private fun generateSampleWithdrawals(): List<Withdrawal> {
        val baseTime = Clock.System.now()
        
        return listOf(
            Withdrawal(
                id = "with_1",
                amount = 5000.0,
                status = "completed",
                beneficiaryId = "ben_1",
                beneficiaryName = "John Doe",
                beneficiaryBank = "Standard Bank",
                requestDate = formatDate(baseTime - 25.days),
                processDate = formatDate(baseTime - 23.days)
            ),
            Withdrawal(
                id = "with_2",
                amount = 2500.0,
                status = "completed",
                beneficiaryId = "ben_2",
                beneficiaryName = "John Doe",
                beneficiaryBank = "First National Bank",
                requestDate = formatDate(baseTime - 15.days),
                processDate = formatDate(baseTime - 14.days)
            ),
            Withdrawal(
                id = "with_3",
                amount = 3000.0,
                status = "pending",
                beneficiaryId = "ben_1",
                beneficiaryName = "John Doe",
                beneficiaryBank = "Standard Bank",
                requestDate = formatDate(baseTime - 2.days),
                processDate = null
            )
        )
    }
    
    /**
     * Generate sample banks
     */
    private fun generateSampleBanks(): List<BankType> {
        return listOf(
            BankType("Standard Bank", "051001"),
            BankType("First National Bank", "250655"),
            BankType("ABSA Bank", "632005"),
            BankType("Nedbank", "198765"),
            BankType("Capitec Bank", "470010"),
            BankType("Discovery Bank", "679000"),
            BankType("Investec Bank", "580105"),
            BankType("African Bank", "430000"),
            BankType("Bidvest Bank", "462005"),
            BankType("TymeBank", "678910")
        )
    }
    
    /**
     * Format date to friendly string
     */
    private fun formatDate(instant: Instant): String {
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}-${dateTime.dayOfMonth.toString().padStart(2, '0')}"
    }
    
    companion object {
        private var instance: SampleFinanceRepository? = null
        
        fun getInstance(): SampleFinanceRepository {
            return instance ?: SampleFinanceRepository().also { instance = it }
        }
    }
}
