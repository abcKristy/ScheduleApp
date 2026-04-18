package com.example.scheduleapp.data.database

import android.util.Log
import com.example.scheduleapp.data.entity.RecurrenceRule
import com.example.scheduleapp.data.entity.ScheduleItem
import com.example.scheduleapp.util.SemesterUtils

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

    /**
     * Сохранение расписания с указанием семестра и временных меток
     */
    suspend fun cacheScheduleItemsWithSemester(
        group: String,
        items: List<ScheduleItem>,
        semester: String = SemesterUtils.getCurrentSemester()
    ) {
        Log.d("REPOSITORY", "Caching ${items.size} items for group: $group, semester: $semester")

        val currentTime = System.currentTimeMillis()
        val expiresAt = currentTime + CACHE_TTL

        val entities = items.map { item ->
            item.toScheduleEntityWithSemester(group, semester, currentTime, expiresAt)
        }

        dao.saveScheduleWithMetadata(group, entities, semester)

        Log.d("REPOSITORY", "Successfully cached items with semester metadata")
    }

    /**
     * Получить расписание группы с учетом семестра
     */
    suspend fun getScheduleForSemester(group: String, semester: String): List<ScheduleItem> {
        Log.d("REPOSITORY", "Getting schedule for group: $group, semester: $semester")
        val cachedItems = dao.getScheduleByGroupAndSemester(group, semester)
        Log.d("REPOSITORY", "Found ${cachedItems.size} items for semester $semester")
        return cachedItems.map { it.toScheduleItem() }
    }

    /**
     * Проверить, есть ли кэш для группы с указанным семестром
     */
    suspend fun hasCachedScheduleForSemester(group: String, semester: String): Boolean {
        val count = dao.hasCachedScheduleForSemester(group, semester)
        Log.d("REPOSITORY", "Checking cache for group: $group, semester: $semester - count: $count")
        return count > 0
    }

    /**
     * Получить семестр кэшированных данных для группы
     */
    suspend fun getCachedSemester(group: String): String? {
        return dao.getCachedSemesterForGroup(group)
    }

    /**
     * Обновить время последнего доступа к группе
     */
    suspend fun updateLastAccessed(group: String) {
        dao.updateLastAccessed(group, System.currentTimeMillis())
    }

    /**
     * Получить все кэшированные группы с информацией о семестре
     */
    suspend fun getAllCachedGroupsInfo(): List<CachedGroupEntity> {
        return dao.getAllCachedGroups()
    }

    /**
     * Удалить устаревшие группы (с другим семестром)
     */
    suspend fun cleanupOutdatedGroups(currentSemester: String) {
        Log.d("REPOSITORY", "Cleaning up groups with semester != $currentSemester")
        dao.deleteGroupsWithDifferentSemester(currentSemester)
    }

    /**
     * Удалить просроченный кэш
     */
    suspend fun cleanupExpiredCache() {
        val currentTime = System.currentTimeMillis()
        Log.d("REPOSITORY", "Cleaning up expired cache")
        dao.deleteExpiredSchedule(currentTime)
    }

    /**
     * Проверить, истек ли срок кэша для группы
     */
    suspend fun isCacheExpired(group: String): Boolean {
        val items = dao.getScheduleByGroup(group)
        if (items.isEmpty()) return true

        val currentTime = System.currentTimeMillis()
        return items.any { it.expiresAt < currentTime }
    }

    /**
     * Удалить кэш для конкретной группы
     */
    suspend fun deleteCacheForGroup(group: String) {
        Log.d("REPOSITORY", "Deleting cache for group: $group")
        dao.deleteCachedGroup(group)
        val items = dao.getScheduleByGroup(group)
        items.forEach { /* удаление через отдельный метод */ }
    }

    companion object {
        private const val CACHE_TTL = 7 * 24 * 60 * 60 * 1000L // 7 дней в миллисекундах
    }

    /**
     * Удалить кэш для конкретной группы и семестра
     */
    suspend fun deleteCacheForGroupAndSemester(group: String, semester: String) {
        Log.d("REPOSITORY", "Deleting cache for group: $group, semester: $semester")
        dao.deleteScheduleByGroupAndSemester(group, semester)
    }

    /**
     * Полная очистка устаревших данных
     */
    suspend fun performFullCleanup(currentSemester: String): Int {
        Log.d("REPOSITORY", "Performing full cleanup for semester: $currentSemester")

        // Удаляем занятия с устаревшим семестром
        dao.deleteBySemesterNot(currentSemester)

        // Удаляем просроченный кэш
        cleanupExpiredCache()

        // Удаляем метаданные устаревших групп
        cleanupOutdatedGroups(currentSemester)

        val remainingGroups = getAllCachedGroupsInfo()
        Log.d("REPOSITORY", "After cleanup: ${remainingGroups.size} groups remain")

        return remainingGroups.size
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
        val currentTime = System.currentTimeMillis()
        return ScheduleEntity(
            id = "${group}_${startTime}_${discipline}_$currentTime",
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
            semester = "LEGACY",
            cachedAt = currentTime,
            expiresAt = currentTime + CACHE_TTL
        )
    }

    private fun ScheduleItem.toScheduleEntityWithSemester(
        group: String,
        semester: String,
        cachedAt: Long,
        expiresAt: Long
    ): ScheduleEntity {
        return ScheduleEntity(
            id = "${group}_${startTime}_${discipline}_$cachedAt",
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
            lastUpdated = cachedAt,
            semester = semester,
            cachedAt = cachedAt,
            expiresAt = expiresAt
        )
    }
}