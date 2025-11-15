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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.data.ScheduleItem
import com.example.scheduleapp.ui.theme.blue
import com.example.scheduleapp.ui.theme.deepGreen
import com.example.scheduleapp.ui.theme.lightGreen
import java.time.LocalDateTime

@Composable
fun ScheduleListItem(
    scheduleItem: ScheduleItem,
    onItemClick: (ScheduleItem) -> Unit = {}
) {
    val dotColor = when (scheduleItem.lessonType.uppercase()) {
        "LECTURE", "ЛЕКЦИЯ" -> deepGreen
        "PR", "PRACTICE", "ПРАКТИКА" -> blue
        else -> lightGreen
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
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                    )
                }
                Text(
                    text = "${scheduleItem.formattedStartTime}-${scheduleItem.formattedEndTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = scheduleItem.room,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(70.dp)
                    .padding(vertical = 4.dp)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawLine(
                        color = deepGreen,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }

            Column(
                modifier = Modifier.weight(2f)
                    .height(70.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = scheduleItem.discipline,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = scheduleItem.teacher,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Theme - Lecture")
@Composable
fun ScheduleItemPreviewLightLecture() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface {
            ScheduleListItem(
                scheduleItem = ScheduleItem(
                    discipline = "Разработка баз данных",
                    lessonType = "LECTURE", // Лекция - зеленый кружок
                    startTime = LocalDateTime.of(2025, 9, 6, 9, 0),
                    endTime = LocalDateTime.of(2025, 9, 6, 10, 30),
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

@Preview(showBackground = true, name = "Light Theme - Practice")
@Composable
fun ScheduleItemPreviewLightPractice() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface {
            ScheduleListItem(
                scheduleItem = ScheduleItem(
                    discipline = "Программирование",
                    lessonType = "PR", // Практика - синий кружок
                    startTime = LocalDateTime.of(2025, 9, 6, 11, 0),
                    endTime = LocalDateTime.of(2025, 9, 6, 12, 30),
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

@Preview(showBackground = true, name = "Dark Theme - Lecture")
@Composable
fun ScheduleItemPreviewDarkLecture() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface {
            ScheduleListItem(
                scheduleItem = ScheduleItem(
                    discipline = "Разработка баз данных",
                    lessonType = "LECTURE",
                    startTime = LocalDateTime.of(2025, 9, 6, 9, 0),
                    endTime = LocalDateTime.of(2025, 9, 6, 10, 30),
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

@Preview(showBackground = true, name = "Dark Theme - Practice")
@Composable
fun ScheduleItemPreviewDarkPractice() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface {
            ScheduleListItem(
                scheduleItem = ScheduleItem(
                    discipline = "Программирование",
                    lessonType = "PR",
                    startTime = LocalDateTime.of(2025, 9, 6, 11, 0),
                    endTime = LocalDateTime.of(2025, 9, 6, 12, 30),
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