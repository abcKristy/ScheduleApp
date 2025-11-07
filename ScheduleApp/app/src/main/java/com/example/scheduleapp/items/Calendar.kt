package com.example.scheduleapp.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.R
import com.example.scheduleapp.data.AppState
import com.example.scheduleapp.ui.theme.darkBlue
import com.example.scheduleapp.ui.theme.deepGreen
import com.example.scheduleapp.ui.theme.gray
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class CalendarView {
    MONTH, WEEK
}

fun getSelectedDayValue(): LocalDate? {
    return AppState.selectedDate
}

fun setSelectedDayValue(date: LocalDate?) {
    AppState.setSelectedDate(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendar() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var swipeInProgress by remember { mutableStateOf(false) }
    var calendarView by remember { mutableStateOf(CalendarView.MONTH) }

    val selectedDate = AppState.selectedDate

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(gray)
                .padding(4.dp)
        ) {
            selectedDate?.let { date ->
                Text(
                    text = "${date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ru")))}",
                    color = deepGreen,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Divider(modifier = Modifier.padding(vertical = 16.dp), color = deepGreen)
            }

            CalendarHeader(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                calendarView = calendarView,
                onPrevious = {
                    when (calendarView) {
                        CalendarView.MONTH -> currentMonth = currentMonth.minusMonths(1)
                        CalendarView.WEEK -> {
                            AppState.setSelectedDate(selectedDate?.minusWeeks(1))
                            currentMonth = YearMonth.from(AppState.selectedDate ?: currentMonth.atDay(1))
                        }
                    }
                },
                onNext = {
                    when (calendarView) {
                        CalendarView.MONTH -> currentMonth = currentMonth.plusMonths(1)
                        CalendarView.WEEK -> {
                            AppState.setSelectedDate(selectedDate?.plusWeeks(1))
                            currentMonth = YearMonth.from(AppState.selectedDate ?: currentMonth.atDay(1))
                        }
                    }
                },
                onToday = {
                    currentMonth = YearMonth.now()
                    AppState.setSelectedDate(LocalDate.now())
                },
                onViewToggle = {
                    calendarView = when (calendarView) {
                        CalendarView.MONTH -> CalendarView.WEEK
                        CalendarView.WEEK -> CalendarView.MONTH
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            WeekDaysHeader()

            Spacer(modifier = Modifier.height(8.dp))

            when (calendarView) {
                CalendarView.MONTH -> {
                    SwipeableCalendarGrid(
                        currentMonth = currentMonth,
                        onDateSelected = { date ->
                            AppState.setSelectedDate(date)
                        },
                        onSwipeLeft = {
                            if (!swipeInProgress) {
                                swipeInProgress = true
                                currentMonth = currentMonth.plusMonths(1)
                                swipeInProgress = false
                            }
                        },
                        onSwipeRight = {
                            if (!swipeInProgress) {
                                swipeInProgress = true
                                currentMonth = currentMonth.minusMonths(1)
                                swipeInProgress = false
                            }
                        }
                    )
                }
                CalendarView.WEEK -> {
                    SwipeableWeekView(
                        currentMonth = currentMonth,
                        onDateSelected = { date ->
                            AppState.setSelectedDate(date)
                        },
                        onSwipeLeft = {
                            if (!swipeInProgress) {
                                swipeInProgress = true
                                AppState.setSelectedDate(AppState.selectedDate?.plusWeeks(1))
                                currentMonth = YearMonth.from(AppState.selectedDate ?: currentMonth.atDay(1))
                                swipeInProgress = false
                            }
                        },
                        onSwipeRight = {
                            if (!swipeInProgress) {
                                swipeInProgress = true
                                AppState.setSelectedDate(AppState.selectedDate?.minusWeeks(1))
                                currentMonth = YearMonth.from(AppState.selectedDate ?: currentMonth.atDay(1))
                                swipeInProgress = false
                            }
                        }
                    )
                }
            }
            Divider(modifier = Modifier.padding(vertical = 1.dp), color = deepGreen)
        }
    }
}

@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    selectedDate: LocalDate?,
    calendarView: CalendarView,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onViewToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            onClick = onToday,
            border = BorderStroke(
                width = 1.dp,
                color = deepGreen
            ),
            shape = CircleShape,
            modifier = Modifier.size(50.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_cal),
                contentDescription = "Сегодня",
                tint = deepGreen,
                modifier = Modifier.size(24.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Предыдущий",
                    tint = deepGreen
                )
            }

            Text(
                text = when (calendarView) {
                    CalendarView.MONTH -> currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ru")))
                    CalendarView.WEEK -> getWeekDisplayText(selectedDate ?: currentMonth.atDay(1))
                },
                modifier = Modifier.padding(horizontal = 8.dp),
                color = deepGreen,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onNext) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Следующий",
                    tint = deepGreen
                )
            }
        }

        IconButton(
            onClick = onViewToggle,
            modifier = Modifier.background(
                color = deepGreen.copy(alpha = 0.1f),
                shape = CircleShape
            )
        ) {
            Icon(
                painter = painterResource(
                    id = when (calendarView) {
                        CalendarView.MONTH -> R.drawable.ic_up
                        CalendarView.WEEK -> R.drawable.ic_down
                    }
                ),
                contentDescription = when (calendarView) {
                    CalendarView.MONTH -> "Свернуть к неделе"
                    CalendarView.WEEK -> "Развернуть к месяцу"
                },
                tint = deepGreen
            )
        }
    }
}

private fun getWeekDisplayText(startDate: LocalDate): String {
    val startOfWeek = startDate.with(java.time.DayOfWeek.MONDAY)
    val endOfWeek = startOfWeek.plusDays(6)

    return if (startOfWeek.month == endOfWeek.month) {
        "${startOfWeek.dayOfMonth} - ${endOfWeek.dayOfMonth} ${startOfWeek.format(DateTimeFormatter.ofPattern("MMMM", Locale("ru")))}"
    } else {
        "${startOfWeek.dayOfMonth} ${startOfWeek.format(DateTimeFormatter.ofPattern("MMMM", Locale("ru")))} - " +
                "${endOfWeek.dayOfMonth} ${endOfWeek.format(DateTimeFormatter.ofPattern("MMMM", Locale("ru")))}"
    }
}

@Composable
fun WeekDaysHeader() {
    val weekDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    Row(modifier = Modifier.fillMaxWidth()) {
        weekDays.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodyMedium,
                color = deepGreen.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun SwipeableCalendarContainer(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    content: @Composable () -> Unit
) {
    var swipeHandled by remember { mutableStateOf(false) }

    Box(
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
                                dragAmount > 0 -> {
                                    onSwipeRight()
                                    swipeHandled = true
                                }

                                dragAmount < 0 -> {
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

@Composable
fun SwipeableCalendarGrid(
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    SwipeableCalendarContainer(
        onSwipeLeft = onSwipeLeft,
        onSwipeRight = onSwipeRight
    ) {
        CalendarGridContent(
            currentMonth = currentMonth,
            onDateSelected = onDateSelected
        )
    }
}

@Composable
fun SwipeableWeekView(
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    SwipeableCalendarContainer(
        onSwipeLeft = onSwipeLeft,
        onSwipeRight = onSwipeRight
    ) {
        WeekViewContent(
            currentMonth = currentMonth,
            onDateSelected = onDateSelected
        )
    }
}

@Composable
fun CalendarGridContent(
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit
) {
    val calendarDays = getCalendarDays(currentMonth)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(calendarDays.size) { index ->
            val calendarDay = calendarDays[index]
            CalendarDay(
                day = calendarDay.date.dayOfMonth,
                isSelected = AppState.selectedDate == calendarDay.date,
                isToday = calendarDay.date == LocalDate.now(),
                isOtherMonth = calendarDay.isOtherMonth,
                onClick = {
                    onDateSelected(calendarDay.date)
                }
            )
        }
    }
}

@Composable
fun WeekViewContent(
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit
) {
    val weekDays = getWeekDays(AppState.selectedDate ?: currentMonth.atDay(1))

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(weekDays.size) { index ->
            val date = weekDays[index]
            CalendarDay(
                day = date.dayOfMonth,
                isSelected = AppState.selectedDate == date,
                isToday = date == LocalDate.now(),
                isOtherMonth = date.month != currentMonth.month,
                onClick = {
                    onDateSelected(date)
                }
            )
        }
    }
}

private fun getWeekDays(startDate: LocalDate): List<LocalDate> {
    val startOfWeek = startDate.with(java.time.DayOfWeek.MONDAY)
    return (0..6).map { startOfWeek.plusDays(it.toLong()) }
}

data class CalendarDay(
    val date: LocalDate,
    val isOtherMonth: Boolean
)

fun getCalendarDays(currentMonth: YearMonth): List<CalendarDay> {
    val days = mutableListOf<CalendarDay>()

    // Первый день текущего месяца
    val firstDayOfMonth = currentMonth.atDay(1)
    // Последний день текущего месяца
    val lastDayOfMonth = currentMonth.atEndOfMonth()

    // Определяем день недели первого дня месяца (1 = понедельник, 7 = воскресенье)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
    val lastDayOfWeek = lastDayOfMonth.dayOfWeek.value

    // Добавляем дни предыдущего месяца
    val previousMonth = currentMonth.minusMonths(1)
    val daysInPreviousMonth = previousMonth.lengthOfMonth()

    // Количество дней предыдущего месяца, которые нужно показать
    val previousMonthDaysToShow = (firstDayOfWeek - 1) % 7
    for (i in previousMonthDaysToShow downTo 1) {
        val day = daysInPreviousMonth - i + 1
        val date = previousMonth.atDay(day)
        days.add(CalendarDay(date = date, isOtherMonth = true))
    }

    // Добавляем дни текущего месяца
    for (day in 1..currentMonth.lengthOfMonth()) {
        val date = currentMonth.atDay(day)
        days.add(CalendarDay(date = date, isOtherMonth = false))
    }

    // Добавляем дни следующего месяца
    val nextMonth = currentMonth.plusMonths(1)
    val nextMonthDaysToShow = (7 - lastDayOfWeek) % 7
    for (day in 1..nextMonthDaysToShow) {
        val date = nextMonth.atDay(day)
        days.add(CalendarDay(date = date, isOtherMonth = true))
    }

    return days
}

@Composable
fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    isOtherMonth: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> darkBlue
        isToday -> deepGreen
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> deepGreen
        isToday -> darkBlue
        isOtherMonth -> deepGreen.copy(alpha = 0.3f)
        else -> deepGreen
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = textColor,
            fontWeight = if (isOtherMonth) FontWeight.Normal else FontWeight.Bold,
            fontSize = if (isOtherMonth) 14.sp else 16.sp
        )
    }
}

@Preview
@Composable
fun CalendarPreview() {
    Calendar()
}