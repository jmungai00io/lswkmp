package com.lswmobile.app.platform

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.lswmobile.app.LivestockWealthApplication
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

actual object NotificationPermissionManager {
    private const val REQUEST_CODE = 9123
    private val mutex = Mutex()
    private var pendingRequest: CompletableDeferred<Boolean>? = null

    actual suspend fun ensurePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        val context = LivestockWealthApplication.instance.applicationContext
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return true
        }

        val activity = CurrentActivityHolder.currentActivity ?: return false

        return mutex.withLock {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                return@withLock true
            }

            val deferred = CompletableDeferred<Boolean>()
            pendingRequest = deferred

            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE
            )

            deferred.await().also {
                pendingRequest = null
            }
        }
    }

    actual fun onPermissionResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode != REQUEST_CODE) return
        val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        pendingRequest?.complete(granted)
        pendingRequest = null
    }
}
