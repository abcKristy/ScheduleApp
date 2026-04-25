package com.example.scheduleapp.data.entity

import java.time.LocalDate
import java.time.LocalTime

data class DynamicScheduleDay(
    val date: LocalDate,
    val items: List<DayItem>
) {
    val hasLessons: Boolean get() = items.any { it is DayItem.Lesson }
    val allItems: List<DayItem> get() = items
}

object DynamicScheduleDayFactory {

    private val lessonTimes = listOf(
        LessonTimeSlot(1, LocalTime.of(9, 0), LocalTime.of(10, 30)),
        LessonTimeSlot(2, LocalTime.of(10, 40), LocalTime.of(12, 10)),
        LessonTimeSlot(3, LocalTime.of(12, 40), LocalTime.of(14, 10)),
        LessonTimeSlot(4, LocalTime.of(14, 20), LocalTime.of(15, 50)),
        LessonTimeSlot(5, LocalTime.of(16, 20), LocalTime.of(17, 50)),
        LessonTimeSlot(6, LocalTime.of(18, 0), LocalTime.of(19, 30)),
        LessonTimeSlot(7, LocalTime.of(19, 40), LocalTime.of(21, 10))
    )

    private val breakTimes = listOf(
        BreakTimeSlot(1, LocalTime.of(10, 30), LocalTime.of(10, 40), 10, false),
        BreakTimeSlot(2, LocalTime.of(12, 10), LocalTime.of(12, 40), 30, true),
        BreakTimeSlot(3, LocalTime.of(14, 10), LocalTime.of(14, 20), 10, false),
        BreakTimeSlot(4, LocalTime.of(15, 50), LocalTime.of(16, 20), 30, true),
        BreakTimeSlot(5, LocalTime.of(17, 50), LocalTime.of(18, 0), 10, false),
        BreakTimeSlot(6, LocalTime.of(19, 30), LocalTime.of(19, 40), 10, false)
    )

    fun createDynamicScheduleDay(
        date: LocalDate,
        scheduleItems: List<ScheduleItem>,
        showEmptyLessons: Boolean
    ): DynamicScheduleDay {
        return if (showEmptyLessons) {
            createWithEmptyLessons(date, scheduleItems)
        } else {
            createWithoutEmptyLessons(date, scheduleItems)
        }
    }

    private fun createWithEmptyLessons(date: LocalDate, scheduleItems: List<ScheduleItem>): DynamicScheduleDay {
        val scheduleDay = ScheduleDayFactory.createScheduleDay(date, scheduleItems)
        return DynamicScheduleDay(date, scheduleDay.allItems)
    }

    private fun createWithoutEmptyLessons(date: LocalDate, scheduleItems: List<ScheduleItem>): DynamicScheduleDay {
        val items = mutableListOf<DayItem>()
        val sortedItems = scheduleItems
            .filter { !EmptySchedule.isEmpty(it) }
            .sortedBy { it.startTime.toLocalTime() }

        if (sortedItems.isEmpty()) {
            return DynamicScheduleDay(date, emptyList())
        }

        items.add(DayItem.Lesson(sortedItems.first()))

        for (i in 1 until sortedItems.size) {
            val prevLesson = sortedItems[i - 1]
            val currentLesson = sortedItems[i]

            val breakItem = findBreakBetweenLessons(prevLesson.endTime.toLocalTime(), currentLesson.startTime.toLocalTime())
            breakItem?.let { items.add(DayItem.Break(it)) }

            items.add(DayItem.Lesson(currentLesson))
        }

        return DynamicScheduleDay(date, items)
    }

    private fun findBreakBetweenLessons(prevEndTime: LocalTime, nextStartTime: LocalTime): BreakItem? {
        return breakTimes.find { breakTime ->
            breakTime.startTime == prevEndTime && breakTime.endTime == nextStartTime
        }?.toBreakItem()
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