// ScheduleFilter.kt
package com.example.scheduleapp.logic

import com.example.scheduleapp.data.ScheduleDay
import com.example.scheduleapp.data.ScheduleItem
import java.time.LocalDate
import java.time.LocalDateTime
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

/**
 * Проверяет, попадает ли дата в правило повторения занятия
 */
fun isDateInRecurrence(scheduleItem: ScheduleItem, targetDate: LocalDate): Boolean {
    val recurrence = scheduleItem.recurrence ?: return false
    val startDate = scheduleItem.startTime.toLocalDate()

    // Целевая дата должна быть после или равна начальной дате
    if (targetDate.isBefore(startDate)) {
        return false
    }

    // Проверяем until (ограничение по дате окончания повторений)
    if (recurrence.until != null && targetDate.atStartOfDay().isAfter(recurrence.until)) {
        return false
    }

    // Проверяем интервал повторения
    val weeksBetween = java.time.temporal.ChronoUnit.WEEKS.between(startDate, targetDate)
    if (recurrence.interval != null && recurrence.interval > 0) {
        if (weeksBetween % recurrence.interval != 0L) {
            return false
        }
    }

    // Проверяем тип повторения (frequency)
    return when (recurrence.frequency?.uppercase()) {
        "WEEKLY" -> true
        else -> true // По умолчанию считаем еженедельным
    }
}

/**
 * Проверяет, является ли дата исключением для занятия
 */
fun isDateException(scheduleItem: ScheduleItem, targetDate: LocalDate): Boolean {
    return scheduleItem.exceptions.any { it == targetDate }
}

/**
 * Проверяет, должно ли занятие отображаться на указанную дату
 */
fun shouldShowOnDate(scheduleItem: ScheduleItem, targetDate: LocalDate): Boolean {
    val itemDate = scheduleItem.startTime.toLocalDate()
    val itemDayOfWeek = scheduleItem.startTime.dayOfWeek
    val targetDayOfWeek = targetDate.dayOfWeek

    // Проверяем, не является ли дата исключением
    if (isDateException(scheduleItem, targetDate)) {
        return false
    }

    // Если дата совпадает точно с оригинальной датой занятия
    if (itemDate == targetDate) {
        return true // Оригинальное занятие всегда показывается (если не исключение)
    }

    // Проверяем, совпадает ли день недели
    if (itemDayOfWeek != targetDayOfWeek) {
        return false
    }

    val recurrence = scheduleItem.recurrence

    return if (recurrence != null) {
        // Для занятий с правилом повторения
        isDateInRecurrence(scheduleItem, targetDate)
    } else {
        // Для занятий без правила повторения - только оригинальная дата
        false
    }
}

fun filterScheduleByDate(scheduleItems: List<ScheduleItem>, selectedDate: LocalDate?): List<ScheduleItem> {
    if (selectedDate == null) return emptyList()

    return scheduleItems.filter { scheduleItem ->
        shouldShowOnDate(scheduleItem, selectedDate)
    }.sortedBy { it.startTime }
}

fun createScheduleDayForDate(scheduleItems: List<ScheduleItem>, targetDate: LocalDate): ScheduleDay {
    val filteredItems = scheduleItems.filter { scheduleItem ->
        shouldShowOnDate(scheduleItem, targetDate)
    }

    return ScheduleDayFactory.createScheduleDay(targetDate, filteredItems)
}