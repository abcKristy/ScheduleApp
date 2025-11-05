package com.example.scheduleapp.bottom_navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenWithBottomNav(
    navController: NavHostController = rememberNavController(),
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNav(navController = navController)
        }
    ) { paddingValues ->
        content()
    }
}