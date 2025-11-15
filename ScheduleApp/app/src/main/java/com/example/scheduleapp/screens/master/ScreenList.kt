// ScreenList.kt (обновленная версия)
package com.example.scheduleapp.screens.master

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.data.AppState
import com.example.scheduleapp.data.TestSchedule
import com.example.scheduleapp.items.Calendar
import com.example.scheduleapp.items.ScheduleListItem
import com.example.scheduleapp.logic.filterScheduleByDate
import com.example.scheduleapp.logic.getScheduleItems
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.gray

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
                                onItemClick = {
                                    // Обработка нажатия на кнопку меню
                                    // Можно показать детали о повторении и исключениях
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
                            text = when {
                                selectedDate != null && scheduleItems.isNotEmpty() -> "На выбранную дату занятий нет!"
                                selectedDate != null -> "Нет данных о занятиях"
                                else -> "Выберите дату"
                            },
                            color = Color.Gray
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = "Используются тестовые данные: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Light Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ScreenListLightPreview() {
    ScheduleAppTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ScreenList()
        }
    }
}

@Preview(
    name = "Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ScreenListDarkPreview() {
    ScheduleAppTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ScreenList()
        }
    }
}