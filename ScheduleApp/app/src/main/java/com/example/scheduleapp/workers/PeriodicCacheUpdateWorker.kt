package com.example.scheduleapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scheduleapp.data.database.ScheduleDatabase
import com.example.scheduleapp.data.database.ScheduleRepository
import com.example.scheduleapp.util.SemesterUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PeriodicCacheUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Запуск периодической проверки кэша")

            val database = ScheduleDatabase.getInstance(applicationContext)
            val repository = ScheduleRepository(database)

            repository.cleanupExpiredCache()

            val currentSemester = SemesterUtils.getCurrentSemester()
            val cachedGroups = repository.getAllCachedGroupsInfo()

            val expiredGroups = cachedGroups.filter { groupInfo ->
                val cacheTTL = 7 * 24 * 60 * 60 * 1000L
                val currentTime = System.currentTimeMillis()
                (currentTime - groupInfo.cachedAt) > cacheTTL
            }

            Log.d(TAG, "Найдено ${expiredGroups.size} групп с истекшим кэшем")

            if (expiredGroups.isNotEmpty()) {
                val workRequest = androidx.work.OneTimeWorkRequestBuilder<SemesterCheckWorker>()
                    .addTag(SemesterCheckWorker.WORK_NAME)
                    .build()

                androidx.work.WorkManager.getInstance(applicationContext)
                    .enqueue(workRequest)

                Log.d(TAG, "Запущен worker для обновления устаревших групп")
            }

            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка в периодической проверке", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "PeriodicCacheUpdate"
        const val WORK_NAME = "periodic_cache_update"
    }
}