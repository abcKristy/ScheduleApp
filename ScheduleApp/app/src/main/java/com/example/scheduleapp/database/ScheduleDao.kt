package com.example.scheduleapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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
}