package com.example.scheduleapp.data

data class ScheduleItem(
    val id: Int,
    val startTime: String,
    val duration: String,
    val endTime: String,
    val lessonName: String,
    val groups: String,
    val teacher: String
)