package com.example.scheduleapp.widgets

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scheduleapp.data.state.AppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker для фонового обновления данных виджета
 * Обновляет расписание ежедневно и при изменении данных
 */
class ScheduleWidgetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting widget data update...")

            // Инициализируем AppState если нужно
            if (AppState.repository == null) {
                Log.d(TAG, "Initializing AppState...")
                AppState.initialize(applicationContext)
            }

            // Проверяем, есть ли группа для отображения
            val currentGroup = AppState.currentGroup
            if (currentGroup.isBlank() || currentGroup == " ") {
                Log.w(TAG, "No group selected for widget")
                // Все равно обновляем виджет, чтобы показать сообщение "Выберите группу"
                ScheduleWidget().updateAll(applicationContext)
                return@withContext Result.success()
            }

            // Получаем актуальные данные для виджета
            Log.d(TAG, "Getting widget data for group: $currentGroup")
            val widgetData = WidgetDataManager.getWidgetData(applicationContext)

            Log.d(TAG, "Widget data retrieved: ${widgetData.scheduleItems.size} items, error: ${widgetData.error}")

            // Обновляем все виджеты
            ScheduleWidget().updateAll(applicationContext)

            Log.d(TAG, "Widget update completed successfully")
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget data", e)

            // Даже при ошибке пытаемся обновить виджет, чтобы показать состояние ошибки
            try {
                ScheduleWidget().updateAll(applicationContext)
            } catch (updateError: Exception) {
                Log.e(TAG, "Failed to update widget after error", updateError)
            }

            // Используем retry() для временных ошибок, failure() для критических
            if (isTemporaryError(e)) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * Определяет, является ли ошибка временной (сетевые проблемы и т.д.)
     */
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

        // Константы для планирования обновлений
        const val DAILY_UPDATE_WORK_NAME = "schedule_widget_daily_update"
        const val DATA_CHANGE_WORK_NAME = "schedule_widget_data_change"
    }
}