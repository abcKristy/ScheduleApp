package com.example.scheduleapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.scheduleapp.data.ThemeViewModel
import com.example.scheduleapp.navigation.NavigationRoute
import com.example.scheduleapp.screens.profile.LoginScreen
import com.example.scheduleapp.screens.profile.RegisterScreen
import com.example.scheduleapp.screens.detail.ScheduleDetailScreen
import com.example.scheduleapp.screens.detail.UserSettingsScreen
import com.example.scheduleapp.screens.master.ScreenList
import com.example.scheduleapp.screens.master.ScreenProfile
import com.example.scheduleapp.screens.master.ScreenSearch
import com.example.scheduleapp.screens.master.WelcomeScreen

@Composable
fun MainNavGraph(navController: NavHostController, themeViewModel: ThemeViewModel?) {
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
            ScreenProfile(navController = navController, themeViewModel = themeViewModel)
        }

        composable(NavigationRoute.ScheduleList.route) {
            ScreenList(navController = navController)
        }

        composable(NavigationRoute.Search.route) {
            ScreenSearch(themeViewModel = themeViewModel)
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