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
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
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
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Виджет расписания на 14 дней вперед
 * Поддерживает вертикальный скролл средствами системы
 */

@SuppressLint("RestrictedApi")
class ScheduleWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val widgetData = rememberWidgetData()

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
                else -> ScheduleContent(widgetData)
            }
        }
    }

    @Composable
    private fun ScheduleContent(data: WidgetData) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            // Заголовок с диапазоном дат
            WidgetHeader(
                startDate = data.startDate,
                endDate = data.endDate,
                currentGroup = data.currentGroup
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Список дней с занятиями
            Column(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                data.scheduleByDate.forEach { (date, scheduleItems) ->
                    // Заголовок дня (Ср, 22 ноября)
                    DayHeader(date = date)

                    // Занятия на этот день
                    scheduleItems.sortedBy { it.startTime }.forEach { scheduleItem ->
                        ScheduleItemSimple(scheduleItem = scheduleItem)
                        Spacer(modifier = GlanceModifier.height(4.dp))
                    }

                    Spacer(modifier = GlanceModifier.height(8.dp))
                }
            }
        }
    }

    @Composable
    private fun DayHeader(date: LocalDate) {
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(white.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = formatDayHeader(date),
                style = TextStyle(
                    color = ColorProvider(white),
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(modifier = GlanceModifier.height(4.dp))
    }

    /**
     * Форматирует заголовок дня (Ср, 22 ноября)
     */
    private fun formatDayHeader(date: LocalDate): String {
        val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale("ru"))
        val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))

        val dayOfWeek = date.format(dayOfWeekFormatter)
        val dateString = date.format(dateFormatter)

        return "${dayOfWeek.lowercase()}, $dateString"
    }

    @Composable
    private fun WidgetHeader(startDate: LocalDate, endDate: LocalDate, currentGroup: String) {
        Column(
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            // Диапазон дат (21 ноя - 4 дек)
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
    private fun ScheduleItemSimple(scheduleItem: ScheduleItem) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Время занятия (9:00)
            Text(
                text = scheduleItem.formattedStartTime,
                style = TextStyle(
                    color = ColorProvider(white),
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.width(40.dp)
            )

            Spacer(modifier = GlanceModifier.width(8.dp))

            // Короткое название и тип
            Text(
                text = buildCompactLessonTitle(scheduleItem),
                style = TextStyle(
                    color = ColorProvider(white),
                    fontWeight = FontWeight.Normal
                ),
                modifier = GlanceModifier.defaultWeight(),
                maxLines = 1
            )

            // Аудитория (если есть)
            if (scheduleItem.room.isNotBlank()) {
                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = scheduleItem.room.split(" ").first(), // Только номер
                    style = TextStyle(
                        color = ColorProvider(white.copy(alpha = 0.7f))
                    )
                )
            }
        }
    }

    /**
     * Собирает компактный заголовок занятия
     */
    private fun buildCompactLessonTitle(scheduleItem: ScheduleItem): String {
        val lessonType = formatLessonType(scheduleItem.lessonType)
        val discipline = getVeryShortDisciplineName(scheduleItem.discipline)

        return if (lessonType == "Окно") {
            "Окно"
        } else {
            "$lessonType: $discipline"
        }
    }

    /**
     * Форматирует тип занятия для отображения
     */
    private fun formatLessonType(lessonType: String): String {
        return when (lessonType.uppercase()) {
            "LECTURE", "LK", "ЛЕКЦИЯ" -> "Лекция"
            "PRACTICE", "PR", "ПРАКТИКА" -> "Практика"
            "LAB", "LABORATORY", "ЛАБОРАТОРНАЯ" -> "Лабораторная"
            "SEMINAR", "SEM", "СЕМИНАР" -> "Семинар"
            "EMPTY" -> "Окно"
            else -> lessonType
        }
    }

    /**
     * Сокращает название дисциплины до минимума
     */
    private fun getVeryShortDisciplineName(discipline: String): String {
        return when {
            discipline.length <= 15 -> discipline
            discipline.contains(" ") -> {
                val words = discipline.split(" ")
                if (words.size >= 2) {
                    // Берем только первые 2 слова
                    words.take(2).joinToString(" ")
                } else {
                    discipline.take(14) + "…"
                }
            }
            else -> discipline.take(14) + "…"
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
                    text = error.take(40) + if (error.length > 40) "..." else "",
                    style = TextStyle(
                        color = ColorProvider(white.copy(alpha = 0.7f))
                    ),
                    maxLines = 2
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

/**
 * Composable функция для получения данных виджета
 */
@Composable
private fun rememberWidgetData(): WidgetData {
    val context = androidx.glance.LocalContext.current
    var widgetData by remember { mutableStateOf(WidgetData(
        currentGroup = "",
        startDate = LocalDate.now(),
        endDate = LocalDate.now().plusDays(13),
        scheduleByDate = emptyMap(),
        isLoading = true,
        error = null
    )) }

    LaunchedEffect(Unit) {
        widgetData = withContext(Dispatchers.IO) {
            WidgetDataManager.getWidgetData(context)
        }
    }

    return widgetData
}