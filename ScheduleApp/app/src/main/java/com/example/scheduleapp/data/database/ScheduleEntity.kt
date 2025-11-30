package com.example.scheduleapp.data.database


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDateTime
import java.time.LocalDate

@Entity(tableName = "schedule_items")
@TypeConverters(Converters::class)
data class ScheduleEntity(
    @PrimaryKey
    val id: String,
    val group: String,
    val discipline: String,
    val lessonType: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val room: String,
    val teacher: String,
    val groups: List<String>,
    val groupsSummary: String,
    val description: String?,
    val frequency: String?,
    val interval: Int?,
    val until: LocalDateTime?,
    val exceptions: List<LocalDate>,
    val lastUpdated: Long = System.currentTimeMillis()
)