package com.lswmobile.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lswmobile.app.platform.NotificationPermissionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize app dependencies
        AppInitializer.initialize()
        
        setContent {
            App() // Use the standard App composable that's shared with iOS
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        NotificationPermissionManager.onPermissionResult(requestCode, grantResults)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}