package com.lswmobile.app.platform

/**
 * Cross-platform helper to ensure the user has granted permission to show notifications.
 */
expect object NotificationPermissionManager {
    suspend fun ensurePermission(): Boolean
    fun onPermissionResult(requestCode: Int, grantResults: IntArray)
}
