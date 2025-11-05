package com.example.scheduleapp.bottom_navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = "profile"){
        composable("profile") {
            ScreenProfile()
        }
        composable("list") {
            ScreenList()
        }
        composable("search") {
            ScreenSearch()
        }
        composable("previous") {
            ScreenPrevious()
        }
    }
}