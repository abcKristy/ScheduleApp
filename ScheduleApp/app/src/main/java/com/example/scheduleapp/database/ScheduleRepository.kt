package com.example.scheduleapp.database

import com.example.scheduleapp.data.RecurrenceRule
import com.example.scheduleapp.data.ScheduleItem


class ScheduleRepository(private val database: ScheduleDatabase) {

    private val dao = database.scheduleDao()

    suspend fun getSchedule(group: String): List<ScheduleItem> {
        // Сначала проверяем локальную базу
        val cachedItems = dao.getScheduleByGroup(group)

        return if (cachedItems.isNotEmpty()) {
            // Конвертируем Entity в Domain модель
            cachedItems.map { it.toScheduleItem() }
        } else {
            // Если нет в кэше, загружаем с сервера
            emptyList() // Серверная загрузка будет отдельно
        }
    }

    suspend fun cacheScheduleItems(group: String, items: List<ScheduleItem>) {
        val entities = items.map { it.toScheduleEntity(group) }
        dao.insertScheduleItems(entities)
    }

    suspend fun hasCachedSchedule(group: String): Boolean {
        return dao.hasCachedSchedule(group) > 0
    }

    suspend fun getCachedGroups(): List<String> {
        return dao.getAllCachedGroups()
    }

    suspend fun clearCacheForGroup(group: String) {
        dao.deleteScheduleForGroup(group)
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
            id = "${group}_${startTime}",
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