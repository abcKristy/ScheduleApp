package com.example.scheduleapp.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduleapp.data.BreakItem
import com.example.scheduleapp.ui.theme.customColors
import com.example.scheduleapp.ui.theme.deepGreen
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalTime

@Composable
fun BreakItemList(
    breakItem: BreakItem,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    var progress by remember { mutableStateOf(0f) }
    var isCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(1000)
        }
    }

    LaunchedEffect(currentTime, breakItem) {
        val start = breakItem.startTime
        val end = breakItem.endTime

        isCompleted = currentTime.isAfter(end)
        progress = when {
            currentTime.isBefore(start) -> 0f
            currentTime.isAfter(end) -> 1f
            else -> {
                val totalDuration = Duration.between(start, end).seconds.toFloat()
                val elapsedDuration = Duration.between(start, currentTime).seconds.toFloat()
                (elapsedDuration.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
            }
        }
    }

    val durationText = "${breakItem.durationMinutes} минут"

    val gradientColors = if (isCompleted) {
        listOf(
            MaterialTheme.customColors.shiny.copy(0.2f),
            MaterialTheme.customColors.shiny.copy(0.2f)
        )
    } else {
        listOf(
            MaterialTheme.customColors.shiny.copy(0.9f),
            MaterialTheme.customColors.searchBar.copy(0.8f)
        )
    }

    val baseLineColor = if (isCompleted) {
        MaterialTheme.customColors.bg1.copy(0f)
    } else {
        MaterialTheme.customColors.dialogCont.copy(0.5f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.bg1.copy(0.9f) // Фон карточки всегда одинаковый
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 0.dp,0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .height(24.dp)
                    .padding(vertical = 3.dp, horizontal = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.Center)
                        .background(
                            color = baseLineColor,
                            shape = RoundedCornerShape(1.dp)
                        )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(3.dp)
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = gradientColors,
                                startX = 0f,
                                endX = Float.POSITIVE_INFINITY
                            )
                        )
                )
            }

            if(!isCompleted){
                Text(
                    text = durationText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.dp),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}


@Preview
@Composable
fun BreakItemListPreview() {
    MaterialTheme {
        Column {
            // Большая перемена (30 минут) - в процессе
            BreakItemList(
                breakItem = BreakItem(
                    startTime = LocalTime.now().minusMinutes(10), // началась 10 минут назад
                    endTime = LocalTime.now().plusMinutes(20), // закончится через 20 минут
                    durationMinutes = 30,
                    isBig = true
                )
            )

            // Маленькая перемена (10 минут) - еще не началась
            BreakItemList(
                breakItem = BreakItem(
                    startTime = LocalTime.now().plusMinutes(5), // начнется через 5 минут
                    endTime = LocalTime.now().plusMinutes(15),
                    durationMinutes = 10,
                    isBig = false
                )
            )

            // Большая перемена (30 минут) - завершена
            BreakItemList(
                breakItem = BreakItem(
                    startTime = LocalTime.now().minusMinutes(40), // началась 40 минут назад
                    endTime = LocalTime.now().minusMinutes(10), // закончилась 10 минут назад
                    durationMinutes = 30,
                    isBig = true
                )
            )
        }
    }
}