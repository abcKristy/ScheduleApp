package com.example.scheduleapp.widgets

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.updateAll
import com.example.scheduleapp.data.entity.ScheduleItem
import com.example.scheduleapp.data.state.AppState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Менеджер данных для виджета
 * Обрабатывает фильтрацию и подготовку данных для отображения
 */
object WidgetDataManager {

    private const val TAG = "WidgetDataManager"

    fun getWidgetData(context: Context): WidgetData {
        return try {
            Log.d("WidgetDataManager", "=== GETTING WIDGET DATA ===")

            if (AppState.repository == null) {
                AppState.initialize(context)
            }

            val currentGroup = AppState.currentGroup
            Log.d("WidgetDataManager", "Current group: '$currentGroup'")

            if (currentGroup.isBlank() || currentGroup == " ") {
                Log.w("WidgetDataManager", "No group selected")
                return WidgetData(
                    currentGroup = "",
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now().plusDays(13),
                    scheduleByDate = emptyMap(),
                    isLoading = false,
                    error = "Выберите группу",
                    scrollOffset = 0
                )
            }

            val scheduleItems = getTestScheduleItems()
            android.util.Log.d("WidgetDataManager", "Retrieved ${scheduleItems.size} total schedule items")

            val filteredItems = filterScheduleFor14Days(scheduleItems)
            android.util.Log.d("WidgetDataManager", "Filtered to ${filteredItems.size} items for 14-day range")

            val scheduleByDate = groupScheduleByDate(filteredItems)
            android.util.Log.d("WidgetDataManager", "Grouped into ${scheduleByDate.size} days with lessons")

            val scrollOffset = WidgetScrollManager.getCurrentOffset(context)
            android.util.Log.d("WidgetDataManager", "Current scroll offset: $scrollOffset")

            WidgetData(
                currentGroup = currentGroup,
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(13),
                scheduleByDate = scheduleByDate,
                isLoading = false,
                error = null,
                scrollOffset = scrollOffset
            )

        } catch (e: Exception) {
            android.util.Log.e("WidgetDataManager", "Error getting widget data", e)
            WidgetData(
                currentGroup = AppState.currentGroup,
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(13),
                scheduleByDate = emptyMap(),
                isLoading = false,
                error = "Ошибка загрузки",
                scrollOffset = 0
            )
        }
    }

    private fun filterScheduleFor14Days(items: List<ScheduleItem>): List<ScheduleItem> {
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(13)

        Log.d(TAG, "Filtering schedule from $startDate to $endDate")

        val filtered = items.filter { scheduleItem ->
            val itemDate = scheduleItem.startTime.toLocalDate()
            val isInRange = !itemDate.isBefore(startDate) && !itemDate.isAfter(endDate)

            isInRange
        }

        val itemsByDate = filtered.groupBy { it.startTime.toLocalDate() }
        itemsByDate.forEach { (date, dayItems) ->
            Log.d(TAG, "Date $date: ${dayItems.size} items")
        }

        Log.d(TAG, "Filter result: ${filtered.size} of ${items.size} items in range")
        return filtered
    }

    private fun groupScheduleByDate(items: List<ScheduleItem>): Map<LocalDate, List<ScheduleItem>> {
        return items.groupBy { it.startTime.toLocalDate() }
            .toSortedMap()
    }

    private fun LocalDateTime.toLocalDate(): LocalDate {
        return this.toLocalDate()
    }
}

data class WidgetData(
    val currentGroup: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val scheduleByDate: Map<LocalDate, List<ScheduleItem>>,
    val isLoading: Boolean,
    val error: String?,
    val scrollOffset: Int = 0
)

object WidgetScrollManager {
    private const val PREFS_NAME = "widget_scroll"
    private const val SCROLL_OFFSET_KEY = "scroll_offset"
    private const val DAYS_PER_PAGE = 3

    fun getCurrentOffset(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val offset = prefs.getInt(SCROLL_OFFSET_KEY, 0)
        android.util.Log.d("WidgetScrollManager", "Getting offset: $offset")
        return offset
    }

    fun scrollDown(context: Context, totalDays: Int): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentOffset = prefs.getInt(SCROLL_OFFSET_KEY, 0)
        val maxOffset = maxOf(0, totalDays - DAYS_PER_PAGE)
        val newOffset = minOf(currentOffset + DAYS_PER_PAGE, maxOffset)

        prefs.edit().putInt(SCROLL_OFFSET_KEY, newOffset).apply()
        android.util.Log.d("WidgetScrollManager", "Scrolled down: $currentOffset -> $newOffset (total: $totalDays, max: $maxOffset)")
        return newOffset
    }

    fun scrollUp(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentOffset = prefs.getInt(SCROLL_OFFSET_KEY, 0)
        val newOffset = maxOf(0, currentOffset - DAYS_PER_PAGE)

        prefs.edit().putInt(SCROLL_OFFSET_KEY, newOffset).apply()
        android.util.Log.d("WidgetScrollManager", "Scrolled up: $currentOffset -> $newOffset")
        return newOffset
    }

    fun reset(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(SCROLL_OFFSET_KEY, 0).apply()
        android.util.Log.d("WidgetScrollManager", "Reset scroll to 0")
    }

    fun canScrollDown(context: Context, totalDays: Int): Boolean {
        val currentOffset = getCurrentOffset(context)
        val canScroll = currentOffset < (totalDays - DAYS_PER_PAGE)
        android.util.Log.d("WidgetScrollManager", "Can scroll down: $canScroll (offset: $currentOffset, total: $totalDays, need: ${currentOffset < (totalDays - DAYS_PER_PAGE)})")
        return canScroll
    }

    fun canScrollUp(context: Context): Boolean {
        val currentOffset = getCurrentOffset(context)
        val canScroll = currentOffset > 0
        android.util.Log.d("WidgetScrollManager", "Can scroll up: $canScroll (offset: $currentOffset)")
        return canScroll
    }
}

private fun getTestScheduleItems(): List<ScheduleItem> {
    val items = mutableListOf<ScheduleItem>()
    val now = LocalDateTime.now()

    for (day in 0..13) {
        val date = now.plusDays(day.toLong())

        for (lesson in 0..2) {
            val startTime = date.withHour(9 + lesson * 2).withMinute(0)
            val endTime = startTime.plusHours(1).plusMinutes(30)

            items.add(ScheduleItem(
                discipline = "Дисциплина ${day + 1}-${lesson + 1}",
                lessonType = if (lesson % 2 == 0) "Лекция" else "Практика",
                startTime = startTime,
                endTime = endTime,
                rooms = listOf("Ауд. ${100 + day + lesson}"),
                teachers = listOf("Преподаватель ${day + 1}"),
                groups = listOf("ИКБО-60-23"),
                groupsSummary = "ИКБО-60-23",
                description = "Тестовое занятие",
                recurrence = null,
                exceptions = emptyList()
            ))
        }
    }

    Log.d(TAG, "Generated ${items.size} test items")
    return items
}