package com.example.scheduleapp.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ScheduleItem(
    val id: String,
    val discipline: String,
    val lessonType: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val room: String,
    val teacher: String,
    val groups: List<String>,
    val groupsSummary: String,
    val description: String?
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