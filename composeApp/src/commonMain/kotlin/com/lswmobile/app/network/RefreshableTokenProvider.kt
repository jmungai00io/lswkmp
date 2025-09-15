package com.lswmobile.app.network

/**
 * Optional capabilities for TokenProvider implementations that can perform
 * delegated refresh and session cap configuration.
 */
interface RefreshableTokenProvider : TokenProvider {
    /**
     * Provide a suspend delegate that performs the actual refresh using API.
     * The parameter may be null when backend uses httpOnly cookies.
     * Should return Pair(accessToken, refreshTokenStringOrEmpty) or null on failure.
     */
    fun setRefreshDelegate(delegate: suspend (String?) -> Pair<String, String>?)

    /**
     * Adjust absolute session duration cap to avoid endless refresh.
     */
    fun setAbsoluteMaxSessionMs(durationMs: Long)
}
