package com.lswmobile.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.Statement
import com.lswmobile.app.platform.PlatformFileUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatementViewModel(
    private val api: LivestockWealthApi
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasError = MutableStateFlow<String?>(null)
    val hasError: StateFlow<String?> = _hasError.asStateFlow()

    private val _statements = MutableStateFlow<List<Statement>>(emptyList())
    val statements: StateFlow<List<Statement>> = _statements.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    private val _downloadError = MutableStateFlow<String?>(null)
    val downloadError: StateFlow<String?> = _downloadError.asStateFlow()

    fun loadStatements() {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = null
            try {
                val response = api.getMyStatements()
                if (response.success) {
                    _statements.value = response.data
                } else {
                    _hasError.value = response.message ?: "Failed to load statements"
                }
            } catch (e: Exception) {
                _hasError.value = e.message ?: "Failed to load statements"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun downloadStatement(userId: String) {
        viewModelScope.launch {
            _isDownloading.value = true
            _downloadError.value = null
            try {
                val bytes = api.downloadStatementPdf(userId)
                val result = PlatformFileUtils.savePdf(bytes, "statement.pdf")
                if (!result.success) {
                    _downloadError.value = result.error ?: "Failed to save file"
                } else {
                    PlatformFileUtils.showDownloadCompletedNotification(
                        title = "Statement downloaded",
                        message = "Tap to open",
                        filePath = result.filePath
                    )
                }
            } catch (e: Exception) {
                _downloadError.value = e.message ?: "Failed to download statement"
            } finally {
                _isDownloading.value = false
            }
        }
    }

    fun downloadTaxCertificate(year: Int) {
        viewModelScope.launch {
            _isDownloading.value = true
            _downloadError.value = null
            try {
                val bytes = api.downloadTaxCertificate(year)
                val result = PlatformFileUtils.savePdf(bytes, "LSW_TaxCertificate_${year}.pdf")
                if (!result.success) {
                    _downloadError.value = result.error ?: "Failed to save file"
                } else {
                    PlatformFileUtils.showDownloadCompletedNotification(
                        title = "Tax certificate $year downloaded",
                        message = "Tap to open",
                        filePath = result.filePath
                    )
                }
            } catch (e: Exception) {
                _downloadError.value = e.message ?: "Failed to download tax certificate"
            } finally {
                _isDownloading.value = false
            }
        }
    }
}
