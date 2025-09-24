package com.lswmobile.app.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.UserNotifications.*
import platform.UIKit.*
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.posix.memcpy

actual object PlatformFileUtils {

    actual suspend fun savePdf(bytes: ByteArray, suggestedFileName: String): SavedFileResult {
        return try {
            val fileName = if (suggestedFileName.lowercase().endsWith(".pdf")) suggestedFileName else "$suggestedFileName.pdf"
            val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
            val documentsDir = (paths.firstOrNull() as? String) ?: NSTemporaryDirectory()
            val filePath = documentsDir + "/" + fileName
            val nsData = bytes.toNSData()
            val ok = nsData.writeToFile(filePath, true)
            if (ok) SavedFileResult(true, filePath, null) else SavedFileResult(false, null, "Failed to write file")
        } catch (t: Throwable) {
            SavedFileResult(false, null, t.message)
        }
    }

    actual suspend fun showDownloadCompletedNotification(title: String, message: String, filePath: String?) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        // Request permission silently (idempotent)
        center.requestAuthorizationWithOptions(UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge) { _, _ ->
        }
        val content = UNMutableNotificationContent()
        content.setTitle(title)
        content.setBody(message)
        if (filePath != null) {
            val info: Map<Any?, *> = mapOf<Any?, Any?>("filePath" to filePath)
            content.setUserInfo(info)
        }
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(0.5, false)
        val request = UNNotificationRequest.requestWithIdentifier(NSUUID().UUIDString, content, trigger)
        center.addNotificationRequest(request) { error ->
            // Ignore errors for now
        }
    }

    actual fun openPdf(filePath: String): Boolean {
        return try {
            val url = NSURL.fileURLWithPath(filePath)
            UIApplication.sharedApplication.openURL(url)
        } catch (t: Throwable) {
            false
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData {
    return this.usePinned {
        NSData.create(bytes = it.addressOf(0), length = this.size.toULong())
    }
}
