package com.lswmobile.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

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
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}