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

    // Обновляем время каждую секунду
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(1000)
        }
    }

    // Вычисляем прогресс и статус завершения
    LaunchedEffect(currentTime, breakItem) {
        val start = breakItem.startTime
        val end = breakItem.endTime

        isCompleted = currentTime.isAfter(end)
        progress = when {
            currentTime.isBefore(start) -> 0f
            currentTime.isAfter(end) -> 1f
            else -> {
                val totalDuration = Duration.between(start, end).toMinutes()
                val elapsedDuration = Duration.between(start, currentTime).toMinutes()
                (elapsedDuration.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
            }
        }
    }

    val durationText = "${breakItem.durationMinutes} минут"

    // Цвет прогресс-полоски в зависимости от статуса
    val progressColor = if (isCompleted) {
        Color(0xFF9E9E9E) // Серый для завершенных
    } else {
        MaterialTheme.customColors.shiny
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.bg1.copy(0.9f) // Фон карточки всегда одинаковый
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Центральная часть с прогресс-полоской
            Box(
                modifier = Modifier
                    .weight(2f)
                    .height(24.dp)
                    .padding(horizontal = 8.dp)
            ) {
                // Фоновая полоска (всегда светло-серая)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.Center)
                        .background(
                            color = Color(0xFFE0E0E0), // Светло-серая
                            shape = RoundedCornerShape(1.dp)
                        )
                )

                // Прогресс полоска
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(3.dp)
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            color = progressColor
                        )
                )
            }

            // Время справа
            Text(
                text = durationText,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Цвет текста всегда одинаковый
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
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