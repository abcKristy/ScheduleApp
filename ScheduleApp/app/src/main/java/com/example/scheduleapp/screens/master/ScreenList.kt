// ScreenList.kt (исправленная версия)
package com.example.scheduleapp.screens.master

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scheduleapp.data.AppState
import com.example.scheduleapp.data.DayItem
import com.example.scheduleapp.data.EmptySchedule
import com.example.scheduleapp.data.ScheduleDay
import com.example.scheduleapp.data.TestSchedule
import com.example.scheduleapp.items.BreakItemList
import com.example.scheduleapp.items.Calendar
import com.example.scheduleapp.items.EmptyScheduleItemCompact
import com.example.scheduleapp.items.ScheduleListItem
import com.example.scheduleapp.logic.createScheduleDayForDate
import com.example.scheduleapp.logic.getScheduleItems
import com.example.scheduleapp.logic.getScheduleItemsWithCache
import com.example.scheduleapp.navigation.NavigationRoute
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.customColors
import java.time.LocalDate

@Composable
fun ScreenList(navController: NavController? = null) {
    val scheduleItems = AppState.scheduleItems
    val isLoading = AppState.isLoading
    val errorMessage = AppState.errorMessage
    val currentGroup = AppState.currentGroup
    val selectedDate = AppState.selectedDate

    LaunchedEffect(currentGroup) {
        AppState.setLoading(true)
        AppState.setErrorMessage(null)

        getScheduleItemsWithCache(
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
        if (selectedDate != null) {
            createScheduleDayForDate(scheduleItems, selectedDate)
        } else {
            ScheduleDay(LocalDate.now())
        }
    }

    Box(modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.customColors.bg2)) {
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
                if (filteredSchedule.hasLessons) {
                    SwipeableScheduleList(
                        filteredSchedule = filteredSchedule,
                        navController = navController,
                        onSwipeLeft = {
                            // Свайп влево - следующий день от выбранной даты
                            val currentDate = AppState.selectedDate
                            if (currentDate != null) {
                                val newDate = currentDate.plusDays(1)
                                AppState.setSelectedDate(newDate)
                            }
                        },
                        onSwipeRight = {
                            // Свайп вправо - предыдущий день от выбранной даты
                            val currentDate = AppState.selectedDate
                            if (currentDate != null) {
                                val newDate = currentDate.minusDays(1)
                                AppState.setSelectedDate(newDate)
                            }
                        }
                    )
                } else {
                    SwipeableEmptyState(
                        selectedDate = selectedDate,
                        scheduleItems = scheduleItems,
                        onSwipeLeft = {
                            val currentDate = AppState.selectedDate
                            if (currentDate != null) {
                                val newDate = currentDate.plusDays(1)
                                AppState.setSelectedDate(newDate)
                            }
                        },
                        onSwipeRight = {
                            val currentDate = AppState.selectedDate
                            if (currentDate != null) {
                                val newDate = currentDate.minusDays(1)
                                AppState.setSelectedDate(newDate)
                            }
                        }
                    )
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

@Composable
fun SwipeableScheduleList(
    filteredSchedule: ScheduleDay,
    navController: NavController?,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    var swipeHandled by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        swipeHandled = false
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (!swipeHandled) {
                            when {
                                dragAmount > 50 -> { // Порог свайпа вправо
                                    onSwipeRight()
                                    swipeHandled = true
                                }
                                dragAmount < -50 -> { // Порог свайпа влево
                                    onSwipeLeft()
                                    swipeHandled = true
                                }
                            }
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        swipeHandled = false
                    }
                )
            },
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        items(filteredSchedule.allItems) { dayItem ->
            when (dayItem) {
                is DayItem.Lesson -> {
                    if (EmptySchedule.isEmpty(dayItem.scheduleItem)) {
                        EmptyScheduleItemCompact(scheduleItem = dayItem.scheduleItem)
                    } else {
                        ScheduleListItem(
                            scheduleItem = dayItem.scheduleItem,
                            onItemClick = {
                                navController?.navigate(NavigationRoute.ScheduleDetail.route)
                            }
                        )
                    }
                }
                is DayItem.Break -> {
                    BreakItemList(breakItem = dayItem.breakItem)
                }
            }
        }
    }
}

@Composable
fun SwipeableEmptyState(
    selectedDate: LocalDate?,
    scheduleItems: List<com.example.scheduleapp.data.ScheduleItem>,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    var swipeHandled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        swipeHandled = false
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (!swipeHandled) {
                            when {
                                dragAmount > 50 -> {
                                    onSwipeRight()
                                    swipeHandled = true
                                }
                                dragAmount < -50 -> {
                                    onSwipeLeft()
                                    swipeHandled = true
                                }
                            }
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        swipeHandled = false
                    }
                )
            },
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