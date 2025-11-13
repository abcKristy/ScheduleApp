package com.example.scheduleapp.data

import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ScheduleItem(
    val discipline: String,
    val lessonType: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val room: String,
    val teacher: String,
    val groups: List<String>,
    val groupsSummary: String,
    val description: String?,
    val recurrence: RecurrenceRule? = null,
    val exceptions: List<LocalDate> = emptyList()
){
    val duration: String
        get() {
            val durationMinutes = java.time.Duration.between(startTime,endTime).toMinutes()
            val hours = durationMinutes/60
            val minutes = durationMinutes%60
            return if (hours>0){
                if (minutes>0)"${hours}ч ${minutes}мин" else "${hours}ч"
            }else "${minutes}мин"
        }

    val formattedStartTime: String
        get() = startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    val formattedEndTime: String
        get() = endTime.format(DateTimeFormatter.ofPattern("HH:mm"))

}

data class RecurrenceRule(
    val frequency: String? = null,
    val interval: Int? = null,
    val until: LocalDateTime? = null
)