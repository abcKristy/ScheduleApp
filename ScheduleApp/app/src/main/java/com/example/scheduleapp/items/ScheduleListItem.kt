package com.example.scheduleapp.items

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.data.ScheduleItem
import com.example.scheduleapp.ui.theme.blue
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.ui.theme.deepGreen
import com.example.scheduleapp.ui.theme.lightGreen
import com.example.scheduleapp.ui.theme.pink40
import com.example.scheduleapp.ui.theme.purple40
import com.example.scheduleapp.ui.theme.purple80
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun ScheduleListItem(
    scheduleItem: ScheduleItem,
    onItemClick: (ScheduleItem) -> Unit = {}
) {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    var progress by remember { mutableStateOf(0f) }
    var isLessonActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(1000)
        }
    }

    LaunchedEffect(currentTime, scheduleItem) {
        val start = scheduleItem.startTime.toLocalTime()
        val end = scheduleItem.endTime.toLocalTime()

        isLessonActive = currentTime.isAfter(start) && currentTime.isBefore(end)

        progress = when {
            currentTime.isBefore(start) -> 0f
            currentTime.isAfter(end) -> 1f
            else -> {
                val totalSeconds = Duration.between(start, end).seconds.toFloat()
                val elapsedSeconds = Duration.between(start, currentTime).seconds.toFloat()
                (elapsedSeconds / totalSeconds).coerceIn(0f, 1f)
            }
        }
    }

    val dotColor = when (scheduleItem.lessonType.uppercase()) {
        "LK", "LECTURE", "ЛЕКЦИЯ" -> deepGreen
        "PR", "PRACTICE", "ПРАКТИКА" -> blue
        else -> pink40
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onItemClick(scheduleItem) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.bg1
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Text(
                            text = scheduleItem.lessonType,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                        )
                    }
                    Text(
                        text = "${scheduleItem.formattedStartTime}-${scheduleItem.formattedEndTime}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = scheduleItem.room,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    modifier = Modifier.weight(2f)
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = scheduleItem.discipline,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = scheduleItem.teacher,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            // Прогресс-бар для активной пары
            if (isLessonActive) {
                val gradientColors = listOf(
                    MaterialTheme.customColors.shiny.copy(0.9f),
                    MaterialTheme.customColors.searchBar.copy(0.8f)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .background(
                            color = MaterialTheme.customColors.bg1.copy(0f)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(3.dp)
                            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = gradientColors,
                                    startX = 0f,
                                    endX = Float.POSITIVE_INFINITY
                                )
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Theme - Active Lesson")
@Composable
fun ScheduleItemPreviewActiveLesson() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface {
            ScheduleListItem(
                scheduleItem = ScheduleItem(
                    discipline = "Разработка баз данных",
                    lessonType = "LECTURE",
                    startTime = LocalDateTime.now().minusMinutes(30), // Началась 30 минут назад
                    endTime = LocalDateTime.now().plusMinutes(60), // Закончится через 60 минут
                    room = "А-15 (В-78)",
                    teacher = "Иванов Петр Сергеевич",
                    groups = listOf("ИКБО-60-23"),
                    groupsSummary = "ИКБО-60-23",
                    description = "Введение в базы данных",
                    recurrence = null,
                    exceptions = emptyList()
                ),
                onItemClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Light Theme - Not Started")
@Composable
fun ScheduleItemPreviewNotStarted() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface {
            ScheduleListItem(
                scheduleItem = ScheduleItem(
                    discipline = "Программирование",
                    lessonType = "PR",
                    startTime = LocalDateTime.now().plusMinutes(30), // Начнется через 30 минут
                    endTime = LocalDateTime.now().plusMinutes(120),
                    room = "Б-24 (В-78)",
                    teacher = "Сидорова Мария Ивановна",
                    groups = listOf("ИКБО-60-23"),
                    groupsSummary = "ИКБО-60-23",
                    description = "Практика по Java",
                    recurrence = null,
                    exceptions = emptyList()
                ),
                onItemClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Light Theme - Finished")
@Composable
fun ScheduleItemPreviewFinished() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface {
            ScheduleListItem(
                scheduleItem = ScheduleItem(
                    discipline = "Математика",
                    lessonType = "LK",
                    startTime = LocalDateTime.now().minusMinutes(120), // Началась 2 часа назад
                    endTime = LocalDateTime.now().minusMinutes(30), // Закончилась 30 минут назад
                    room = "В-10 (В-78)",
                    teacher = "Петров Алексей Владимирович",
                    groups = listOf("ИКБО-60-23"),
                    groupsSummary = "ИКБО-60-23",
                    description = "Линейная алгебра",
                    recurrence = null,
                    exceptions = emptyList()
                ),
                onItemClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Dark Theme - Active Lesson")
@Composable
fun ScheduleItemPreviewDarkActive() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface {
            ScheduleListItem(
                scheduleItem = ScheduleItem(
                    discipline = "Разработка баз данных",
                    lessonType = "LECTURE",
                    startTime = LocalDateTime.now().minusMinutes(30),
                    endTime = LocalDateTime.now().plusMinutes(60),
                    room = "А-15 (В-78)",
                    teacher = "Иванов Петр Сергеевич",
                    groups = listOf("ИКБО-60-23"),
                    groupsSummary = "ИКБО-60-23",
                    description = "Введение в базы данных",
                    recurrence = null,
                    exceptions = emptyList()
                ),
                onItemClick = {}
            )
        }
    }
}