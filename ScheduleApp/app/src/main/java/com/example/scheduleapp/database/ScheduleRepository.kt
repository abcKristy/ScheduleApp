package com.example.scheduleapp.database

import com.example.scheduleapp.data.RecurrenceRule
import com.example.scheduleapp.data.ScheduleItem
import kotlin.compareTo

class ScheduleRepository(private val database: ScheduleDatabase) {

    private val dao = database.scheduleDao()

    suspend fun getSchedule(group: String): List<ScheduleItem> {
        val cachedItems = dao.getScheduleByGroup(group)
        return cachedItems.map { it.toScheduleItem() }
    }

    suspend fun cacheScheduleItems(group: String, items: List<ScheduleItem>) {
        val entities = items.map { it.toScheduleEntity(group) }
        dao.insertScheduleItems(entities)
    }

    suspend fun hasCachedSchedule(group: String): Boolean {
        return dao.hasCachedSchedule(group) > 0
    }

    private fun ScheduleEntity.toScheduleItem(): ScheduleItem {
        return ScheduleItem(
            discipline = discipline,
            lessonType = lessonType,
            startTime = startTime,
            endTime = endTime,
            room = room,
            teacher = teacher,
            groups = groups,
            groupsSummary = groupsSummary,
            description = description,
            recurrence = if (frequency != null || interval != null || until != null) {
                RecurrenceRule(
                    frequency = frequency,
                    interval = interval,
                    until = until
                )
            } else null,
            exceptions = exceptions
        )
    }

    private fun ScheduleItem.toScheduleEntity(group: String): ScheduleEntity {
        return ScheduleEntity(
            id = "${group}_${startTime}_${discipline}", // Уникальный ID
            group = group,
            discipline = discipline,
            lessonType = lessonType,
            startTime = startTime,
            endTime = endTime,
            room = room,
            teacher = teacher,
            groups = groups,
            groupsSummary = groupsSummary,
            description = description,
            frequency = recurrence?.frequency,
            interval = recurrence?.interval,
            until = recurrence?.until,
            exceptions = exceptions,
            lastUpdated = System.currentTimeMillis()
        )
    }
}