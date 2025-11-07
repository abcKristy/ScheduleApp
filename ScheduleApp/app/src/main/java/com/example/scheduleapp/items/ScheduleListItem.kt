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
                    text = scheduleItem.startTime,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen
                )
                Text(
                    text = scheduleItem.duration,
                    color = deepGreen
                )
                Text(
                    text = scheduleItem.endTime,
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
                        text = scheduleItem.lessonName,
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
                    text = scheduleItem.groups,
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



fun getTestScheduleItems(): List<ScheduleItem> {
    return listOf(
        ScheduleItem(
            id = 1,
            startTime = "09:00",
            duration = "1ч 30мин",
            endTime = "10:30",
            lessonName = "Математика",
            groups = "Группа А, Группа Б",
            teacher = "Иванов И.И."
        ),
        ScheduleItem(
            id = 2,
            startTime = "10:45",
            duration = "1ч 30мин",
            endTime = "12:15",
            lessonName = "Физика",
            groups = "Группа В",
            teacher = "Петров П.П."
        ),
        ScheduleItem(
            id = 3,
            startTime = "13:00",
            duration = "2ч 00мин",
            endTime = "15:00",
            lessonName = "Программирование",
            groups = "Группа А, Группа В",
            teacher = "Сидоров С.С."
        ),
        ScheduleItem(
            id = 4,
            startTime = "15:30",
            duration = "1ч 30мин",
            endTime = "17:00",
            lessonName = "Английский язык",
            groups = "Группа Б",
            teacher = "Кузнецова Е.В."
        ),
        ScheduleItem(
            id = 5,
            startTime = "17:15",
            duration = "1ч 30мин",
            endTime = "18:45",
            lessonName = "История",
            groups = "Группа А, Группа Б, Группа В",
            teacher = "Николаева О.П."
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ScheduleItemPreview() {
    MaterialTheme {
        ScheduleListItem(
            scheduleItem = ScheduleItem(
                id = 1,
                startTime = "09:00",
                duration = "1ч 30мин",
                endTime = "10:30",
                lessonName = "Математика",
                groups = "Группа А, Группа Б",
                teacher = "Иванов И.И."
            ),
            onOptionsClick = {}
        )
    }
}