package com.example.scheduleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.logic.LocalThemeViewModel
import com.example.scheduleapp.logic.ThemeViewModel
import com.example.scheduleapp.screens.MainScreen
import com.example.scheduleapp.ui.theme.ScheduleAppTheme

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppState.initialize(this)
        themeViewModel.initializeTheme(this)

        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            LaunchedEffect(isDarkTheme) {
                println("DEBUG: MainActivity - isDarkTheme = $isDarkTheme")
            }

            ScheduleAppTheme(darkTheme = isDarkTheme) {
                CompositionLocalProvider(
                    LocalThemeViewModel provides themeViewModel
                ) {
                    MainScreen()
                }
            }
        }
    }
}