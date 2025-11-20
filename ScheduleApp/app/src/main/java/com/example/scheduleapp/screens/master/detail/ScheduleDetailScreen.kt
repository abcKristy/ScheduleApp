package com.example.scheduleapp.screens.master.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.data.entity.ScheduleItem
import com.example.scheduleapp.data.entity.TestSchedule
import com.example.scheduleapp.ui.theme.ScheduleAppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleDetailScreen(onNavigateBack: () -> Boolean) {
    val scheduleItem = TestSchedule().firstOrNull() ?: ScheduleItem(
        discipline = "Разработка баз данных",
        lessonType = "LECTURE",
        startTime = LocalDateTime.of(2025, 9, 6, 9, 0),
        endTime = LocalDateTime.of(2025, 9, 6, 10, 30),
        room = "А-15 (В-78)",
        teacher = "Иванов Петр Сергеевич",
        groups = listOf("ИКБО-60-23", "ИКБО-61-23"),
        groupsSummary = "ИКБО-60-23, ИКБО-61-23",
        description = "Введение в базы данных. Основные понятия и принципы работы с реляционными базами данных.",
        recurrence = null,
        exceptions = emptyList()
    )

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = scheduleItem.discipline,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Основная информация",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            InfoCard(title = "Дисциплина", value = scheduleItem.discipline)
            InfoCard(title = "Тип занятия", value = scheduleItem.lessonType)
            InfoCard(title = "Аудитория", value = scheduleItem.room)
            InfoCard(title = "Преподаватель", value = scheduleItem.teacher)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Время и дата",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            InfoCard(title = "Дата и время начала", value = scheduleItem.startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
            InfoCard(title = "Дата и время окончания", value = scheduleItem.endTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
            InfoCard(title = "Время начала", value = scheduleItem.formattedStartTime)
            InfoCard(title = "Время окончания", value = scheduleItem.formattedEndTime)
            InfoCard(title = "Продолжительность", value = scheduleItem.duration)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Группы",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            InfoCard(title = "Список групп", value = scheduleItem.groups.joinToString(", "))
            InfoCard(title = "Сводка по группам", value = scheduleItem.groupsSummary)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Дополнительная информация",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            InfoCard(
                title = "Описание",
                value = scheduleItem.description ?: "Описание отсутствует"
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Повторение",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            scheduleItem.recurrence?.let { recurrence ->
                InfoCard(title = "Частота повторения", value = recurrence.frequency ?: "Не указана")
                recurrence.interval?.let {
                    InfoCard(title = "Интервал", value = it.toString())
                }
                recurrence.until?.let {
                    InfoCard(title = "Повторять до", value = it.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                }
            } ?: InfoCard(title = "Повторение", value = "Не повторяется")

            // ИСКЛЮЧЕНИЯ
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Исключения",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (scheduleItem.exceptions.isNotEmpty()) {
                InfoCard(
                    title = "Даты исключений",
                    value = scheduleItem.exceptions.joinToString(",\n") { it.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) }
                )
                InfoCard(
                    title = "Количество исключений",
                    value = scheduleItem.exceptions.size.toString()
                )
            } else {
                InfoCard(title = "Исключения", value = "Исключения отсутствуют")
            }

            // ВСЕ СВОЙСТВА ОБЪЕКТА (для отладки)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Все свойства объекта",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            InfoCard(
                title = "Полная информация",
                value = """
                    |Discipline: ${scheduleItem.discipline}
                    |LessonType: ${scheduleItem.lessonType}
                    |StartTime: ${scheduleItem.startTime}
                    |EndTime: ${scheduleItem.endTime}
                    |Room: ${scheduleItem.room}
                    |Teacher: ${scheduleItem.teacher}
                    |Groups: ${scheduleItem.groups}
                    |GroupsSummary: ${scheduleItem.groupsSummary}
                    |Description: ${scheduleItem.description}
                    |Duration: ${scheduleItem.duration}
                    |FormattedStartTime: ${scheduleItem.formattedStartTime}
                    |FormattedEndTime: ${scheduleItem.formattedEndTime}
                    |Recurrence: ${scheduleItem.recurrence}
                    |Exceptions: ${scheduleItem.exceptions}
                """.trimMargin()
            )
        }
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
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