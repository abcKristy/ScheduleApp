// ScreenList.kt (обновленная версия)
package com.example.scheduleapp.screens.master

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.R
import com.example.scheduleapp.data.AppState
import com.example.scheduleapp.data.TestSchedule
import com.example.scheduleapp.items.Calendar
import com.example.scheduleapp.items.ScheduleListItem
import com.example.scheduleapp.logic.filterScheduleByDate
import com.example.scheduleapp.logic.getScheduleItems

@Composable
fun ScreenList() {
    val context = LocalContext.current
    val scheduleItems = AppState.scheduleItems
    val isLoading = AppState.isLoading
    val errorMessage = AppState.errorMessage
    val currentGroup = AppState.currentGroup
    val selectedDate = AppState.selectedDate

    LaunchedEffect(currentGroup) {
        AppState.setLoading(true)
        AppState.setErrorMessage(null)

        getScheduleItems(
            context = context,
            group = currentGroup,
            onSuccess = { items ->
                AppState.setScheduleItems(items)
                AppState.setLoading(false)
            },
            onError = { error ->
                AppState.setErrorMessage(error)
                AppState.setScheduleItems(TestSchedule())
                AppState.setLoading(false)
            }
        )
    }

    val filteredSchedule = remember(scheduleItems, selectedDate) {
        filterScheduleByDate(scheduleItems, selectedDate)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Spacer(modifier = Modifier.height(40.dp))
            Calendar()

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (filteredSchedule.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        items(filteredSchedule) { scheduleItem ->
                            ScheduleListItem(
                                scheduleItem = scheduleItem,
                                onOptionsClick = {
                                    // Обработка нажатия на кнопку меню
                                }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedDate != null) {
                                "Сегодня занятий нет!"
                            } else {
                                "Выберите дату"
                            },
                            color = Color.Gray
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = "Используются тестовые данные ИКБО-10-23: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
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
    ) {
        ScreenList()
    }
}