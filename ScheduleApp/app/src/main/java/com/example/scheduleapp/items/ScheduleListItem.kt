package com.example.scheduleapp.items

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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