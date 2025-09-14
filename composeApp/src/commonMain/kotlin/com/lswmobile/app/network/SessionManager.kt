package com.lswmobile.app.network

import com.lswmobile.app.network.model.TokenRefreshState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global session events to coordinate unauthorized navigation and refresh status.
 */
object SessionManager {
    private val _refreshState = MutableStateFlow<TokenRefreshState>(TokenRefreshState.Idle)
    val refreshState: StateFlow<TokenRefreshState> = _refreshState.asStateFlow()

    fun notifySuccess() {
        _refreshState.value = TokenRefreshState.Success
    }

    fun notifyUnauthorized() {
        _refreshState.value = TokenRefreshState.Unauthorized
    }

    fun notifyError(message: String) {
        _refreshState.value = TokenRefreshState.Error(message)
    }

    fun reset() {
        _refreshState.value = TokenRefreshState.Idle
    }
}
