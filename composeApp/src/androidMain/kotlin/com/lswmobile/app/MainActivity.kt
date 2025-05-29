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

        // Remove Koin initialization - it's already done in the Application class
        
        setContent {
            AppWithKoin() // Using Koin for dependency injection
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}