package com.example.scheduleapp.bottom_navigation

import com.example.scheduleapp.R

sealed class BottomItem(val title:String, val iconId:Int, val route:String) {
    object Search: BottomItem("Search", R.drawable.ic_search,"search")
    object Profile: BottomItem("Profile", R.drawable.ic_profile,"profile")
    object List: BottomItem("List", R.drawable.ic_list,"list")
    object Previous: BottomItem("Previous", R.drawable.ic_previous,"previous")
}
