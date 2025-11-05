package com.example.scheduleapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.scheduleapp.bottom_navigation.*
import com.example.scheduleapp.navigation.NavigationRoute
import com.example.scheduleapp.screens.profile.LoginScreen
import com.example.scheduleapp.screens.profile.RegisterScreen
import com.example.scheduleapp.screens.profile.UserDetailScreen
import com.example.scheduleapp.screens.MainScreen
import com.example.scheduleapp.screens.master.ScheduleDetailScreen
import com.example.scheduleapp.screens.master.WelcomeScreen

@Composable
fun MainNavGraph(navController: NavHostController) {
    var startDestination by remember {
        mutableStateOf(NavigationRoute.Welcome.route)
    }

    // Здесь можно добавить логику для проверки, авторизован ли пользователь
    // if (isUserLoggedIn) startDestination = NavigationRoute.Main.route

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
            ScreenProfile()
        }

        composable(NavigationRoute.ScheduleList.route) {
            ScreenList()
        }

        composable(NavigationRoute.Search.route) {
            ScreenSearch()
        }

        composable(NavigationRoute.Previous.route) {
            ScreenPrevious()
        }
        ////////////////////////////////////////////////

        // Detail screens
        composable(NavigationRoute.UserDetail.route) {
            UserDetailScreen(
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