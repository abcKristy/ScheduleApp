package com.example.scheduleapp.screens.master.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.R
import com.example.scheduleapp.data.entity.ScheduleItem
import com.example.scheduleapp.data.entity.TestSchedule
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(
    onNavigateBack: () -> Boolean
) {
    val selectedScheduleItem = AppState.selectedScheduleItem
    val scheduleItem = selectedScheduleItem ?: getDefaultScheduleItem()

    // Определяем картинку в зависимости от типа занятия
    val backgroundImage = when (scheduleItem.lessonType.uppercase()) {
        "LECTURE", "ЛЕКЦИЯ" -> R.drawable.bg_lk
        else -> R.drawable.bg_pr
    }

    // Определяем номер пары по времени начала
    val lessonNumber = getLessonNumber(scheduleItem.startTime)
    val lessonTypeText = getLessonTypeText(scheduleItem.lessonType)
    val circleColor = when (scheduleItem.lessonType.uppercase()) {
        "LECTURE", "ЛЕКЦИЯ" -> Color(0xFF4CAF50) // Зеленый для лекций
        "PRACTICE", "ПРАКТИКА" -> Color(0xFF2196F3) // Синий для практик
        "LAB", "ЛАБОРАТОРНАЯ" -> Color(0xFFFF9800) // Оранжевый для лаб
        else -> Color(0xFF9C27B0) // Фиолетовый для остальных
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Фоновая картинка (верхняя 1/3)
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = "Фон занятия",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 255.dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Название пары по центру
            Text(
                text = scheduleItem.discipline,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Строка с типом занятия, временем и номером пары - ПО ЦЕНТРУ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center // Добавляем центрирование по горизонтали
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(circleColor)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = lessonTypeText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = "${scheduleItem.formattedStartTime}-${scheduleItem.formattedEndTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = "$lessonNumber пара",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Остальная информация - ПО ЛЕВОМУ КРАЮ
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Аудитория
                Text(
                    text = scheduleItem.room,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Преподаватель
                Text(
                    text = scheduleItem.teacher,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Группы
                Text(
                    text = scheduleItem.groups.joinToString(", "),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Дополнительная информация (если есть)
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // Описание
                scheduleItem.description?.let { description ->
                    if (description.isNotBlank()) {
                        InfoCard(
                            title = "Описание",
                            value = description
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Функция для определения номера пары по времени
private fun getLessonNumber(startTime: LocalDateTime): Int {
    val hour = startTime.hour
    return when (hour) {
        8, 9 -> 1
        10, 11 -> 2
        12, 13 -> 3
        14, 15 -> 4
        16, 17 -> 5
        18, 19 -> 6
        else -> 1
    }
}

// Функция для преобразования типа занятия в читаемый текст
private fun getLessonTypeText(lessonType: String): String {
    return when (lessonType.uppercase()) {
        "LECTURE", "ЛЕКЦИЯ" -> "Лекция"
        "PRACTICE", "ПРАКТИКА" -> "Практика"
        "LAB", "ЛАБОРАТОРНАЯ" -> "Лабораторная"
        else -> lessonType
    }
}

private fun getDefaultScheduleItem(): ScheduleItem {
    return TestSchedule().firstOrNull() ?: ScheduleItem(
        discipline = "Разработка мобильных приложений на Android",
        lessonType = "LECTURE",
        startTime = LocalDateTime.of(2025, 9, 6, 9, 0),
        endTime = LocalDateTime.of(2025, 9, 6, 10, 30),
        room = "А-15 (В-78)",
        teacher = "Иванов Петр Сергеевич",
        groups = listOf("ИКБО-60-23", "ИКБО-61-23", "ИКБО-62-23"),
        groupsSummary = "ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
        description = "Введение в разработку мобильных приложений. Основные концепции и архитектурные паттерны, используемые в современной мобильной разработке.",
        recurrence = null,
        exceptions = emptyList()
    )
}

@Preview(showBackground = true)
@Composable
fun ScheduleDetailScreenPreview() {
    ScheduleAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ScheduleDetailScreen(onNavigateBack = { true })
        }
    }
}