// MainActivity.kt
package com.example.scheduleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.scheduleapp.data.AppState
import com.example.scheduleapp.screens.MainScreen
import com.example.scheduleapp.ui.theme.ScheduleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppState.initialize(this)

        setContent {
            ScheduleAppTheme {
                MainScreen()
            }
        }
    }
}