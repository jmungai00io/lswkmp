package com.lswmobile.app.platform

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.lswmobile.app.BuildConfig
import com.lswmobile.app.LivestockWealthApplication
import java.io.File

actual object PlatformFileUtils {

    private const val CHANNEL_ID = "downloads_channel"

    private fun context(): Context = LivestockWealthApplication.instance.applicationContext

    actual suspend fun savePdf(bytes: ByteArray, suggestedFileName: String): SavedFileResult {
        return try {
            val fileName = if (suggestedFileName.endsWith(".pdf", true)) suggestedFileName else "$suggestedFileName.pdf"
            // Try saving into public Downloads (no permission required on Android 10+ with MediaStore)
            val resolver = context().contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
            }
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Files.getContentUri("external")
            }

            val itemUri: Uri? = resolver.insert(collection, contentValues)
            if (itemUri != null) {
                resolver.openOutputStream(itemUri)?.use { it.write(bytes) }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val cv2 = ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) }
                    resolver.update(itemUri, cv2, null, null)
                }
                // Return the content uri string
                return SavedFileResult(true, itemUri.toString(), null)
            }

            // Fallback: save into app external files and expose via FileProvider
            val dir = context().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context().cacheDir
            val outFile = File(dir, fileName)
            outFile.outputStream().use { it.write(bytes) }
            val uri = FileProvider.getUriForFile(
                context(),
                BuildConfig.APPLICATION_ID + ".fileprovider",
                outFile
            )
            SavedFileResult(true, uri.toString(), null)
        } catch (e: Exception) {
            SavedFileResult(false, null, e.message)
        }
    }

    actual suspend fun showDownloadCompletedNotification(title: String, message: String, filePath: String?) {
        // Create channel
        val nm = context().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent? = filePath?.let { path ->
            val uri = Uri.parse(path)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            PendingIntent.getActivity(
                context(),
                (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
            )
        }

        val builder = NotificationCompat.Builder(context(), CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .apply { if (pendingIntent != null) setContentIntent(pendingIntent) }

        NotificationManagerCompat.from(context()).notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), builder.build())
    }

    actual fun openPdf(filePath: String): Boolean {
        return try {
            val uri = Uri.parse(filePath)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context().startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}
