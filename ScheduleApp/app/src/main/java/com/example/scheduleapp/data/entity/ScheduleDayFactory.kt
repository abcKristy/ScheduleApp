package com.example.scheduleapp.data.entity

import java.time.LocalDate
import java.time.LocalTime

object ScheduleDayFactory {

    // Временные интервалы пар (обновленные согласно комментариям)
    private val lessonTimes = listOf(
        LessonTimeSlot(1, LocalTime.of(9, 0), LocalTime.of(10, 30)),    // 9:00-10:30
        LessonTimeSlot(2, LocalTime.of(10, 40), LocalTime.of(12, 10)),  // 10:40-12:10
        LessonTimeSlot(3, LocalTime.of(12, 40), LocalTime.of(14, 10)),  // 12:40-14:10
        LessonTimeSlot(4, LocalTime.of(14, 20), LocalTime.of(15, 50)),  // 14:20-15:50
        LessonTimeSlot(5, LocalTime.of(16, 20), LocalTime.of(17, 50)),  // 16:20-17:50
        LessonTimeSlot(6, LocalTime.of(18, 0), LocalTime.of(19, 30)),   // 18:00-19:30
        LessonTimeSlot(7, LocalTime.of(19, 40), LocalTime.of(21, 10))   // 19:40-21:10
    )

    // Временные интервалы перемен (обновленные согласно комментариям)
    private val breakTimes = listOf(
        BreakTimeSlot(1, LocalTime.of(10, 30), LocalTime.of(10, 40), 10, false),  // 10:30-10:40 (10 мин)
        BreakTimeSlot(2, LocalTime.of(12, 10), LocalTime.of(12, 40), 30, true),   // 12:10-12:40 (30 мин) - большая перемена
        BreakTimeSlot(3, LocalTime.of(14, 10), LocalTime.of(14, 20), 10, false),  // 14:10-14:20 (10 мин)
        BreakTimeSlot(4, LocalTime.of(15, 50), LocalTime.of(16, 20), 30, true),   // 15:50-16:20 (30 мин)
        BreakTimeSlot(5, LocalTime.of(17, 50), LocalTime.of(18, 0), 10, false),   // 17:50-18:00 (10 мин)
        BreakTimeSlot(6, LocalTime.of(19, 30), LocalTime.of(19, 40), 10, false)   // 19:30-19:40 (10 мин)
    )

    fun createScheduleDay(date: LocalDate, scheduleItems: List<ScheduleItem>): ScheduleDay {
        val sortedItems = scheduleItems.sortedBy { it.startTime.toLocalTime() }

        val lessonMap = mutableMapOf<Int, ScheduleItem>()

        sortedItems.forEach { item ->
            val lessonNumber = findLessonNumber(item.startTime.toLocalTime())
            if (lessonNumber != null) {
                lessonMap[lessonNumber] = item
            }
        }

        for (lessonNumber in 1..7) {
            if (!lessonMap.containsKey(lessonNumber)) {
                lessonMap[lessonNumber] = EmptySchedule.createEmptyItem(lessonNumber, date)
            }
        }

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