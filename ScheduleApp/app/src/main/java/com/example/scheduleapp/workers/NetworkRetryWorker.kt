package com.example.scheduleapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scheduleapp.data.database.ScheduleDatabase
import com.example.scheduleapp.data.database.ScheduleRepository
import com.example.scheduleapp.data.state.PreferencesManager
import com.example.scheduleapp.logic.getScheduleItemsWithCache
import com.example.scheduleapp.util.SemesterUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkRetryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val group = inputData.getString(KEY_GROUP) ?: return@withContext Result.failure()
            val forceRefresh = inputData.getBoolean(KEY_FORCE_REFRESH, false)

            Log.d(TAG, "Запуск отложенной загрузки для группы: $group")

            val database = ScheduleDatabase.getInstance(applicationContext)
            val repository = ScheduleRepository(database)

            var success = false
            var errorMessage: String? = null

            getScheduleItemsWithCache(
                context = applicationContext,
                group = group,
                repository = repository,
                forceRefresh = forceRefresh,
                onSuccess = { items ->
                    success = true
                    Log.d(TAG, "✅ Отложенная загрузка успешна: $group, ${items.size} занятий")
                    PreferencesManager.removePendingRetryGroup(applicationContext, group)
                },
                onError = { error ->
                    errorMessage = error
                    Log.e(TAG, "❌ Ошибка отложенной загрузки: $group - $error")
                }
            )

            if (success) {
                Result.success()
            } else {
                Result.retry()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Критическая ошибка в NetworkRetryWorker", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "NetworkRetryWorker"
        const val WORK_NAME = "network_retry_worker"
        const val KEY_GROUP = "group"
        const val KEY_FORCE_REFRESH = "force_refresh"
    }
}