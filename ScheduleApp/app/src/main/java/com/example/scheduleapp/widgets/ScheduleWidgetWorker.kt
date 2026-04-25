package com.example.scheduleapp.widgets

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scheduleapp.data.state.AppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleWidgetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting widget data update...")

            if (AppState.repository == null) {
                Log.d(TAG, "Initializing AppState...")
                AppState.initialize(applicationContext)
            }

            val currentGroup = AppState.currentGroup
            if (currentGroup.isBlank() || currentGroup == " ") {
                Log.w(TAG, "No group selected for widget")
                ScheduleWidget().updateAll(applicationContext)
                return@withContext Result.success()
            }

            Log.d(TAG, "Getting widget data for group: $currentGroup")
            val widgetData = WidgetDataManager.getWidgetData(applicationContext)

            val totalItems = widgetData.scheduleByDate.values.sumOf { it.size }
            val daysWithLessons = widgetData.scheduleByDate.count { it.value.isNotEmpty() }

            Log.d(TAG, "Widget data retrieved: $totalItems items across $daysWithLessons days, error: ${widgetData.error}")

            ScheduleWidget().updateAll(applicationContext)

            Log.d(TAG, "Widget update completed successfully")
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget data", e)

            try {
                ScheduleWidget().updateAll(applicationContext)
            } catch (updateError: Exception) {
                Log.e(TAG, "Failed to update widget after error", updateError)
            }

            if (isTemporaryError(e)) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private fun isTemporaryError(e: Exception): Boolean {
        return when (e) {
            is java.net.UnknownHostException,
            is java.net.ConnectException,
            is java.net.SocketTimeoutException -> true
            else -> false
        }
    }

    companion object {
        private const val TAG = "ScheduleWidgetWorker"
        const val WORK_TAG = "schedule_widget_update"
        const val DAILY_UPDATE_WORK_NAME = "schedule_widget_daily_update"
        const val DATA_CHANGE_WORK_NAME = "schedule_widget_data_change"
    }
}