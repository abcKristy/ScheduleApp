package com.example.scheduleapp.widgets

import android.content.Context
import android.util.Log

/**
 * Вспомогательный класс для обновления виджета из разных частей приложения
 */
object WidgetUpdateHelper {

    private const val TAG = "WidgetUpdateHelper"

    /**
     * Запускает немедленное обновление виджета
     */
    fun scheduleImmediateUpdate(context: Context) {
        try {
            scheduleImmediateWidgetUpdate(context)
            Log.d(TAG, "Widget update scheduled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule widget update", e)
        }
    }

    /**
     * Запускает обновление при изменении группы
     */
    fun scheduleUpdateOnGroupChange(context: Context) {
        Log.d(TAG, "Scheduling widget update due to group change")
        scheduleImmediateUpdate(context)
    }

    /**
     * Запускает обновление при загрузке новых данных
     */
    fun scheduleUpdateOnDataChange(context: Context) {
        Log.d(TAG, "Scheduling widget update due to data change")
        scheduleImmediateUpdate(context)
    }

    /**
     * Запускает обновление при изменении даты
     */
    fun scheduleUpdateOnDateChange(context: Context) {
        Log.d(TAG, "Scheduling widget update due to date change")
        scheduleImmediateUpdate(context)
    }
}