package com.example.scheduleapp.screens.master

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.R
import com.example.scheduleapp.items.Calendar
import com.example.scheduleapp.items.ScheduleListItem
import com.example.scheduleapp.items.getTestScheduleItems

@Composable
fun ScreenList() {
    val scheduleItems = remember { getTestScheduleItems() }

    Box {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Spacer(modifier = Modifier.height(40.dp))
            Calendar()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scheduleItems) { scheduleItem ->
                    ScheduleListItem(
                        scheduleItem = scheduleItem,
                        onOptionsClick = {
                            // Здесь будет переход на экран деталей
                        }
                    )
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@Preview
fun TestList() {
    Scaffold(
        containerColor = colorResource(id = R.color.gray)
    )
    {
        ScreenList()
    }
}