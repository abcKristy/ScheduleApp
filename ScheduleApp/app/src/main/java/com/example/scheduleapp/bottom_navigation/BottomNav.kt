package com.example.scheduleapp.bottom_navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.scheduleapp.R

@Composable
fun BottomNav(
    navController: NavController
) {
    NavigationBar(
        containerColor = colorResource(id = R.color.darkBlue),
        modifier = Modifier.height(115.dp)
    ) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        BottomItem.items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    // Навигация через переданный navController
                    navController.navigate(item.route) {
                        // Очищаем back stack до текущего элемента
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Восстанавливаем состояние при повторном нажатии
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.iconId),
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.darkBlue),
                    selectedTextColor = colorResource(id = R.color.lightGreen),
                    unselectedIconColor = colorResource(id = R.color.lightGreen),
                    unselectedTextColor = colorResource(id = R.color.darkBlue),
                    indicatorColor = colorResource(id = R.color.lightGreen)
                )
            )
        }
    }
}