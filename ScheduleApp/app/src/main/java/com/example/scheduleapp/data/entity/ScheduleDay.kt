package com.example.scheduleapp.data.entity

import java.time.LocalDate
import java.time.LocalTime

data class ScheduleDay(
    val date: LocalDate,
    val firstLesson: ScheduleItem? = null,    // 9:00-10:30
    val firstBreak: BreakItem? = null,        // 10:30-10:40 (10 мин)
    val secondLesson: ScheduleItem? = null,   // 10:40-12:10
    val secondBreak: BreakItem? = null,       // 12:10-12:40 (30 мин)
    val thirdLesson: ScheduleItem? = null,    // 12:40-14:10
    val thirdBreak: BreakItem? = null,        // 14:10-14:20 (10 мин)
    val fourthLesson: ScheduleItem? = null,   // 14:20-15:50
    val fourthBreak: BreakItem? = null,       // 15:50-16:20 (30 мин)
    val fifthLesson: ScheduleItem? = null,    // 16:20-17:50
    val fifthBreak: BreakItem? = null,        // 17:50-18:00 (10 мин)
    val sixthLesson: ScheduleItem? = null,    // 18:00-19:30
    val sixthBreak: BreakItem? = null,        // 19:30-19:40 (10 мин)
    val seventhLesson: ScheduleItem? = null   // 19:40-21:10
) {
    val allItems: List<DayItem> get() {
        val items = mutableListOf<DayItem>()

        firstLesson?.let { items.add(DayItem.Lesson(it)) }
        firstBreak?.let { items.add(DayItem.Break(it)) }
        secondLesson?.let { items.add(DayItem.Lesson(it)) }
        secondBreak?.let { items.add(DayItem.Break(it)) }
        thirdLesson?.let { items.add(DayItem.Lesson(it)) }
        thirdBreak?.let { items.add(DayItem.Break(it)) }
        fourthLesson?.let { items.add(DayItem.Lesson(it)) }
        fourthBreak?.let { items.add(DayItem.Break(it)) }
        fifthLesson?.let { items.add(DayItem.Lesson(it)) }
        fifthBreak?.let { items.add(DayItem.Break(it)) }
        sixthLesson?.let { items.add(DayItem.Lesson(it)) }
        sixthBreak?.let { items.add(DayItem.Break(it)) }
        seventhLesson?.let { items.add(DayItem.Lesson(it)) }

        return items
    }

    val hasLessons: Boolean get() {
        return firstLesson != null || secondLesson != null || thirdLesson != null ||
                fourthLesson != null || fifthLesson != null || sixthLesson != null || seventhLesson != null
    }
}

sealed class DayItem {
    data class Lesson(val scheduleItem: ScheduleItem) : DayItem()
    data class Break(val breakItem: BreakItem) : DayItem()
}

data class BreakItem(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val durationMinutes: Int,
    val isBig: Boolean = false
) {
    val formattedTime: String get() = "$startTime-$endTime"
    val durationText: String get() = if (isBig) "30 минут" else "10 минут"
    val typeText: String get() = if (isBig) "Большая перемена" else "Маленькая перемена"
}