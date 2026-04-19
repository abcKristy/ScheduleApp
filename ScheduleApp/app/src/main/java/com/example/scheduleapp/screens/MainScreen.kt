package com.example.scheduleapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.scheduleapp.navigation.MainNavGraph
import com.example.scheduleapp.navigation.bottom_navigation.BottomNav
import com.example.scheduleapp.navigation.NavigationRoute
import com.example.scheduleapp.screens.master.items.OfflineIndicator
import com.example.scheduleapp.ui.theme.customColors

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
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
        containerColor = MaterialTheme.customColors.bg2,
        bottomBar = {
            if (showBottomNav) {
                BottomNav(navController)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.customColors.bg2)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                OfflineIndicator()
                MainNavGraph(navController)
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}