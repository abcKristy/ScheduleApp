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

class ScheduleWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = ScheduleWidget()

    companion object {
        private const val TAG = "ScheduleWidgetReceiver"
        const val WIDGET_UPDATE_WORK_NAME = "schedule_widget_update"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d(TAG, "onUpdate: Widget updated for IDs: ${appWidgetIds.joinToString()}")

        scheduleImmediateWidgetUpdate(context)

        scheduleDailyWidgetUpdate(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d(TAG, "onEnabled: Widget enabled")
        scheduleDailyWidgetUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.d(TAG, "onDisabled: Widget disabled")
        cancelWidgetUpdates(context)
    }

    private fun scheduleDailyWidgetUpdate(context: Context) {
        try {
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
                    ExistingPeriodicWorkPolicy.KEEP,
                    dailyWorkRequest
                )

            Log.d(TAG, "Daily widget update scheduled with initial delay: ${initialDelay}min")
        } catch (e: Exception) {
            Log.e(TAG, "scheduleDailyWidgetUpdate: Error scheduling daily update", e)
        }
    }

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

fun scheduleImmediateWidgetUpdate(context: Context) {
    try {
        val immediateWorkRequest = OneTimeWorkRequestBuilder<ScheduleWidgetWorker>()
            .setInitialDelay(0, TimeUnit.MINUTES)
            .addTag(ScheduleWidgetWorker.DATA_CHANGE_WORK_NAME)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                ScheduleWidgetWorker.DATA_CHANGE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                immediateWorkRequest
            )

        Log.d("ScheduleWidget", "Immediate widget update scheduled")
    } catch (e: Exception) {
        Log.e("ScheduleWidget", "scheduleImmediateWidgetUpdate: Error scheduling immediate update", e)
    }
}