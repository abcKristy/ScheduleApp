package com.example.scheduleapp.screens.master.items

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.R
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import com.example.scheduleapp.ui.theme.customColors
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

enum class CalendarView {
    MONTH, WEEK
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendar(
    currentMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit
) {
    var localCurrentMonth by remember { mutableStateOf(currentMonth) }
    var swipeInProgress by remember { mutableStateOf(false) }
    var calendarView by remember { mutableStateOf(CalendarView.WEEK) }

    val customColors = MaterialTheme.customColors
    val selectedDate = AppState.selectedDate

    LaunchedEffect(currentMonth) {
        localCurrentMonth = currentMonth
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
        ) {
            selectedDate?.let { date ->
                Text(
                    text = AppState.currentGroup,
                    color = customColors.title,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Divider(modifier = Modifier.padding(vertical = 16.dp), color = customColors.title)
            }

            CalendarHeader(
                currentMonth = localCurrentMonth,
                selectedDate = selectedDate,
                calendarView = calendarView,
                onPrevious = {
                    when (calendarView) {
                        CalendarView.MONTH -> {
                            val newMonth = localCurrentMonth.minusMonths(1)
                            localCurrentMonth = newMonth
                            onMonthChange(newMonth)
                        }
                        CalendarView.WEEK -> {
                            AppState.setSelectedDate(selectedDate?.minusWeeks(1))
                            val newMonth = YearMonth.from(AppState.selectedDate ?: localCurrentMonth.atDay(1))
                            localCurrentMonth = newMonth
                            onMonthChange(newMonth)
                        }
                    }
                },
                onNext = {
                    when (calendarView) {
                        CalendarView.MONTH -> {
                            val newMonth = localCurrentMonth.plusMonths(1)
                            localCurrentMonth = newMonth
                            onMonthChange(newMonth)
                        }
                        CalendarView.WEEK -> {
                            AppState.setSelectedDate(selectedDate?.plusWeeks(1))
                            val newMonth = YearMonth.from(AppState.selectedDate ?: localCurrentMonth.atDay(1))
                            localCurrentMonth = newMonth
                            onMonthChange(newMonth)
                        }
                    }
                },
                onToday = {
                    val today = LocalDate.now()
                    localCurrentMonth = YearMonth.now()
                    onMonthChange(localCurrentMonth)
                    AppState.setSelectedDate(today)
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
                        currentMonth = localCurrentMonth,
                        onDateSelected = { date ->
                            AppState.setSelectedDate(date)
                            // Обновляем месяц при выборе даты
                            val newMonth = YearMonth.from(date)
                            localCurrentMonth = newMonth
                            onMonthChange(newMonth)
                        },
                        onSwipeLeft = {
                            if (!swipeInProgress) {
                                swipeInProgress = true
                                val newMonth = localCurrentMonth.plusMonths(1)
                                localCurrentMonth = newMonth
                                onMonthChange(newMonth)
                                swipeInProgress = false
                            }
                        },
                        onSwipeRight = {
                            if (!swipeInProgress) {
                                swipeInProgress = true
                                val newMonth = localCurrentMonth.minusMonths(1)
                                localCurrentMonth = newMonth
                                onMonthChange(newMonth)
                                swipeInProgress = false
                            }
                        }
                    )
                }
                CalendarView.WEEK -> {
                    SwipeableWeekView(
                        currentMonth = localCurrentMonth,
                        onDateSelected = { date ->
                            AppState.setSelectedDate(date)
                            // Обновляем месяц при выборе даты
                            val newMonth = YearMonth.from(date)
                            localCurrentMonth = newMonth
                            onMonthChange(newMonth)
                        },
                        onSwipeLeft = {
                            if (!swipeInProgress) {
                                swipeInProgress = true
                                AppState.setSelectedDate(AppState.selectedDate?.plusWeeks(1))
                                val newMonth = YearMonth.from(AppState.selectedDate ?: localCurrentMonth.atDay(1))
                                localCurrentMonth = newMonth
                                onMonthChange(newMonth)
                                swipeInProgress = false
                            }
                        },
                        onSwipeRight = {
                            if (!swipeInProgress) {
                                swipeInProgress = true
                                AppState.setSelectedDate(AppState.selectedDate?.minusWeeks(1))
                                val newMonth = YearMonth.from(AppState.selectedDate ?: localCurrentMonth.atDay(1))
                                localCurrentMonth = newMonth
                                onMonthChange(newMonth)
                                swipeInProgress = false
                            }
                        }
                    )
                }
            }
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
    val academicWeekNumber = getAcademicWeekNumber(selectedDate)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = onToday,
            modifier = Modifier.background(
                color = MaterialTheme.customColors.searchItem.copy(alpha = 0.45f),
                shape = CircleShape
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_cal),
                contentDescription = "Сегодня",
                tint = MaterialTheme.customColors.title,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Предыдущий",
                        tint = MaterialTheme.customColors.title
                    )
                }

                Text(
                    text = when (calendarView) {
                        CalendarView.MONTH -> currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ru")))
                        CalendarView.WEEK -> getWeekDisplayText(selectedDate ?: currentMonth.atDay(1), selectedDate)
                    },
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = MaterialTheme.customColors.title,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onNext) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Следующий",
                        tint = MaterialTheme.customColors.title
                    )
                }
            }

            Text(
                text = "$academicWeekNumber неделя",
                color = MaterialTheme.customColors.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(
            onClick = onViewToggle,
            modifier = Modifier.background(
                color = MaterialTheme.customColors.searchItem.copy(alpha = 0.45f),
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
                tint = MaterialTheme.customColors.title
            )
        }
    }
}

@Composable
fun WeekDaysHeader() {
    val weekDays = listOf("пн", "вт", "ср", "чт", "пт", "сб", "вс")

    Row(modifier = Modifier.fillMaxWidth()) {
        weekDays.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.customColors.title.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
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
    val startOfWeek = startDate.with(DayOfWeek.MONDAY)
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
        isSelected -> MaterialTheme.customColors.shiny.copy(0.5f)
        else -> Color.Transparent
    }

    val todayBorderColor = MaterialTheme.customColors.shiny
    val borderStyle = if (isToday && !isSelected) {
        Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 10f), 0f))
    } else {
        Fill
    }

    val textColor = MaterialTheme.customColors.title

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .drawBehind {
                if (isToday && !isSelected) {
                    drawCircle(
                        color = todayBorderColor,
                        style = borderStyle,
                        radius = size.minDimension / 2 - 1.dp.toPx()
                    )
                }
            }
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

private fun getAcademicWeekNumber(selectedDate: LocalDate?): Int {
    if (selectedDate == null) return 1

    val currentYear = selectedDate.year
    val autumnSemesterStart = LocalDate.of(currentYear, 9, 2) // 1 сентября
    val springSemesterStart = LocalDate.of(currentYear, 2, 11) // 10 февраля

    // Определяем, в каком семестре находится выбранная дата
    val semesterStart = when {
        // Если дата между 1 сентября и концом года - осенний семестр
        selectedDate.monthValue >= 9 -> autumnSemesterStart
        // Если дата между 10 февраля и 31 августа - весенний семестр
        selectedDate.isAfter(springSemesterStart) || selectedDate.isEqual(springSemesterStart) -> springSemesterStart
        // Если дата до 10 февраля - это осенний семестр предыдущего учебного года
        else -> LocalDate.of(currentYear - 1, 9, 1)
    }

    // Вычисляем разницу в неделях
    val weeksBetween = ChronoUnit.WEEKS.between(
        semesterStart,
        selectedDate
    )

    // Нумерация недель с 1
    var weekNumber = weeksBetween.toInt() + 1

    // Ограничиваем номер недели в зависимости от семестра
    weekNumber = when {
        // Осенний семестр (сентябрь-декабрь) - максимум 17 недель
        semesterStart.monthValue == 9 -> minOf(weekNumber, 17)
        // Весенний семестр (февраль-июнь) - максимум 20 недель
        else -> minOf(weekNumber, 20)
    }

    // Гарантируем, что номер недели не меньше 1
    return maxOf(1, weekNumber)
}
private fun getWeekDisplayText(startDate: LocalDate, selectedDate: LocalDate?): String {
    val startOfWeek = startDate.with(DayOfWeek.MONDAY)
    val endOfWeek = startOfWeek.plusDays(6)

    return if (startOfWeek.month == endOfWeek.month) {
        "${startOfWeek.dayOfMonth} - ${endOfWeek.dayOfMonth} ${startOfWeek.format(DateTimeFormatter.ofPattern("MMMM", Locale("ru")))}"
    } else {
        "${startOfWeek.dayOfMonth} ${startOfWeek.format(DateTimeFormatter.ofPattern("MMMM", Locale("ru")))} - " +
                "${endOfWeek.dayOfMonth} ${endOfWeek.format(DateTimeFormatter.ofPattern("MMMM", Locale("ru")))}"
    }
}



@Preview(
    name = "Light Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun CalendarPreviewL() {
    ScheduleAppTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Calendar(
                currentMonth = YearMonth.now(),
                onMonthChange = {}
            )
        }
    }
}

@Preview(
    name = "Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CalendarPreviewN() {
    ScheduleAppTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Calendar(
                currentMonth = YearMonth.now(),
                onMonthChange = {}
            )
        }
    }
}