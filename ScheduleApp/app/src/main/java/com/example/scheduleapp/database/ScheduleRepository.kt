package com.example.scheduleapp.database

import android.util.Log
import com.example.scheduleapp.data.RecurrenceRule
import com.example.scheduleapp.data.ScheduleItem
import kotlin.compareTo

class ScheduleRepository(private val database: ScheduleDatabase) {

    private val dao = database.scheduleDao()

    suspend fun getSchedule(group: String): List<ScheduleItem> {
        Log.d("REPOSITORY", "Getting schedule from DB for group: $group")
        val cachedItems = dao.getScheduleByGroup(group)
        Log.d("REPOSITORY", "Found ${cachedItems.size} items in database for group: $group")
        return cachedItems.map { it.toScheduleItem() }
    }

    suspend fun cacheScheduleItems(group: String, items: List<ScheduleItem>) {
        Log.d("REPOSITORY", "Caching ${items.size} items for group: $group")
        val entities = items.map { it.toScheduleEntity(group) }
        dao.insertScheduleItems(entities)
        Log.d("REPOSITORY", "Successfully cached items for group: $group")

        // Логируем ID для отладки
        entities.forEachIndexed { index, entity ->
            Log.d("REPOSITORY", "Cached item $index: ${entity.id}")
        }
    }

    suspend fun hasCachedSchedule(group: String): Boolean {
        val count = dao.hasCachedSchedule(group)
        Log.d("REPOSITORY", "Checking cache for group: $group - count: $count")
        return count > 0
    }

    suspend fun getAllCachedGroups(): List<String> {
        val allItems = dao.getAllScheduleItems()
        val groups = allItems.map { it.group }.distinct()
        Log.d("REPOSITORY", "All cached groups: $groups")
        return groups
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