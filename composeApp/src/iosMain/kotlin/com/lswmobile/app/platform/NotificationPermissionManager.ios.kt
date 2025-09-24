package com.lswmobile.app.platform

actual object NotificationPermissionManager {
    actual suspend fun ensurePermission(): Boolean = true
    actual fun onPermissionResult(requestCode: Int, grantResults: IntArray) = Unit
}
