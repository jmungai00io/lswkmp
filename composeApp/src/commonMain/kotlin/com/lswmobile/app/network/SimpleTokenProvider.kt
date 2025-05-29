package com.lswmobile.app.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Simple in-memory implementation of TokenProvider
 * For a real app, this would store tokens securely in platform-specific storage
 */
class SimpleTokenProvider : TokenProvider {
    private var accessToken: String? = null
    private var refreshToken: String? = null
    
    override fun getAccessToken(): String? = accessToken
    
    override fun getRefreshToken(): String? = refreshToken
    
    override suspend fun refreshTokens(refreshToken: String): Pair<String, String>? {
        // In a real implementation, this would make an API call to refresh the token
        // For now, it returns null, indicating refresh failed
        return null
    }
    
    override fun saveTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }
    
    override fun clearTokens() {
        accessToken = null
        refreshToken = null
    }
}
