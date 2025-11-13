package com.example.scheduleapp.items

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.R
import com.example.scheduleapp.data.RecurrenceRule
import com.example.scheduleapp.data.ScheduleItem
import com.example.scheduleapp.ui.theme.deepGreen
import com.example.scheduleapp.ui.theme.gray
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleListItem(
    scheduleItem: ScheduleItem,
    onOptionsClick: ()->Unit
) {
    val hasRecurrence = scheduleItem.recurrence != null
    val hasExceptions = scheduleItem.exceptions.isNotEmpty()

    Card(
        modifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(gray),
        elevation = CardDefaults.cardElevation(4.dp)
    )
    {
        Column(
            modifier = Modifier
                .background(gray)
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.Start
                )
                {
                    Text(
                        text = scheduleItem.formattedStartTime,
                        fontWeight = FontWeight.Bold,
                        color = deepGreen
                    )
                    Text(
                        text = scheduleItem.duration,
                        color = deepGreen
                    )
                    Text(
                        text = scheduleItem.formattedEndTime,
                        fontWeight = FontWeight.Bold,
                        color = deepGreen
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(0.2f)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(
                                width = 2.dp,
                                color = deepGreen,
                                shape = CircleShape
                            )
                    )

                    Canvas(
                        modifier = Modifier
                            .height(100.dp)
                            .width(2.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        drawLine(
                            color = deepGreen,
                            start = Offset(size.width / 2, 0f),
                            end = Offset(size.width / 2, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier
                    .weight(2f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = scheduleItem.discipline,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            color = deepGreen
                        )
                        IconButton(
                            onClick = onOptionsClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_more),
                                contentDescription = "details of schedule",
                                tint = deepGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = scheduleItem.groupsSummary,
                        color = deepGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scheduleItem.teacher,
                        color = deepGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scheduleItem.room,
                        color = deepGreen
                    )

                    // Информация о повторении
                    if (hasRecurrence || hasExceptions) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val recurrenceInfo = buildString {
                            if (hasRecurrence) {
                                val recurrence = scheduleItem.recurrence!!
                                append("Повторение: ")
                                when (recurrence.frequency?.uppercase()) {
                                    "WEEKLY" -> append("еженедельно")
                                    "DAILY" -> append("ежедневно")
                                    "MONTHLY" -> append("ежемесячно")
                                    else -> append("повторяется")
                                }
                                if (recurrence.interval != null && recurrence.interval > 1) {
                                    append(" каждые ${recurrence.interval} нед.")
                                }
                                if (recurrence.until != null) {
                                    append(" до ${recurrence.until.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}")
                                }
                            }
                            if (hasExceptions) {
                                if (isNotEmpty()) append("\n")
                                append("Исключения: ${scheduleItem.exceptions.size} дат")
                            }
                        }
                        Text(
                            text = recurrenceInfo,
                            color = deepGreen.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleItemPreview() {
    MaterialTheme {
        ScheduleListItem(
            scheduleItem = ScheduleItem(
                discipline = "Разработка баз данных",
                lessonType = "PR",
                startTime = LocalDateTime.of(2025, 9, 6, 10, 40),
                endTime = LocalDateTime.of(2025, 9, 6, 12, 10),
                room = "И-212-б (В-78)",
                teacher = "Ужахов Нурдин Люреханович",
                groups = listOf("ИКБО-60-23"),
                groupsSummary = "ИКБО-60-23",
                description = "SQL запросы",
                recurrence = RecurrenceRule(
                    frequency = "WEEKLY",
                    interval = 1,
                    until = LocalDateTime.of(2025, 12, 13, 23, 59)
                ),
                exceptions = listOf(
                    java.time.LocalDate.of(2025, 10, 4),
                    java.time.LocalDate.of(2025, 11, 1)
                )
            ),
            onOptionsClick = {}
        )
    }
}