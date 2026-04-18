package com.example.scheduleapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scheduleapp.data.database.ScheduleDatabase
import com.example.scheduleapp.data.database.ScheduleRepository
import com.example.scheduleapp.data.state.PreferencesManager
import com.example.scheduleapp.util.SemesterUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CacheCleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "========== ЗАПУСК ОЧИСТКИ УСТАРЕВШИХ ДАННЫХ ==========")
            val startTime = System.currentTimeMillis()

            val database = ScheduleDatabase.getInstance(applicationContext)
            val repository = ScheduleRepository(database)

            val currentSemester = SemesterUtils.getCurrentSemester()
            Log.d(TAG, "Текущий семестр: $currentSemester")

            val cachedGroups = repository.getAllCachedGroupsInfo()
            Log.d(TAG, "Найдено ${cachedGroups.size} кэшированных групп")

            var deletedCount = 0
            var keptCount = 0

            cachedGroups.forEach { groupInfo ->
                try {
                    if (groupInfo.semester != currentSemester && groupInfo.semester != "LEGACY") {
                        Log.d(TAG, "Удаление устаревших данных для группы: ${groupInfo.groupName} (семестр: ${groupInfo.semester})")

                        repository.deleteCacheForGroupAndSemester(groupInfo.groupName, groupInfo.semester)
                        deletedCount++
                    } else {
                        keptCount++
                        Log.d(TAG, "Группа актуальна: ${groupInfo.groupName} (семестр: ${groupInfo.semester})")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при обработке группы ${groupInfo.groupName}", e)
                }
            }

            // Очистка просроченного кэша
            repository.cleanupExpiredCache()

            // Очистка устаревших групп из cached_groups
            repository.cleanupOutdatedGroups(currentSemester)

            // Сохраняем время последней очистки
            PreferencesManager.saveLastCacheCleanup(applicationContext, System.currentTimeMillis())

            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "========== ОЧИСТКА ЗАВЕРШЕНА ==========")
            Log.d(TAG, "Удалено групп: $deletedCount, сохранено групп: $keptCount, время: ${duration}ms")

            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Критическая ошибка при очистке кэша", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "CacheCleanupWorker"
        const val WORK_NAME = "cache_cleanup_worker"
    }
}