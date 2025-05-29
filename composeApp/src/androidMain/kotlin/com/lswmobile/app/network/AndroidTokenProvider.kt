package com.lswmobile.app.network

import android.content.Context
import android.content.SharedPreferences

/**
 * Android-specific implementation of TokenProvider using SharedPreferences
 */
class AndroidTokenProvider(
    context: Context
) : TokenProvider {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    override fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }
    
    override fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }
    
    override suspend fun refreshTokens(refreshToken: String): Pair<String, String>? {
        // This will be implemented with the LivestockWealthApi
        // We'll need to call the refresh token endpoint
        return null // Implementation will come later when we have the API
    }
    
    override fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }
    
    override fun clearTokens() {
        sharedPreferences.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            apply()
        }
    }
    
    companion object {
        private const val PREF_NAME = "livestockwealth_auth"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
