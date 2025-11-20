package com.example.scheduleapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.scheduleapp.screens.profile.LoginScreen
import com.example.scheduleapp.screens.profile.RegisterScreen
import com.example.scheduleapp.screens.master.detail.ScheduleDetailScreen
import com.example.scheduleapp.screens.master.detail.UserSettingsScreen
import com.example.scheduleapp.screens.master.ScreenList
import com.example.scheduleapp.screens.master.ScreenProfile
import com.example.scheduleapp.screens.master.ScreenSearch
import com.example.scheduleapp.screens.master.WelcomeScreen

@Composable
fun MainNavGraph(navController: NavHostController) {
    var startDestination by remember {
        mutableStateOf(NavigationRoute.Welcome.route)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationRoute.Welcome.route) {
            WelcomeScreen(
                onNavigateToMain = {
                    navController.navigate(NavigationRoute.Profile.route) {
                        popUpTo(NavigationRoute.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavigationRoute.Profile.route) {
            ScreenProfile(navController = navController)
        }

        composable(NavigationRoute.ScheduleList.route) {
            ScreenList(navController = navController)
        }

        composable(NavigationRoute.Search.route) {
            ScreenSearch()
        }

        ////////////////////////////////////////////////

        composable(NavigationRoute.UserSettings.route) {
            UserSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavigationRoute.ScheduleDetail.route) {
            ScheduleDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavigationRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavigationRoute.Profile.route) {
                        popUpTo(NavigationRoute.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavigationRoute.Register.route)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavigationRoute.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavigationRoute.Profile.route) {
                        popUpTo(NavigationRoute.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}