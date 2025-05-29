package com.lswmobile.app.network

/**
 * TokenProvider interface for handling authentication tokens
 */
interface TokenProvider {
    /**
     * Get the current access token
     */
    fun getAccessToken(): String?
    
    /**
     * Get the current refresh token
     */
    fun getRefreshToken(): String?
    
    /**
     * Refresh the tokens using the given refresh token
     * @param refreshToken The refresh token to use
     * @return A pair of new access token and refresh token, or null if refresh failed
     */
    suspend fun refreshTokens(refreshToken: String): Pair<String, String>?
    
    /**
     * Save new tokens
     * @param accessToken The new access token
     * @param refreshToken The new refresh token
     */
    fun saveTokens(accessToken: String, refreshToken: String)
    
    /**
     * Clear all tokens (for logout)
     */
    fun clearTokens()
}
