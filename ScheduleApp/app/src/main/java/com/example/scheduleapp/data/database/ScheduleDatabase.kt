package com.example.scheduleapp.data.database


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [
        ScheduleEntity::class,
        CachedGroupEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: ScheduleDatabase? = null

        fun getInstance(context: Context): ScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDatabase::class.java,
                    "schedule_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Миграция с версии 1 на версию 2
         * Добавляет поля semester, cachedAt, expiresAt в schedule_items
         * Создает таблицу cached_groups
         */
        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL("""
                    ALTER TABLE schedule_items 
                    ADD COLUMN semester TEXT NOT NULL DEFAULT 'LEGACY'
                """)

                database.execSQL("""
                    ALTER TABLE schedule_items 
                    ADD COLUMN cachedAt INTEGER NOT NULL DEFAULT 0
                """)

                database.execSQL("""
                    ALTER TABLE schedule_items 
                    ADD COLUMN expiresAt INTEGER NOT NULL DEFAULT 0
                """)

                val currentTime = System.currentTimeMillis()
                val defaultExpiresAt = currentTime + 7 * 24 * 60 * 60 * 1000  // +7 дней

                database.execSQL("""
                    UPDATE schedule_items 
                    SET cachedAt = $currentTime, 
                        expiresAt = $defaultExpiresAt
                    WHERE cachedAt = 0
                """)

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS cached_groups (
                        groupName TEXT PRIMARY KEY NOT NULL,
                        semester TEXT NOT NULL,
                        itemCount INTEGER NOT NULL,
                        lastAccessed INTEGER NOT NULL,
                        cachedAt INTEGER NOT NULL
                    )
                """)

                database.execSQL("""
                    INSERT OR REPLACE INTO cached_groups (groupName, semester, itemCount, lastAccessed, cachedAt)
                    SELECT 
                        `group` as groupName,
                        'LEGACY' as semester,
                        COUNT(*) as itemCount,
                        $currentTime as lastAccessed,
                        $currentTime as cachedAt
                    FROM schedule_items 
                    GROUP BY `group`
                """)
            }
        }
    }
}