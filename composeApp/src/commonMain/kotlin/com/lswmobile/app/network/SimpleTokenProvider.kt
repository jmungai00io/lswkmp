package com.lswmobile.app.network

import io.ktor.util.date.getTimeMillis

/**
 * Simple in-memory implementation of TokenProvider
 * For a real app, this would store tokens securely in platform-specific storage
 */
class SimpleTokenProvider : TokenProvider {
    private var accessToken: String? = null
    private var refreshToken: String? = null
    
    // Single-flight guard for refresh to avoid multiple concurrent refresh calls
    private val refreshMutex: kotlinx.coroutines.sync.Mutex = kotlinx.coroutines.sync.Mutex()
    
    // Delegate that performs the actual refresh using API (set by repository)
    private var refreshDelegate: (suspend (String?) -> Pair<String, String>?)? = null
    
    // Absolute session cap to avoid infinite refresh over long periods (default 14 days)
    private var sessionStartAtMs: Long? = null
    private var absoluteMaxSessionMs: Long = 14L * 24L * 60L * 60L * 1000L
    
    /**
     * Wire the refresh delegate from a layer that has API access
     */
    fun setRefreshDelegate(delegate: suspend (String?) -> Pair<String, String>?) {
        this.refreshDelegate = delegate
    }
    
    /**
     * Optionally adjust absolute session duration cap
     */
    fun setAbsoluteMaxSessionMs(durationMs: Long) {
        this.absoluteMaxSessionMs = durationMs
    }
    
    override fun getAccessToken(): String? = accessToken
    
    override fun getRefreshToken(): String? = refreshToken
    
    override suspend fun refreshTokens(refreshToken: String): Pair<String, String>? {
        val delegate = refreshDelegate ?: return null
        // Enforce absolute session cap
        val start = sessionStartAtMs
        if (start != null && (getTimeMillis() - start) > absoluteMaxSessionMs) {
            return null
        }
        return refreshMutex.lockAndRun {
            // If backend uses httpOnly cookie, the passed refreshToken may be blank
            delegate(refreshToken.ifBlank { null })
        }
    }
    
    override fun saveTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        if (sessionStartAtMs == null && accessToken.isNotBlank()) {
            sessionStartAtMs = getTimeMillis()
        }
    }
    
    override fun clearTokens() {
        accessToken = null
        refreshToken = null
        sessionStartAtMs = null
    }
}

// Small helper to ensure correct unlocking
private suspend inline fun <T> kotlinx.coroutines.sync.Mutex.lockAndRun(block: () -> T): T {
    this.lock()
    try {
        return block()
    } finally {
        this.unlock()
    }
}
