package com.example.scheduleapp.bottom_navigation

import com.example.scheduleapp.navigation.NavigationRoute
import com.example.scheduleapp.R

sealed class BottomItem(val title:String, val iconId:Int, val route:String) {
    object Search: BottomItem("Search", R.drawable.ic_search, NavigationRoute.Search.route)
    object Profile: BottomItem("Profile", R.drawable.ic_profile,NavigationRoute.Profile.route)
    object ScheduleList: BottomItem("ScheduleList", R.drawable.ic_list,NavigationRoute.ScheduleList.route)
    object Previous: BottomItem("Previous", R.drawable.ic_previous,NavigationRoute.Previous.route)

    companion object{
        val items = listOf(Search, Previous, ScheduleList , Profile)
    }
}
