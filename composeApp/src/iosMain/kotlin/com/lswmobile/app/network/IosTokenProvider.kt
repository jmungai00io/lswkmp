package com.lswmobile.app.network

import platform.Foundation.NSUserDefaults

/**
 * iOS-specific implementation of TokenProvider using NSUserDefaults
 */
class IosTokenProvider : TokenProvider {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    
    override fun getAccessToken(): String? {
        return userDefaults.stringForKey(KEY_ACCESS_TOKEN)
    }
    
    override fun getRefreshToken(): String? {
        return userDefaults.stringForKey(KEY_REFRESH_TOKEN)
    }
    
    override suspend fun refreshTokens(refreshToken: String): Pair<String, String>? {
        // This will be implemented with the LivestockWealthApi
        // We'll need to call the refresh token endpoint
        return null // Implementation will come later when we have the API
    }
    
    override fun saveTokens(accessToken: String, refreshToken: String) {
        userDefaults.setObject(accessToken, KEY_ACCESS_TOKEN)
        userDefaults.setObject(refreshToken, KEY_REFRESH_TOKEN)
        userDefaults.synchronize()
    }
    
    override fun clearTokens() {
        userDefaults.removeObjectForKey(KEY_ACCESS_TOKEN)
        userDefaults.removeObjectForKey(KEY_REFRESH_TOKEN)
        userDefaults.synchronize()
    }
    
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
