package com.example.scheduleapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_groups")
data class CachedGroupEntity(
    @PrimaryKey
    val groupName: String,
    val semester: String,
    val itemCount: Int,
    val lastAccessed: Long,
    val cachedAt: Long = System.currentTimeMillis()
)