package com.example.scheduleapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedule_items WHERE `group` = :group ORDER BY startTime")
    suspend fun getScheduleByGroup(group: String): List<ScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItems(items: List<ScheduleEntity>)

    @Query("SELECT COUNT(*) FROM schedule_items WHERE `group` = :group")
    suspend fun hasCachedSchedule(group: String): Int

    @Query("SELECT * FROM schedule_items")
    suspend fun getAllScheduleItems(): List<ScheduleEntity>

    @Query("SELECT * FROM schedule_items WHERE `group` = :group AND semester = :semester ORDER BY startTime")
    suspend fun getScheduleByGroupAndSemester(group: String, semester: String): List<ScheduleEntity>


    @Query("SELECT COUNT(*) FROM schedule_items WHERE `group` = :group AND semester = :semester")
    suspend fun hasCachedScheduleForSemester(group: String, semester: String): Int


    @Query("SELECT DISTINCT semester FROM schedule_items WHERE `group` = :group LIMIT 1")
    suspend fun getCachedSemesterForGroup(group: String): String?


    @Query("DELETE FROM schedule_items WHERE `group` = :group AND semester = :semester")
    suspend fun deleteScheduleByGroupAndSemester(group: String, semester: String)

    @Query("DELETE FROM schedule_items WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredSchedule(currentTime: Long)


    @Query("SELECT DISTINCT `group`, semester, COUNT(*) as count FROM schedule_items GROUP BY `group`, semester")
    suspend fun getAllCachedGroupsWithSemester(): List<GroupSemesterInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedGroup(group: CachedGroupEntity)

    @Query("SELECT * FROM cached_groups ORDER BY lastAccessed DESC")
    suspend fun getAllCachedGroups(): List<CachedGroupEntity>

    @Query("SELECT * FROM cached_groups WHERE groupName = :groupName")
    suspend fun getCachedGroup(groupName: String): CachedGroupEntity?

    @Query("UPDATE cached_groups SET lastAccessed = :timestamp WHERE groupName = :groupName")
    suspend fun updateLastAccessed(groupName: String, timestamp: Long)

    @Query("DELETE FROM cached_groups WHERE groupName = :groupName")
    suspend fun deleteCachedGroup(groupName: String)

    @Query("DELETE FROM cached_groups WHERE semester != :currentSemester")
    suspend fun deleteGroupsWithDifferentSemester(currentSemester: String)

    @Query("SELECT COUNT(*) FROM schedule_items WHERE semester = :semester")
    suspend fun getTotalLessonsCount(semester: String): Int

    @Query("SELECT COUNT(*) FROM schedule_items WHERE `group` = :group AND expiresAt < :currentTime")
    suspend fun getExpiredItemsCount(group: String, currentTime: Long): Int

    @Query("SELECT COUNT(*) FROM schedule_items WHERE `group` = :group AND cachedAt < :threshold")
    suspend fun getItemsOlderThan(group: String, threshold: Long): Int

    @Query("DELETE FROM schedule_items WHERE semester = 'LEGACY'")
    suspend fun deleteLegacyItems()

    @Query("DELETE FROM cached_groups WHERE semester = 'LEGACY'")
    suspend fun deleteLegacyGroups()

    /**
     * Удалить все занятия с семестром, отличным от текущего
     */
    @Query("DELETE FROM schedule_items WHERE semester != :currentSemester AND semester != 'LEGACY'")
    suspend fun deleteBySemesterNot(currentSemester: String)

    @Transaction
    suspend fun saveScheduleWithMetadata(
        group: String,
        items: List<ScheduleEntity>,
        semester: String
    ) {
        deleteScheduleByGroupAndSemester(group, semester)
        insertScheduleItems(items)
        insertCachedGroup(
            CachedGroupEntity(
                groupName = group,
                semester = semester,
                itemCount = items.size,
                lastAccessed = System.currentTimeMillis(),
                cachedAt = System.currentTimeMillis()
            )
        )
    }
}

data class GroupSemesterInfo(
    val group: String,
    val semester: String,
    val count: Int
)