package com.example.scheduleapp.items

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.scheduleapp.data.ScheduleItem
import com.example.scheduleapp.ui.theme.deepGreen
import com.example.scheduleapp.ui.theme.gray
import java.time.LocalDateTime

@Composable
fun ScheduleListItem(
    scheduleItem: ScheduleItem,
    onOptionsClick: ()->Unit
) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(gray),
        elevation = CardDefaults.cardElevation(4.dp)
    )
    {
        Row(
            modifier = Modifier
                .background(gray)
                .wrapContentHeight()
                .padding(16.dp)
        )
        {
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
                id = "296a15c0-f67b-4fe5-a70a-2f0232fcb7c5",
                discipline = "Бэкенд-разработка",
                lessonType = "LK",
                startTime = LocalDateTime.of(2025, 9, 6, 9, 0),
                endTime = LocalDateTime.of(2025, 9, 6, 10, 30),
                room = "А-18 (В-78)",
                teacher = "Волков Михаил Юрьевич",
                groups = listOf("ИКБО-10-23", "ИКБО-13-23", "ИКБО-12-23", "ИКБО-11-23", "ИКБО-14-23", "ИКБО-15-23"),
                groupsSummary = "ИКБО-10-23, ИКБО-13-23, ИКБО-12-23, ИКБО-11-23, ИКБО-14-23, ИКБО-15-23",
                description = null
            ),
            onOptionsClick = {}
        )
    }
}

fun TestSchedule(): List<ScheduleItem> {
    return listOf(
        ScheduleItem(
            id = "1",
            discipline = "Бэкенд-разработка на Java",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 6, 9, 0),
            endTime = LocalDateTime.of(2025, 9, 6, 10, 30),
            room = "А-18",
            teacher = "Волков М.Ю.",
            groups = listOf("ИКБО-10-23", "ИКБО-11-23"),
            groupsSummary = "ИКБО-10-23, ИКБО-11-23",
            description = "Введение в Spring Framework"
        ),
        ScheduleItem(
            id = "2",
            discipline = "Веб-технологии",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 6, 10, 45),
            endTime = LocalDateTime.of(2025, 9, 6, 12, 15),
            room = "Б-22",
            teacher = "Смирнова О.Л.",
            groups = listOf("ИКБО-12-23"),
            groupsSummary = "ИКБО-12-23",
            description = "Разработка адаптивного дизайна"
        ),
        ScheduleItem(
            id = "3",
            discipline = "Мобильная разработка",
            lessonType = "LAB",
            startTime = LocalDateTime.of(2025, 9, 6, 13, 0),
            endTime = LocalDateTime.of(2025, 9, 6, 14, 30),
            room = "Комп. класс №3",
            teacher = "Козлов Д.В.",
            groups = listOf("ИКБО-13-23", "ИКБО-14-23"),
            groupsSummary = "ИКБО-13-23, ИКБО-14-23",
            description = "Создание первого Android-приложения"
        ),
        ScheduleItem(
            id = "4",
            discipline = "Базы данных",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 6, 14, 45),
            endTime = LocalDateTime.of(2025, 9, 6, 16, 15),
            room = "В-101",
            teacher = "Петрова Е.С.",
            groups = listOf("ИКБО-10-23", "ИКБО-11-23", "ИКБО-12-23"),
            groupsSummary = "ИКБО-10-23, ИКБО-11-23, ИКБО-12-23",
            description = "Нормализация баз данных. НФБК"
        ),
        ScheduleItem(
            id = "5",
            discipline = "Алгоритмы и структуры данных",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 6, 16, 30),
            endTime = LocalDateTime.of(2025, 9, 6, 18, 0),
            room = "Г-205",
            teacher = "Николаев А.В.",
            groups = listOf("ИКБО-13-23", "ИКБО-14-23", "ИКБО-15-23"),
            groupsSummary = "ИКБО-13-23, ИКБО-14-23, ИКБО-15-23",
            description = "Решение задач на графы и деревья"
        ),
        ScheduleItem(
            id = "6",
            discipline = "Иностранный язык",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 6, 18, 15),
            endTime = LocalDateTime.of(2025, 9, 6, 19, 45),
            room = "А-305",
            teacher = "Иванова Т.К.",
            groups = listOf("ИКБО-10-23"),
            groupsSummary = "ИКБО-10-23",
            description = "Технический английский для IT-специалистов"
        )
    )
}