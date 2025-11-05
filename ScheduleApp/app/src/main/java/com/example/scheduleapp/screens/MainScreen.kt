package com.example.scheduleapp.screens

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.scheduleapp.MainNavGraph
import com.example.scheduleapp.bottom_navigation.BottomNav
import com.example.scheduleapp.navigation.NavigationRoute

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var showBottomNav by remember { mutableStateOf(true) }
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            showBottomNav = backStackEntry.destination.route != NavigationRoute.Welcome.route
        }
    }
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNav(navController)
            }
        }
    ) {
        MainNavGraph(navController)
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}