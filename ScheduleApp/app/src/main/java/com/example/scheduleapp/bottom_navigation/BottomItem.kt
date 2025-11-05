package com.example.scheduleapp.bottom_navigation

import com.example.scheduleapp.R

sealed class BottomItem(val title:String, val icanId:Int, val route:String) {
    object Screen1: BottomItem("Search", R.drawable.ic_search,"search")
    object Screen2: BottomItem("Profile", R.drawable.ic_profile,"profile")
    object Screen3: BottomItem("Screen 3", R.drawable.ic_search,"screen_3")
    object Screen4: BottomItem("Screen 4", R.drawable.ic_search,"screen_4")
}