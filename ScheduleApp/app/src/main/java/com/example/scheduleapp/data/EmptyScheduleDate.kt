package com.example.scheduleapp.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object EmptySchedule {
    // Функция для создания пустой пары по номеру
    fun createEmptyItem(lessonNumber: Int, date: LocalDate): ScheduleItem {
        val (startTime, endTime) = getTimeByLessonNumber(lessonNumber)
        val startDateTime = LocalDateTime.of(date, startTime)
        val endDateTime = LocalDateTime.of(date, endTime)

        return ScheduleItem(
            discipline = "",
            lessonType = "EMPTY",
            startTime = startDateTime,
            endTime = endDateTime,
            room = "",
            teacher = "",
            groups = emptyList(),
            groupsSummary = "",
            description = null,
            recurrence = null,
            exceptions = emptyList()
        )
    }

    private fun getTimeByLessonNumber(lessonNumber: Int): Pair<LocalTime, LocalTime> {
        return when (lessonNumber) {
            1 -> LocalTime.of(9, 0) to LocalTime.of(10, 30)
            2 -> LocalTime.of(10, 40) to LocalTime.of(12, 10)
            3 -> LocalTime.of(12, 40) to LocalTime.of(14, 10)
            4 -> LocalTime.of(14, 20) to LocalTime.of(15, 50)
            5 -> LocalTime.of(16, 20) to LocalTime.of(17, 50)
            6 -> LocalTime.of(18, 0) to LocalTime.of(19, 30)
            7 -> LocalTime.of(19, 40) to LocalTime.of(21, 10)
            else -> LocalTime.of(9, 0) to LocalTime.of(10, 30)
        }
    }

    // Получить номер пары по времени начала
    fun getLessonNumber(scheduleItem: ScheduleItem): Int {
        val startTime = scheduleItem.startTime.toLocalTime()
        return when (startTime) {
            LocalTime.of(9, 0) -> 1
            LocalTime.of(10, 40) -> 2
            LocalTime.of(12, 40) -> 3
            LocalTime.of(14, 20) -> 4
            LocalTime.of(16, 20) -> 5
            LocalTime.of(18, 0) -> 6
            LocalTime.of(19, 40) -> 7
            else -> 1
        }
    }

    // Проверка, является ли пара пустой
    fun isEmpty(scheduleItem: ScheduleItem): Boolean {
        return scheduleItem.lessonType == "EMPTY"
    }
}