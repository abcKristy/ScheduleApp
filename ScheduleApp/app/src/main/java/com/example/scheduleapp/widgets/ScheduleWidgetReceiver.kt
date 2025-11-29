package com.example.scheduleapp.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Receiver для виджета расписания
 * Обрабатывает системные события и обновления виджета
 */
class ScheduleWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = ScheduleWidget()

    companion object {
        private const val TAG = "ScheduleWidgetReceiver"
        const val WIDGET_UPDATE_WORK_NAME = "schedule_widget_update"
    }

    /**
     * Вызывается при обновлении виджета (каждые 30 минут по умолчанию)
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d(TAG, "onUpdate: Widget updated for IDs: ${appWidgetIds.joinToString()}")

        // Запускаем фоновое обновление данных
        scheduleWidgetUpdate(context)
    }

    /**
     * Вызывается при первом создании виджета
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d(TAG, "onEnabled: Widget enabled")
    }

    /**
     * Вызывается при удалении последнего экземпляра виджета
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.d(TAG, "onDisabled: Widget disabled")

        // Останавливаем все фоновые работы
        cancelWidgetUpdates(context)
    }

    /**
     * Планирование разового обновления виджета
     */
    private fun scheduleWidgetUpdate(context: Context) {
        try {
            val updateWorkRequest = OneTimeWorkRequestBuilder<ScheduleWidgetWorker>()
                .setInitialDelay(1, TimeUnit.MINUTES)
                .addTag(WIDGET_UPDATE_WORK_NAME)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WIDGET_UPDATE_WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    updateWorkRequest
                )

            Log.d(TAG, "scheduleWidgetUpdate: Widget update scheduled")
        } catch (e: Exception) {
            Log.e(TAG, "scheduleWidgetUpdate: Error scheduling update", e)
        }
    }

    /**
     * Отмена всех запланированных обновлений
     */
    private fun cancelWidgetUpdates(context: Context) {
        try {
            WorkManager.getInstance(context)
                .cancelUniqueWork(WIDGET_UPDATE_WORK_NAME)
            Log.d(TAG, "cancelWidgetUpdates: All widget updates cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "cancelWidgetUpdates: Error cancelling updates", e)
        }
    }
}