package com.example.scheduleapp.widgets

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
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
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.ui.theme.blue
import com.example.scheduleapp.ui.theme.darkGray
import com.example.scheduleapp.ui.theme.deepGreen
import com.example.scheduleapp.ui.theme.lightGreen
import com.example.scheduleapp.ui.theme.pink40
import com.example.scheduleapp.ui.theme.white
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Основной класс виджета расписания
 * Отображает расписание на текущий день в компактном формате
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
                .padding(16.dp),
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
            // Заголовок с датой и группой
            WidgetHeader(
                currentDate = data.currentDate,
                currentGroup = data.currentGroup,
                itemsCount = data.scheduleItems.size
            )

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Список пар
            Column(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                data.scheduleItems.take(4).forEach { scheduleItem ->
                    ScheduleItemCompact(scheduleItem = scheduleItem)
                    Spacer(modifier = GlanceModifier.height(6.dp))
                }

                // Показать индикатор, если пар больше 4
                if (data.scheduleItems.size > 4) {
                    MoreItemsIndicator(count = data.scheduleItems.size - 4)
                }
            }
        }
    }

    @Composable
    private fun WidgetHeader(currentDate: LocalDate, currentGroup: String, itemsCount: Int) {
        Column(
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            // Дата
            Text(
                text = currentDate.format(
                    DateTimeFormatter.ofPattern("dd MMMM", Locale("ru"))
                ),
                style = TextStyle(
                    color = ColorProvider(white),
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Группа и количество пар
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentGroup.ifEmpty { "Не выбрана" },
                    style = TextStyle(
                        color = ColorProvider(white.copy(alpha = 0.8f)),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )

                if (itemsCount > 0) {
                    Text(
                        text = "$itemsCount пар",
                        style = TextStyle(
                            color = ColorProvider(white.copy(alpha = 0.6f))
                        )
                    )
                }
            }
        }
    }


    @Composable
    private fun ScheduleItemCompact(scheduleItem: ScheduleItem) {
        val dotColor = when (scheduleItem.lessonType.uppercase()) {
            "LK", "LECTURE", "ЛЕКЦИЯ" -> deepGreen
            "PR", "PRACTICE", "ПРАКТИКА" -> blue
            else -> pink40
        }

        val isCurrentLesson = scheduleItem.let { item ->
            val now = java.time.LocalTime.now()
            now.isAfter(item.startTime.toLocalTime()) && now.isBefore(item.endTime.toLocalTime())
        }

        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(
                    if (isCurrentLesson) lightGreen.copy(alpha = 0.2f)
                    else white.copy(alpha = 0.1f)
                )
                .padding(8.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Время
                Text(
                    text = scheduleItem.formattedStartTime,
                    style = TextStyle(
                        color = ColorProvider(white),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.width(35.dp)
                )

                Spacer(modifier = GlanceModifier.width(8.dp))

                Box(
                    modifier = GlanceModifier
                        .width(4.dp)
                        .height(4.dp)
                        .background(dotColor)
                ){}

                Spacer(modifier = GlanceModifier.width(8.dp))

                Text(
                    text = getShortDisciplineName(scheduleItem.discipline),
                    style = TextStyle(
                        color = ColorProvider(white),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = GlanceModifier.defaultWeight(),
                    maxLines = 1
                )

                // Аудитория
                if (scheduleItem.room.isNotBlank()) {
                    Text(
                        text = scheduleItem.room.split(" ").first(), // Берем только номер
                        style = TextStyle(
                            color = ColorProvider(white.copy(alpha = 0.7f))
                        )
                    )
                }
            }
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
                    text = if (currentGroup.isNotEmpty()) "Нет пар" else "Выберите группу",
                    style = TextStyle(
                        color = ColorProvider(white),
                        fontWeight = FontWeight.Medium
                    )
                )

                if (currentGroup.isEmpty()) {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "в настройках",
                        style = TextStyle(
                            color = ColorProvider(white.copy(alpha = 0.6f))
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun MoreItemsIndicator(count: Int) {
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Text(
                text = "+$count ещё",
                style = TextStyle(
                    color = ColorProvider(white.copy(alpha = 0.5f))
                )
            )
        }
    }

    /**
     * Сокращает название дисциплины для компактного отображения
     */
    private fun getShortDisciplineName(discipline: String): String {
        return when {
            discipline.length <= 15 -> discipline
            discipline.contains(" ") -> {
                val words = discipline.split(" ")
                if (words.size >= 2) {
                    "${words[0]} ${words[1].take(1)}."
                } else {
                    discipline.take(14) + "…"
                }
            }
            else -> discipline.take(14) + "…"
        }
    }
}

/**
 * Data class для хранения состояния виджета
 */
private data class WidgetData(
    val currentGroup: String = "",
    val currentDate: LocalDate = LocalDate.now(),
    val scheduleItems: List<ScheduleItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Composable функция для получения данных виджета
 */
@Composable
private fun rememberWidgetData(): WidgetData {
    // TODO: Реализовать получение реальных данных из AppState
    // Временная заглушка с тестовыми данными
    return WidgetData(
        currentGroup = AppState.currentGroup,
        currentDate = AppState.selectedDate ?: LocalDate.now(),
        scheduleItems = emptyList(),
        isLoading = false,
        error = null
    )
}