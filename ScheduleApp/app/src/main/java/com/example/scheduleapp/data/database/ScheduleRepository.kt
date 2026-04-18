package com.example.scheduleapp.data.database

import android.util.Log
import com.example.scheduleapp.data.entity.RecurrenceRule
import com.example.scheduleapp.data.entity.ScheduleItem

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

    suspend fun getScheduleForSemester(group: String, semester: String): List<ScheduleItem> {
        Log.d("REPOSITORY", "Getting schedule for group: $group, semester: $semester")
        val cachedItems = dao.getScheduleByGroupAndSemester(group, semester)
        Log.d("REPOSITORY", "Found ${cachedItems.size} items for semester $semester")
        return cachedItems.map { it.toScheduleItem() }
    }

    suspend fun hasCachedScheduleForSemester(group: String, semester: String): Boolean {
        val count = dao.hasCachedScheduleForSemester(group, semester)
        Log.d("REPOSITORY", "Checking cache for group: $group, semester: $semester - count: $count")
        return count > 0
    }

    suspend fun getCachedSemester(group: String): String? {
        return dao.getCachedSemesterForGroup(group)
    }

    suspend fun cacheScheduleItemsWithSemester(
        group: String,
        items: List<ScheduleItem>,
        semester: String
    ) {
        Log.d("REPOSITORY", "Caching ${items.size} items for group: $group, semester: $semester")
        val entities = items.map { it.toScheduleEntityWithSemester(group, semester) }
        dao.saveScheduleWithMetadata(group, entities, semester)
        Log.d("REPOSITORY", "Successfully cached items with semester metadata")
    }

    suspend fun updateLastAccessed(group: String) {
        dao.updateLastAccessed(group, System.currentTimeMillis())
    }

    suspend fun getAllCachedGroupsInfo(): List<CachedGroupEntity> {
        return dao.getAllCachedGroups()
    }

    suspend fun cleanupOutdatedGroups(currentSemester: String) {
        Log.d("REPOSITORY", "Cleaning up groups with semester != $currentSemester")
        dao.deleteGroupsWithDifferentSemester(currentSemester)
    }

    suspend fun cleanupExpiredCache() {
        val currentTime = System.currentTimeMillis()
        Log.d("REPOSITORY", "Cleaning up expired cache")
        dao.deleteExpiredSchedule(currentTime)
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
            id = "${group}_${startTime}_${discipline}",
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
            lastUpdated = System.currentTimeMillis(),
            semester = "LEGACY"
        )
    }

    private fun ScheduleItem.toScheduleEntityWithSemester(group: String, semester: String): ScheduleEntity {
        val currentTime = System.currentTimeMillis()
        return ScheduleEntity(
            id = "${group}_${startTime}_${discipline}_${currentTime}",
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
            lastUpdated = currentTime,
            semester = semester,
            cachedAt = currentTime,
            expiresAt = currentTime + 7 * 24 * 60 * 60 * 1000
        )
    }
}