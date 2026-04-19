package com.example.scheduleapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scheduleapp.data.state.PreferencesManager
import com.example.scheduleapp.logic.createApiService
import com.example.scheduleapp.util.SemesterUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SemesterAvailabilityWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val group = inputData.getString(KEY_GROUP) ?: return@withContext Result.failure()

            Log.d(TAG, "Проверка доступности нового семестра для группы: $group")

            val currentSemester = SemesterUtils.getCurrentSemester()

            try {
                val apiService = createApiService()
                val response = apiService.getSchedule(group)

                if (response.isNotEmpty()) {
                    Log.d(TAG, "✅ Новый семестр доступен для группы: $group")
                    PreferencesManager.setApiHasNewSemester(applicationContext, true)
                    PreferencesManager.resetFailedAttempts(applicationContext)

                    val updateWorker = androidx.work.OneTimeWorkRequestBuilder<SemesterCheckWorker>()
                        .addTag(SemesterCheckWorker.WORK_NAME)
                        .build()

                    androidx.work.WorkManager.getInstance(applicationContext)
                        .enqueue(updateWorker)

                    Result.success()
                } else {
                    handleEmptyResponse(group)
                }

            } catch (e: Exception) {
                handleApiError(group, e)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Критическая ошибка", e)
            Result.retry()
        }
    }

    private fun handleEmptyResponse(group: String): Result {
        Log.w(TAG, "API вернул пустой ответ для группы: $group")

        val attempts = PreferencesManager.incrementFailedAttempts(applicationContext)
        PreferencesManager.setApiHasNewSemester(applicationContext, false)

        Log.d(TAG, "Количество неудачных попыток: $attempts")

        scheduleNextRetry()

        return Result.retry()
    }

    private fun handleApiError(group: String, error: Exception): Result {
        Log.e(TAG, "Ошибка API для группы: $group", error)

        PreferencesManager.incrementFailedAttempts(applicationContext)

        scheduleNextRetry()

        return Result.retry()
    }

    private fun scheduleNextRetry() {
        val retryHours = PreferencesManager.getRetryIntervalHours(applicationContext)
        Log.d(TAG, "Планирование следующей проверки через $retryHours часов")
    }

    companion object {
        private const val TAG = "SemesterAvailability"
        const val WORK_NAME = "semester_availability_worker"
        const val KEY_GROUP = "group"
    }
}