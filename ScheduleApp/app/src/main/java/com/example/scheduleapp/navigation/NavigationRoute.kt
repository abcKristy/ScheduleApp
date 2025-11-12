package com.example.scheduleapp.navigation

sealed class NavigationRoute(val route: String){
    object Welcome: NavigationRoute("welcome")
    object Login: NavigationRoute("login")
    object Register: NavigationRoute("register")
    object Profile: NavigationRoute("profile")
    object ScheduleList: NavigationRoute("schedule_list")
    object Search: NavigationRoute("search")
    object UserDetail: NavigationRoute("user_detail")
    object ScheduleDetail: NavigationRoute("schedule_detail")

    companion object{
        fun fromRoute(route: String?): NavigationRoute{
            return when(route?.substringBefore("/")){
                "welcome" -> Welcome
                "login" -> Login
                "register" -> Register
                "profile" -> Profile
                "schedule_list" -> ScheduleList
                "search" -> Search
                "user_detail" -> UserDetail
                "schedule_detail" -> ScheduleDetail
                null -> Profile
                else -> throw IllegalArgumentException("Route $route is not recognized")
            }
        }
    }



}