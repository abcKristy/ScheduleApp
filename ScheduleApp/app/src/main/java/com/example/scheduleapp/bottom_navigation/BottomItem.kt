package com.example.scheduleapp.bottom_navigation

import com.example.scheduleapp.navigation.NavigationRoute
import com.example.scheduleapp.R

sealed class BottomItem(val iconId:Int, val route:String) {
    object Search: BottomItem(R.drawable.ic_search, NavigationRoute.Search.route)
    object Profile: BottomItem(R.drawable.ic_profile,NavigationRoute.Profile.route)
    object ScheduleList: BottomItem(R.drawable.ic_list,NavigationRoute.ScheduleList.route)
    object Previous: BottomItem(R.drawable.ic_previous,NavigationRoute.Previous.route)

    companion object{
        val items = listOf(Search, Previous, ScheduleList , Profile)
    }
}
