package com.example.scheduleapp

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.scheduleapp.bottom_navigation.BottomNav
import com.example.scheduleapp.bottom_navigation.NavGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Preview
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {BottomNav(navController)}
    ) {
        NavGraph(navController)

    }
}