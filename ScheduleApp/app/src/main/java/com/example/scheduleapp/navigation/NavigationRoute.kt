package com.example.scheduleapp.navigation

sealed class NavigationRoute(val route: String){
    object Welcome: NavigationRoute("welcome")
    object Login: NavigationRoute("login")
    object Register: NavigationRoute("register")
    object Profile: NavigationRoute("profile")
    object ScheduleList: NavigationRoute("schedule_list")
    object Search: NavigationRoute("search")
    object ScheduleDetail: NavigationRoute("schedule_detail")
    object UserSettings: NavigationRoute("user_settings")

    companion object{
        fun fromRoute(route: String?): NavigationRoute{
            return when(route?.substringBefore("/")){
                "welcome" -> Welcome
                "login" -> Login
                "register" -> Register
                "profile" -> Profile
                "schedule_list" -> ScheduleList
                "search" -> Search
                "schedule_detail" -> ScheduleDetail
                "user_settings" -> UserSettings
                null -> Profile
                else -> throw IllegalArgumentException("Route $route is not recognized")
            }
        }
    }
}