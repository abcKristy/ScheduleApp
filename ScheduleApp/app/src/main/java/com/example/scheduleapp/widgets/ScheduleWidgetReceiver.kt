package com.example.scheduleapp.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
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

        // Запускаем немедленное обновление данных
        scheduleImmediateWidgetUpdate(context)

        // Планируем ежедневное обновление
        scheduleDailyWidgetUpdate(context)
    }

    /**
     * Вызывается при первом создании виджета
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d(TAG, "onEnabled: Widget enabled")
        // Планируем ежедневное обновление при первом создании виджета
        scheduleDailyWidgetUpdate(context)
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
     * Планирует ежедневное обновление виджета в 00:01
     */
    private fun scheduleDailyWidgetUpdate(context: Context) {
        try {
            // Вычисляем время до следующего дня 00:01
            val now = java.time.LocalDateTime.now()
            val nextDay = now.plusDays(1).withHour(0).withMinute(1).withSecond(0)
            val initialDelay = java.time.Duration.between(now, nextDay).toMinutes()

            val dailyWorkRequest = PeriodicWorkRequestBuilder<ScheduleWidgetWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(initialDelay, TimeUnit.MINUTES)
                .addTag(ScheduleWidgetWorker.DAILY_UPDATE_WORK_NAME)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    ScheduleWidgetWorker.DAILY_UPDATE_WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP, // ИСПРАВЛЕНО: ExistingPeriodicWorkPolicy вместо ExistingWorkPolicy
                    dailyWorkRequest
                )

            Log.d(TAG, "Daily widget update scheduled with initial delay: ${initialDelay}min")
        } catch (e: Exception) {
            Log.e(TAG, "scheduleDailyWidgetUpdate: Error scheduling daily update", e)
        }
    }

    /**
     * Отмена всех запланированных обновлений
     */
    private fun cancelWidgetUpdates(context: Context) {
        try {
            WorkManager.getInstance(context)
                .cancelUniqueWork(WIDGET_UPDATE_WORK_NAME)
            WorkManager.getInstance(context)
                .cancelUniqueWork(ScheduleWidgetWorker.DAILY_UPDATE_WORK_NAME)
            WorkManager.getInstance(context)
                .cancelUniqueWork(ScheduleWidgetWorker.DATA_CHANGE_WORK_NAME)
            Log.d(TAG, "cancelWidgetUpdates: All widget updates cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "cancelWidgetUpdates: Error cancelling updates", e)
        }
    }
}

/**
 * Запускает немедленное обновление при изменении данных
 */
fun scheduleImmediateWidgetUpdate(context: Context) {
    try {
        val immediateWorkRequest = OneTimeWorkRequestBuilder<ScheduleWidgetWorker>()
            .setInitialDelay(0, TimeUnit.MINUTES)
            .addTag(ScheduleWidgetWorker.DATA_CHANGE_WORK_NAME)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                ScheduleWidgetWorker.DATA_CHANGE_WORK_NAME,
                ExistingWorkPolicy.REPLACE, // ОСТАВЛЯЕМ ExistingWorkPolicy для разовых работ
                immediateWorkRequest
            )

        Log.d("ScheduleWidget", "Immediate widget update scheduled")
    } catch (e: Exception) {
        Log.e("ScheduleWidget", "scheduleImmediateWidgetUpdate: Error scheduling immediate update", e)
    }
}