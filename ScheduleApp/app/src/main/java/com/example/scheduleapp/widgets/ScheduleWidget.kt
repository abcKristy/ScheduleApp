package com.example.scheduleapp.widgets

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.scheduleapp.data.entity.ScheduleItem
import com.example.scheduleapp.ui.theme.darkGray
import com.example.scheduleapp.ui.theme.white
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.ceil

/**
 * Виджет расписания с виртуальным скроллом через клики
 */
@SuppressLint("RestrictedApi")
class ScheduleWidget : GlanceAppWidget() {

    private val widgetScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var updateKey: Long = System.currentTimeMillis()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        android.util.Log.d("ScheduleWidget", "=== PROVIDE GLANCE CALLED (key: $updateKey) ===")

        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    suspend fun forceUpdate(context: Context) {
        updateKey = System.currentTimeMillis()
        android.util.Log.d("ScheduleWidget", "Force update with new key: $updateKey")
        updateAll(context)
    }

    @Composable
    private fun WidgetContent() {
        val widgetData = rememberWidgetData()
        val scrollOffset = remember { mutableStateOf(0) }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(darkGray)
                .padding(12.dp),
            contentAlignment = Alignment.TopStart
        ) {
            when {
                widgetData.isLoading -> LoadingState()
                widgetData.error != null -> ErrorState(widgetData.error)
                widgetData.scheduleByDate.isEmpty() -> EmptyState(widgetData.currentGroup)
                else -> ScrollableScheduleContent(widgetData, scrollOffset.value)
            }
        }
    }

    @Composable
    private fun ScrollableScheduleContent(data: WidgetData, scrollOffset: Int) {
        android.util.Log.d("ScrollableScheduleContent", "Rendering with offset: $scrollOffset, total days: ${data.scheduleByDate.size}")

        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            // ЗАГОЛОВОК - всегда видимый
            WidgetHeader(
                startDate = data.startDate,
                endDate = data.endDate,
                currentGroup = data.currentGroup
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // ОСНОВНОЙ КОНТЕНТ с пагинацией
            val sortedDays = data.scheduleByDate.entries.sortedBy { it.key }
            val totalDays = sortedDays.size
            val daysToShow = getDaysToShow(sortedDays, scrollOffset, totalDays)

            android.util.Log.d("ScrollableScheduleContent", "Days to show: ${daysToShow.size}")

            DaysList(
                daysToShow = daysToShow,
                currentOffset = scrollOffset,
                totalDays = totalDays
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            // КНОПКИ ПРОКРУТКИ
            ScrollControls(
                currentOffset = scrollOffset,
                totalDays = totalDays,
                modifier = GlanceModifier.fillMaxWidth()
            )
        }
    }

    @Composable
    private fun DaysList(
        daysToShow: List<Map.Entry<LocalDate, List<ScheduleItem>>>,
        currentOffset: Int,
        totalDays: Int
    ) {
        android.util.Log.d("DaysList", "Rendering ${daysToShow.size} days, offset=$currentOffset, total=$totalDays")

        Column(
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            // Показываем индикатор если прокрутили вниз
            if (currentOffset > 0) {
                Text(
                    text = "↓ Показаны дни ${currentOffset + 1}-${currentOffset + daysToShow.size} из $totalDays",
                    style = TextStyle(
                        color = ColorProvider(white.copy(alpha = 0.6f)),
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = GlanceModifier.padding(bottom = 4.dp)
                )
            }

            if (daysToShow.isNotEmpty()) {
                daysToShow.forEach { (date, scheduleItems) ->
                    android.util.Log.d("DaysList", "Rendering day: $date with ${scheduleItems.size} items")
                    DaySection(date, scheduleItems)
                    Spacer(modifier = GlanceModifier.height(6.dp))
                }
            } else {
                // Отладочная информация
                Text(
                    text = "Нет данных для отображения (offset: $currentOffset)",
                    style = TextStyle(
                        color = ColorProvider(white.copy(alpha = 0.5f))
                    ),
                    modifier = GlanceModifier.padding(vertical = 8.dp)
                )
                android.util.Log.w("DaysList", "No days to show! Offset: $currentOffset, total: $totalDays")
            }
        }
    }

    @Composable
    private fun ScrollControls(currentOffset: Int, totalDays: Int, modifier: GlanceModifier = GlanceModifier) {
        val context = androidx.glance.LocalContext.current
        val canScrollUp = WidgetScrollManager.canScrollUp(context)
        val canScrollDown = WidgetScrollManager.canScrollDown(context, totalDays)

        android.util.Log.d("ScrollControls", "Controls: offset=$currentOffset, total=$totalDays, up=$canScrollUp, down=$canScrollDown")

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка "Вверх" - показываем только когда можно скроллить вверх
            Text(
                text = if (canScrollUp) "↑ Раньше" else " ",
                style = TextStyle(
                    color = if (canScrollUp) ColorProvider(white.copy(alpha = 0.8f)) else ColorProvider(white.copy(alpha = 0.0f)),
                    fontWeight = FontWeight.Medium
                ),
                modifier = GlanceModifier
                    .defaultWeight()
                    .run {
                        if (canScrollUp) {
                            this.then(GlanceModifier.clickable {
                                android.util.Log.d("ScrollControls", "Up clicked")
                                WidgetScrollManager.scrollUp(context)
                                // Запускаем в coroutine scope
                                widgetScope.launch {
                                    ScheduleWidget().updateAll(context)
                                }
                            })
                        } else {
                            this
                        }
                    }
            )

            // Индикатор позиции - показываем текущую страницу
            val currentPage = if (totalDays > 0) (currentOffset / 3) + 1 else 0
            val totalPages = if (totalDays > 0) ceil(totalDays.toDouble() / 3).toInt() else 0

            Text(
                text = "$currentPage/$totalPages",
                style = TextStyle(
                    color = ColorProvider(white.copy(alpha = 0.7f)),
                    fontWeight = FontWeight.Normal
                ),
                modifier = GlanceModifier.padding(horizontal = 8.dp)
            )

            // Кнопка "Вниз" - показываем только когда можно скроллить вниз
            Text(
                text = if (canScrollDown) "↓ Дальше" else " ",
                style = TextStyle(
                    color = if (canScrollDown) ColorProvider(white.copy(alpha = 0.8f)) else ColorProvider(white.copy(alpha = 0.0f)),
                    fontWeight = FontWeight.Medium
                ),
                modifier = GlanceModifier
                    .defaultWeight()
                    .run {
                        if (canScrollDown) {
                            this.then(GlanceModifier.clickable {
                                android.util.Log.d("ScrollControls", "Down clicked")
                                val widgetData = WidgetDataManager.getWidgetData(context)
                                WidgetScrollManager.scrollDown(context, widgetData.scheduleByDate.size)
                                // Запускаем в coroutine scope
                                widgetScope.launch {
                                    ScheduleWidget().updateAll(context)
                                }
                            })
                        } else {
                            this
                        }
                    }
            )
        }
    }

    private fun getDaysToShow(
        sortedDays: List<Map.Entry<LocalDate, List<ScheduleItem>>>,
        scrollOffset: Int,
        totalDays: Int
    ): List<Map.Entry<LocalDate, List<ScheduleItem>>> {
        val startIndex = scrollOffset
        val endIndex = minOf(startIndex + 3, totalDays) // Показываем по 3 дня за раз

        android.util.Log.d("getDaysToShow", "Range: $startIndex to $endIndex (total: $totalDays, offset: $scrollOffset)")

        return if (startIndex < totalDays && startIndex < endIndex) {
            val result = sortedDays.subList(startIndex, endIndex)
            android.util.Log.d("getDaysToShow", "Returning ${result.size} days")
            result
        } else {
            android.util.Log.w("getDaysToShow", "Invalid range: $startIndex to $endIndex")
            emptyList()
        }
    }

    @Composable
    private fun DaySection(date: LocalDate, scheduleItems: List<ScheduleItem>) {
        Column(
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            // Заголовок дня
            DayHeader(date = date)

            // Занятия на этот день
            if (scheduleItems.isNotEmpty()) {
                scheduleItems.sortedBy { it.startTime }.forEach { scheduleItem ->
                    ScheduleItemCompact(scheduleItem = scheduleItem)
                    Spacer(modifier = GlanceModifier.height(3.dp))
                }
            } else {
                Text(
                    text = "Нет занятий",
                    style = TextStyle(
                        color = ColorProvider(white.copy(alpha = 0.5f))
                    ),
                    modifier = GlanceModifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
    }

    @Composable
    private fun DayHeader(date: LocalDate) {
        val isToday = date == LocalDate.now()
        val backgroundColor = if (isToday) {
            white.copy(alpha = 0.3f)
        } else {
            white.copy(alpha = 0.15f)
        }

        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDayHeader(date),
                    style = TextStyle(
                        color = ColorProvider(white),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )

                if (isToday) {
                    Text(
                        text = "• сегодня",
                        style = TextStyle(
                            color = ColorProvider(white.copy(alpha = 0.8f)),
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
    }

    /**
     * Форматирует заголовок дня (Ср, 22 ноября)
     */
    private fun formatDayHeader(date: LocalDate): String {
        val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale("ru"))
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))

        val dayOfWeek = date.format(dayOfWeekFormatter)
        val dateString = date.format(dateFormatter)

        return "${dayOfWeek.replaceFirstChar { it.uppercase() }}, $dateString"
    }

    @Composable
    private fun WidgetHeader(startDate: LocalDate, endDate: LocalDate, currentGroup: String) {
        Column(
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            // Диапазон дат
            Text(
                text = formatDateRangeForWidget(startDate, endDate),
                style = TextStyle(
                    color = ColorProvider(white),
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            // Группа
            Text(
                text = currentGroup.ifEmpty { "Группа не выбрана" },
                style = TextStyle(
                    color = ColorProvider(white.copy(alpha = 0.8f)),
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }

    /**
     * Форматирует диапазон дат для заголовка
     */
    private fun formatDateRangeForWidget(startDate: LocalDate, endDate: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))
        val start = startDate.format(dateFormatter)
        val end = endDate.format(dateFormatter)
        return "$start - $end"
    }

    @Composable
    private fun ScheduleItemCompact(scheduleItem: ScheduleItem) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Время занятия
            Text(
                text = scheduleItem.formattedStartTime,
                style = TextStyle(
                    color = ColorProvider(white),
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.width(34.dp)
            )

            Spacer(modifier = GlanceModifier.width(6.dp))

            // Компактная информация о занятии
            Column(
                modifier = GlanceModifier.defaultWeight()
            ) {
                // Дисциплина (сокращенная)
                Text(
                    text = getVeryShortDisciplineName(scheduleItem.discipline),
                    style = TextStyle(
                        color = ColorProvider(white),
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 1
                )

                // Тип занятия и преподаватель
                Text(
                    text = "${formatLessonType(scheduleItem.lessonType)} • ${getShortTeacherName(scheduleItem.teacher)}",
                    style = TextStyle(
                        color = ColorProvider(white.copy(alpha = 0.7f)),
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 1
                )
            }

            // Аудитория
            if (scheduleItem.room.isNotBlank()) {
                Spacer(modifier = GlanceModifier.width(6.dp))
                Text(
                    text = getShortRoom(scheduleItem.room),
                    style = TextStyle(
                        color = ColorProvider(white.copy(alpha = 0.7f)),
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }

    /**
     * Сокращает название дисциплины
     */
    private fun getVeryShortDisciplineName(discipline: String): String {
        return when {
            discipline.length <= 18 -> discipline
            discipline.contains(" ") -> {
                val words = discipline.split(" ")
                if (words.size >= 2) {
                    words.take(2).joinToString(" ")
                } else {
                    discipline.take(17) + "…"
                }
            }
            else -> discipline.take(17) + "…"
        }
    }

    /**
     * Сокращает имя преподавателя
     */
    private fun getShortTeacherName(teacher: String): String {
        return if (teacher.length <= 12) {
            teacher
        } else {
            val parts = teacher.split(" ")
            if (parts.size >= 2) {
                val lastName = parts.last()
                val initials = parts.dropLast(1).joinToString(".") { it.first() + "." }
                "$initials $lastName"
            } else {
                teacher.take(11) + "…"
            }
        }
    }

    /**
     * Сокращает номер аудитории
     */
    private fun getShortRoom(room: String): String {
        return room.split(" ").first()
    }

    /**
     * Форматирует тип занятия для отображения
     */
    private fun formatLessonType(lessonType: String): String {
        return when (lessonType.uppercase()) {
            "LECTURE", "LK", "ЛЕКЦИЯ" -> "Лекция"
            "PRACTICE", "PR", "ПРАКТИКА" -> "Практика"
            "LAB", "LABORATORY", "ЛАБОРАТОРНАЯ" -> "Лаб."
            "SEMINAR", "SEM", "СЕМИНАР" -> "Сем."
            "EMPTY" -> "Окно"
            else -> lessonType.take(6)
        }
    }

    @Composable
    private fun LoadingState() {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Загрузка...",
                style = TextStyle(
                    color = ColorProvider(white)
                )
            )
        }
    }

    @Composable
    private fun ErrorState(error: String) {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ошибка",
                    style = TextStyle(
                        color = ColorProvider(white),
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                Text(
                    text = error.take(30) + if (error.length > 30) "..." else "",
                    style = TextStyle(
                        color = ColorProvider(white.copy(alpha = 0.7f))
                    )
                )
            }
        }
    }

    @Composable
    private fun EmptyState(currentGroup: String) {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (currentGroup.isNotEmpty() && currentGroup != " ") "Нет пар на 14 дней" else "Выберите группу",
                    style = TextStyle(
                        color = ColorProvider(white),
                        fontWeight = FontWeight.Medium
                    )
                )

                if (currentGroup.isEmpty() || currentGroup == " ") {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "в настройках приложения",
                        style = TextStyle(
                            color = ColorProvider(white.copy(alpha = 0.6f))
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberWidgetData(): WidgetData {
    val context = androidx.glance.LocalContext.current
    var widgetData by remember { mutableStateOf(WidgetData(
        currentGroup = "",
        startDate = LocalDate.now(),
        endDate = LocalDate.now().plusDays(13),
        scheduleByDate = emptyMap(),
        isLoading = true,
        error = null,
        scrollOffset = 0
    )) }

    LaunchedEffect(Unit) {
        val data = withContext(Dispatchers.IO) {
            WidgetDataManager.getWidgetData(context)
        }

        // Получаем АКТУАЛЬНОЕ состояние скролла каждый раз
        val scrollOffset = WidgetScrollManager.getCurrentOffset(context)

        widgetData = data.copy(scrollOffset = scrollOffset)

        android.util.Log.d("ScheduleWidget",
            "Widget data loaded: ${data.scheduleByDate.size} days, scroll: $scrollOffset")

        // Логируем детали по дням
        data.scheduleByDate.forEach { (date, items) ->
            android.util.Log.d("ScheduleWidget", "Day $date: ${items.size} items")
        }

        // Логируем какие дни будем показывать
        val sortedDays = data.scheduleByDate.entries.sortedBy { it.key }
        val daysToShow = getDaysToShow(sortedDays, scrollOffset, data.scheduleByDate.size)
        android.util.Log.d("ScheduleWidget", "Will show ${daysToShow.size} days: ${daysToShow.map { it.key }}")
    }

    return widgetData
}

private fun getDaysToShow(
    sortedDays: List<Map.Entry<LocalDate, List<ScheduleItem>>>,
    scrollOffset: Int,
    totalDays: Int
): List<Map.Entry<LocalDate, List<ScheduleItem>>> {
    val startIndex = scrollOffset
    val endIndex = minOf(startIndex + 3, totalDays) // Показываем по 3 дня за раз

    android.util.Log.d("getDaysToShow", "Range: $startIndex to $endIndex (total: $totalDays, offset: $scrollOffset)")

    return if (startIndex < totalDays && startIndex < endIndex) {
        val result = sortedDays.subList(startIndex, endIndex)
        android.util.Log.d("getDaysToShow", "Returning ${result.size} days: ${result.map { it.key }}")
        result
    } else {
        android.util.Log.w("getDaysToShow", "Invalid range: $startIndex to $endIndex")
        emptyList()
    }
}