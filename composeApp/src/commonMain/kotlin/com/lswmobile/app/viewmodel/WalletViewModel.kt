package com.lswmobile.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.GetAssetsResponse
import com.lswmobile.app.network.model.MyAsset
import com.lswmobile.app.network.model.WalletOverview
import com.lswmobile.app.utils.ErrorUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing wallet data and operations
 */
class WalletViewModel(
    private val api: LivestockWealthApi
) : ViewModel() {
    
    // Wallet overview state
    private val _walletOverview = MutableStateFlow<WalletOverview?>(null)
    val walletOverview: StateFlow<WalletOverview?> = _walletOverview.asStateFlow()
    
    // Assets state
    private val _assets = MutableStateFlow<List<MyAsset>>(emptyList())
    val assets: StateFlow<List<MyAsset>> = _assets.asStateFlow()
    
    // Loading states
    private val _isLoadingOverview = MutableStateFlow(false)
    val isLoadingOverview: StateFlow<Boolean> = _isLoadingOverview.asStateFlow()
    
    private val _isLoadingAssets = MutableStateFlow(false)
    val isLoadingAssets: StateFlow<Boolean> = _isLoadingAssets.asStateFlow()
    
    // Error states
    private val _overviewError = MutableStateFlow<String?>(null)
    val overviewError: StateFlow<String?> = _overviewError.asStateFlow()
    
    private val _assetsError = MutableStateFlow<String?>(null)
    val assetsError: StateFlow<String?> = _assetsError.asStateFlow()
    
    init {
        // Load data on initialization
        loadWalletData()
    }
    
    /**
     * Load all wallet data
     */
    fun loadWalletData() {
        loadWalletOverview()
        loadAssets()
    }
    
    /**
     * Load wallet overview
     */
    fun loadWalletOverview() {
        viewModelScope.launch {
            _isLoadingOverview.value = true
            _overviewError.value = null
            
            try {
                val overview = api.getWalletOverview()
                // Convert negative balances to positive for UI display (same as WalletService)
                val adjustedOverview = overview.copy(
                    balance = -1 * overview.balance,
                    availableBalance = -1 * overview.availableBalance,
                    totalPriceOfAssetsInWaitingList = -1 * overview.totalPriceOfAssetsInWaitingList
                )
                _walletOverview.value = adjustedOverview
            } catch (e: Exception) {
                _overviewError.value = ErrorUtils.extractErrorMessage(e, "Failed to load wallet overview")
            } finally {
                _isLoadingOverview.value = false
            }
        }
    }
    
    /**
     * Load user assets
     */
    fun loadAssets(allocated: Boolean? = null) {
        viewModelScope.launch {
            _isLoadingAssets.value = true
            _assetsError.value = null
            
            try {
                val response = api.getMyAssets(allocated)
                _assets.value = response.data ?: emptyList()
            } catch (e: Exception) {
                _assetsError.value = ErrorUtils.extractErrorMessage(e, "Failed to load assets")
            } finally {
                _isLoadingAssets.value = false
            }
        }
    }
    
    /**
     * Clear overview error
     */
    fun clearOverviewError() {
        _overviewError.value = null
    }
    
    /**
     * Clear assets error
     */
    fun clearAssetsError() {
        _assetsError.value = null
    }
    
    /**
     * Refresh all data
     */
    fun refresh() {
        loadWalletData()
    }
}
