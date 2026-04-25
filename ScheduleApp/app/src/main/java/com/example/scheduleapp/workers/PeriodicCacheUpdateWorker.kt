package com.example.scheduleapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.scheduleapp.data.database.ScheduleDatabase
import com.example.scheduleapp.data.database.ScheduleRepository
import com.example.scheduleapp.data.state.PreferencesManager
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
            val database = ScheduleDatabase.getInstance(applicationContext)
            val repository = ScheduleRepository(database)

            repository.cleanupExpiredCache()

            val cacheTtlDays = PreferencesManager.getCacheTtlDays(applicationContext)
            val cacheTtlMillis = cacheTtlDays * 24 * 60 * 60 * 1000L

            val cachedGroups = repository.getAllCachedGroupsInfo()
            val currentTime = System.currentTimeMillis()

            val expiredGroups = cachedGroups.filter { groupInfo ->
                (currentTime - groupInfo.cachedAt) > cacheTtlMillis
            }

            if (expiredGroups.isNotEmpty()) {
                val workRequest = OneTimeWorkRequestBuilder<SemesterCheckWorker>()
                    .addTag(SemesterCheckWorker.WORK_NAME)
                    .build()
                WorkManager.getInstance(applicationContext).enqueue(workRequest)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "PeriodicCacheUpdate"
        const val WORK_NAME = "periodic_cache_update"
    }
}