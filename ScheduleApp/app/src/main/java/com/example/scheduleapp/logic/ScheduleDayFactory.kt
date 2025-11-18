// ScheduleDayFactory.kt
package com.example.scheduleapp.logic

import com.example.scheduleapp.data.*
import java.time.LocalDate
import java.time.LocalTime

object ScheduleDayFactory {

    // Временные интервалы пар (по умолчанию для МИРЭА)
    private val lessonTimes = listOf(
        LessonTimeSlot(1, LocalTime.of(9, 0), LocalTime.of(10, 30)),
        LessonTimeSlot(2, LocalTime.of(10, 40), LocalTime.of(12, 10)),
        LessonTimeSlot(3, LocalTime.of(12, 40), LocalTime.of(14, 10)),
        LessonTimeSlot(4, LocalTime.of(14, 20), LocalTime.of(15, 50)),
        LessonTimeSlot(5, LocalTime.of(16, 0), LocalTime.of(17, 30)),
        LessonTimeSlot(6, LocalTime.of(17, 40), LocalTime.of(19, 10)),
        LessonTimeSlot(7, LocalTime.of(19, 20), LocalTime.of(20, 50))
    )

    // Временные интервалы перемен
    private val breakTimes = listOf(
        BreakTimeSlot(1, LocalTime.of(10, 30), LocalTime.of(10, 40), 10, false),
        BreakTimeSlot(2, LocalTime.of(12, 10), LocalTime.of(12, 40), 30, true), // Большая перемена
        BreakTimeSlot(3, LocalTime.of(14, 10), LocalTime.of(14, 20), 10, false),
        BreakTimeSlot(4, LocalTime.of(15, 50), LocalTime.of(16, 0), 10, false),
        BreakTimeSlot(5, LocalTime.of(17, 30), LocalTime.of(17, 40), 10, false),
        BreakTimeSlot(6, LocalTime.of(19, 10), LocalTime.of(19, 20), 10, false)
    )

    fun createScheduleDay(date: LocalDate, scheduleItems: List<ScheduleItem>): ScheduleDay {
        // Сортируем пары по времени начала
        val sortedItems = scheduleItems.sortedBy { it.startTime.toLocalTime() }

        // Создаем мапу для быстрого доступа к паре по номеру
        val lessonMap = mutableMapOf<Int, ScheduleItem>()

        // Распределяем пары по слотам
        sortedItems.forEach { item ->
            val lessonNumber = findLessonNumber(item.startTime.toLocalTime())
            if (lessonNumber != null) {
                lessonMap[lessonNumber] = item
            }
        }

        // Создаем перемены
        val breakMap = breakTimes.associateBy { it.number }

        return ScheduleDay(
            date = date,
            firstLesson = lessonMap[1],
            firstBreak = breakMap[1]?.toBreakItem(),
            secondLesson = lessonMap[2],
            secondBreak = breakMap[2]?.toBreakItem(),
            thirdLesson = lessonMap[3],
            thirdBreak = breakMap[3]?.toBreakItem(),
            fourthLesson = lessonMap[4],
            fourthBreak = breakMap[4]?.toBreakItem(),
            fifthLesson = lessonMap[5],
            fifthBreak = breakMap[5]?.toBreakItem(),
            sixthLesson = lessonMap[6],
            sixthBreak = breakMap[6]?.toBreakItem(),
            seventhLesson = lessonMap[7]
        )
    }

    private fun findLessonNumber(startTime: LocalTime): Int? {
        return lessonTimes.find { it.startTime == startTime }?.number
    }

    // Вспомогательные data class для временных слотов
    private data class LessonTimeSlot(
        val number: Int,
        val startTime: LocalTime,
        val endTime: LocalTime
    )

    private data class BreakTimeSlot(
        val number: Int,
        val startTime: LocalTime,
        val endTime: LocalTime,
        val durationMinutes: Int,
        val isBig: Boolean
    ) {
        fun toBreakItem(): BreakItem {
            return BreakItem(startTime, endTime, durationMinutes, isBig)
        }
    }
}