package com.example.scheduleapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedule_items WHERE group = :group ORDER BY startTime")
    suspend fun getScheduleByGroup(group: String): List<ScheduleEntity>

    @Query("SELECT DISTINCT group FROM schedule_items")
    suspend fun getAllCachedGroups(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItems(items: List<ScheduleEntity>)

    @Query("DELETE FROM schedule_items WHERE group = :group")
    suspend fun deleteScheduleForGroup(group: String)

    @Query("SELECT COUNT(*) FROM schedule_items WHERE group = :group")
    suspend fun hasCachedSchedule(group: String): Int

    @Query("SELECT lastUpdated FROM schedule_items WHERE group = :group ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLastUpdateTime(group: String): Long?
}