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

/**
 * Упрощенный виджет расписания - только дата и список пар
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
        // Получаем данные для отображения
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
                widgetData.scheduleItems.isEmpty() -> EmptyState(widgetData.currentGroup)
                else -> ScheduleContent(widgetData)
            }
        }
    }

    @Composable
    private fun ScheduleContent(data: WidgetData) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            // Заголовок с датой
            WidgetHeader(
                currentDate = data.currentDate,
                currentGroup = data.currentGroup
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Список всех пар (система сама обеспечит скролл если не помещается)
            Column(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                data.scheduleItems.forEach { scheduleItem ->
                    ScheduleItemSimple(scheduleItem = scheduleItem)
                    Spacer(modifier = GlanceModifier.height(6.dp))
                }
            }
        }
    }

    @Composable
    private fun WidgetHeader(currentDate: LocalDate, currentGroup: String) {
        Column(
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            // Форматированная дата (21 ноября - пн)
            Text(
                text = WidgetDataManager.formatDateForWidget(currentDate),
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

    @Composable
    private fun ScheduleItemSimple(scheduleItem: ScheduleItem) {
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(white.copy(alpha = 0.1f))
                .padding(8.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Время занятия (9:00-10:30)
                Text(
                    text = WidgetDataManager.formatLessonTime(scheduleItem),
                    style = TextStyle(
                        color = ColorProvider(white),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.width(70.dp)
                )

                Spacer(modifier = GlanceModifier.width(8.dp))

                // Детали занятия
                Column(
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    // Тип и название занятия (Лекция: Реляционные БД)
                    Text(
                        text = buildLessonTitle(scheduleItem),
                        style = TextStyle(
                            color = ColorProvider(white),
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 2
                    )

                    // Аудитория, если есть
                    if (scheduleItem.room.isNotBlank()) {
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        Text(
                            text = scheduleItem.room,
                            style = TextStyle(
                                color = ColorProvider(white.copy(alpha = 0.7f))
                            )
                        )
                    }

                    // Преподаватель, если есть
                    if (scheduleItem.teacher.isNotBlank()) {
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        Text(
                            text = scheduleItem.teacher,
                            style = TextStyle(
                                color = ColorProvider(white.copy(alpha = 0.7f))
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }

    /**
     * Собирает заголовок занятия в формате "Тип: Название"
     */
    private fun buildLessonTitle(scheduleItem: ScheduleItem): String {
        val lessonType = WidgetDataManager.formatLessonType(scheduleItem.lessonType)
        val discipline = scheduleItem.discipline

        return if (discipline.isNotBlank()) {
            if (lessonType == "Окно") {
                "Окно"
            } else {
                "$lessonType: ${getShortDisciplineName(discipline)}"
            }
        } else {
            lessonType
        }
    }

    /**
     * Сокращает название дисциплины для компактного отображения
     */
    private fun getShortDisciplineName(discipline: String): String {
        return when {
            discipline.length <= 25 -> discipline
            discipline.contains(" ") -> {
                val words = discipline.split(" ")
                if (words.size >= 2) {
                    // Берем первое слово и первые буквы остальных
                    words.take(3).joinToString(" ") { word ->
                        if (word == words.first()) word else "${word.take(1)}."
                    }
                } else {
                    discipline.take(24) + "…"
                }
            }
            else -> discipline.take(24) + "…"
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
                    text = if (currentGroup.isNotEmpty() && currentGroup != " ") "Нет пар" else "Выберите группу",
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
        currentDate = LocalDate.now(),
        scheduleItems = emptyList(),
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