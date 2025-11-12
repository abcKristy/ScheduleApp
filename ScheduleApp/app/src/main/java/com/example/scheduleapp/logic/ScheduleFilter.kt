// ScheduleFilter.kt
package com.example.scheduleapp.logic

import com.example.scheduleapp.data.ScheduleItem
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * Определяет, является ли неделя четной (true) или нечетной (false)
 * Считаем, что неделя с 1 сентября 2025 года - нечетная (false)
 */
fun isEvenWeek(date: LocalDate): Boolean {
    val referenceDate = LocalDate.of(2025, 9, 1)
    val referenceWeek = referenceDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
    val currentWeek = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())

    return (currentWeek - referenceWeek) % 2 == 0
}

fun filterScheduleByDate(scheduleItems: List<ScheduleItem>, selectedDate: LocalDate?): List<ScheduleItem> {
    if (selectedDate == null) return emptyList()

    val dayOfWeek = selectedDate.dayOfWeek
    val isEven = isEvenWeek(selectedDate)

    return scheduleItems.filter { scheduleItem ->
        val itemDayOfWeek = scheduleItem.startTime.dayOfWeek
        val itemWeekType = isEvenWeek(scheduleItem.startTime.toLocalDate())

        itemDayOfWeek == dayOfWeek && itemWeekType == isEven
    }.sortedBy { it.startTime }
}