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
import com.example.scheduleapp.data.ThemeViewModel
import com.example.scheduleapp.navigation.MainNavGraph
import com.example.scheduleapp.navigation.bottom_navigation.BottomNav
import com.example.scheduleapp.navigation.NavigationRoute

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(themeViewModel: ThemeViewModel? = null) {
    val navController = rememberNavController()
    var showBottomNav by remember { mutableStateOf(true) }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val currentRoute = backStackEntry.destination.route
            showBottomNav = when (currentRoute) {
                NavigationRoute.ScheduleList.route -> true
                NavigationRoute.Profile.route -> true
                NavigationRoute.Search.route -> true
                else -> false
            }
        }
    }
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNav(navController)
            }
        }
    ) {
        MainNavGraph(navController, themeViewModel = themeViewModel)
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}