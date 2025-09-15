package com.lswmobile.app.network

import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.sync.Mutex

/**
 * Persistent implementation of TokenProvider using Multiplatform Settings.
 * Stores tokens securely in platform-specific storage and supports delegated refresh.
 */
class PersistentTokenProvider(
    private val settings: Settings
) : RefreshableTokenProvider {

    private val refreshMutex = Mutex()
    private var refreshDelegate: (suspend (String?) -> Pair<String, String>?)? = null

    private val KEY_ACCESS = "auth.access_token"
    private val KEY_REFRESH = "auth.refresh_token"
    private val KEY_SESSION_START = "auth.session_start_ms"
    private val KEY_ABS_MAX = "auth.abs_max_session_ms"

    // Default 14 days
    private val defaultAbsMax = 14L * 24L * 60L * 60L * 1000L

    override fun getAccessToken(): String? = settings.getStringOrNull(KEY_ACCESS)

    override fun getRefreshToken(): String? = settings.getStringOrNull(KEY_REFRESH)

    override suspend fun refreshTokens(refreshToken: String): Pair<String, String>? {
        val delegate = refreshDelegate ?: return null
        // Absolute session cap
        val start = settings.getLongOrNull(KEY_SESSION_START)
        val cap = settings.getLongOrNull(KEY_ABS_MAX) ?: defaultAbsMax
        if (start != null && (getTimeMillis() - start) > cap) {
            return null
        }
        return refreshMutex.lockAndRun {
            delegate(refreshToken.ifBlank { null })
        }
    }

    override fun saveTokens(accessToken: String, refreshToken: String) {
        settings[KEY_ACCESS] = accessToken
        settings[KEY_REFRESH] = refreshToken
        if (!settings.contains(KEY_SESSION_START) && accessToken.isNotBlank()) {
            settings[KEY_SESSION_START] = getTimeMillis()
        }
    }

    override fun clearTokens() {
        settings.remove(KEY_ACCESS)
        settings.remove(KEY_REFRESH)
        settings.remove(KEY_SESSION_START)
        // Keep absolute max setting
    }

    override fun setRefreshDelegate(delegate: suspend (String?) -> Pair<String, String>?) {
        this.refreshDelegate = delegate
    }

    override fun setAbsoluteMaxSessionMs(durationMs: Long) {
        settings[KEY_ABS_MAX] = durationMs
    }
}

private fun Settings.getStringOrNull(key: String): String? = if (contains(key)) this[key] else null
private fun Settings.getLongOrNull(key: String): Long? = if (contains(key)) this[key] else null

private suspend inline fun <T> Mutex.lockAndRun(block: () -> T): T {
    this.lock()
    try {
        return block()
    } finally {
        this.unlock()
    }
}
