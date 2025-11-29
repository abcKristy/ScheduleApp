package com.example.scheduleapp.screens.master

import android.content.res.Configuration
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scheduleapp.R
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.data.entity.DayItem
import com.example.scheduleapp.data.entity.DynamicScheduleDay
import com.example.scheduleapp.data.entity.EmptySchedule
import com.example.scheduleapp.data.entity.TestSchedule
import com.example.scheduleapp.data.entity.ScheduleItem
import com.example.scheduleapp.screens.master.items.BreakItemList
import com.example.scheduleapp.screens.master.items.Calendar
import com.example.scheduleapp.screens.master.items.EmptyScheduleItemCompact
import com.example.scheduleapp.screens.master.items.ScheduleListItem
import com.example.scheduleapp.logic.createScheduleDayForDate
import com.example.scheduleapp.logic.getScheduleItemsWithCache
import com.example.scheduleapp.navigation.NavigationRoute
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.customColors
import com.google.gson.Gson
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun ScreenList(navController: NavController? = null) {
    val scheduleItems = AppState.scheduleItems
    val isLoading = AppState.isLoading
    val errorMessage = AppState.errorMessage
    val currentGroup = AppState.currentGroup
    val selectedDate = AppState.selectedDate
    val showEmptyLessons = AppState.showEmptyLessons

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    LaunchedEffect(selectedDate) {
        selectedDate?.let { date ->
            currentMonth = YearMonth.from(date)
        }
    }

    LaunchedEffect(currentGroup) {
        AppState.setLoading(true)
        AppState.setErrorMessage(null)

        getScheduleItemsWithCache(
            group = currentGroup,
            repository = AppState.repository,
            onSuccess = { items ->
                AppState.setScheduleItems(items)
                AppState.setLoading(false)
                AppState.setErrorMessage(null)
            },
            onError = { error ->
                AppState.setErrorMessage(error)
                AppState.setScheduleItems(TestSchedule())
                AppState.setScheduleItems(TestSchedule())
            }
        )
    }

    val isSunday = selectedDate?.dayOfWeek?.value == 7

    val filteredSchedule = remember(scheduleItems, selectedDate, showEmptyLessons) {
        if (selectedDate != null) {
            createScheduleDayForDate(scheduleItems, selectedDate, showEmptyLessons)
        } else {
            DynamicScheduleDay(LocalDate.now(), emptyList())
        }
    }

    val onSwipeLeft = {
        val currentDate = AppState.selectedDate
        if (currentDate != null) {
            val newDate = currentDate.plusDays(1)
            AppState.setSelectedDate(newDate)
            currentMonth = YearMonth.from(newDate)
        }
    }

    val onSwipeRight = {
        val currentDate = AppState.selectedDate
        if (currentDate != null) {
            val newDate = currentDate.minusDays(1)
            AppState.setSelectedDate(newDate)
            currentMonth = YearMonth.from(newDate)
        }
    }

    Box(modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.customColors.bg2)) {
        Column {
            Spacer(modifier = Modifier.height(40.dp))
            Calendar(
                currentMonth = currentMonth,
                onMonthChange = { newMonth ->
                    currentMonth = newMonth
                }
            )

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
                SwipeableScheduleContent(
                    onSwipeLeft = onSwipeLeft,
                    onSwipeRight = onSwipeRight
                ) {
                    when {
                        isSunday -> {
                            SundayContent()
                        }
                        filteredSchedule.hasLessons -> {
                            ScheduleListContent(
                                filteredSchedule = filteredSchedule,
                                navController = navController
                            )
                        }
                        else -> {
                            EmptyStateContent(
                                selectedDate = selectedDate,
                                scheduleItems = scheduleItems
                            )
                        }
                    }
                }

                if (errorMessage != null && scheduleItems == TestSchedule()) {
                    Text(
                        text = "Используются тестовые данные: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                } else if (errorMessage != null) {
                    Text(
                        text = "Информация: $errorMessage",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SundayContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_light_mode),
            contentDescription = "Воскресенье - выходной",
            modifier = Modifier.size(200.dp)
        )
    }
}

@Composable
private fun ScheduleListContent(
    filteredSchedule: DynamicScheduleDay,
    navController: NavController?
) {
    LazyColumn(
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
                                AppState.selectedScheduleItem = dayItem.scheduleItem
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
private fun EmptyStateContent(
    selectedDate: LocalDate?,
    scheduleItems: List<ScheduleItem>
) {
    Box(
        modifier = Modifier.fillMaxSize(),
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

@Composable
fun SwipeableScheduleContent(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    content: @Composable () -> Unit
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
            }
    ) {
        content()
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