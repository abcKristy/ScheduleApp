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

    /**
    * Получает данные для виджета на 14 дней вперед
    */
    fun getWidgetData(context: Context): WidgetData {
        return try {
            android.util.Log.d("WidgetDataManager", "=== GETTING WIDGET DATA ===")

            // Инициализируем AppState если нужно
            if (AppState.repository == null) {
                AppState.initialize(context)
            }

            val currentGroup = AppState.currentGroup
            android.util.Log.d("WidgetDataManager", "Current group: '$currentGroup'")

            if (currentGroup.isBlank() || currentGroup == " ") {
                android.util.Log.w("WidgetDataManager", "No group selected")
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

            // Получаем расписание
            val scheduleItems = getScheduleForWidget(context, currentGroup)
            android.util.Log.d("WidgetDataManager", "Retrieved ${scheduleItems.size} total schedule items")

            // Фильтруем на 14 дней вперед
            val filteredItems = filterScheduleFor14Days(scheduleItems)
            android.util.Log.d("WidgetDataManager", "Filtered to ${filteredItems.size} items for 14-day range")

            // Группируем по датам
            val scheduleByDate = groupScheduleByDate(filteredItems)
            android.util.Log.d("WidgetDataManager", "Grouped into ${scheduleByDate.size} days with lessons")

            // Получаем текущее состояние скролла
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

    /**
     * Получает расписание для группы
     */
    private fun getScheduleForWidget(context: Context, group: String): List<ScheduleItem> {
        return getTestScheduleItems()
//        return try {
//            // Пытаемся получить из AppState
//            val stateItems = AppState.scheduleItems
//            if (stateItems.isNotEmpty()) {
//                Log.d(TAG, "Using ${stateItems.size} items from AppState")
//                return stateItems
//            }
//
//            // Если в AppState нет, пробуем из БД
//            val repository = AppState.repository
//            if (repository != null) {
//                // Запускаем в синхронном режиме (осторожно с контекстом)
//                val dbItems = runCatching {
//                    // В реальном коде это должно быть suspend, но для виджета упрощаем
//                    // Здесь нужно асинхронное получение данных
//                    emptyList<ScheduleItem>()
//                }.getOrElse { emptyList() }
//
//                if (dbItems.isNotEmpty()) {
//                    Log.d(TAG, "Using ${dbItems.size} items from database")
//                    return dbItems
//                }
//            }
//
//            Log.w(TAG, "No schedule items found in AppState or database")
//            emptyList()
//
//        } catch (e: Exception) {
//            Log.e(TAG, "Error getting schedule for widget", e)
//            emptyList()
//        }
    }

    /**
     * Фильтрует расписание на 14 дней вперед от текущей даты
     */
    private fun filterScheduleFor14Days(items: List<ScheduleItem>): List<ScheduleItem> {
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(13)

        Log.d(TAG, "Filtering schedule from $startDate to $endDate")

        val filtered = items.filter { scheduleItem ->
            val itemDate = scheduleItem.startTime.toLocalDate()
            val isInRange = !itemDate.isBefore(startDate) && !itemDate.isAfter(endDate)

            isInRange
        }

        // Логируем детали
        val itemsByDate = filtered.groupBy { it.startTime.toLocalDate() }
        itemsByDate.forEach { (date, dayItems) ->
            Log.d(TAG, "Date $date: ${dayItems.size} items")
        }

        Log.d(TAG, "Filter result: ${filtered.size} of ${items.size} items in range")
        return filtered
    }

    /**
     * Группирует занятия по датам
     */
    private fun groupScheduleByDate(items: List<ScheduleItem>): Map<LocalDate, List<ScheduleItem>> {
        return items.groupBy { it.startTime.toLocalDate() }
            .toSortedMap() // Сортируем по дате
    }

    /**
     * Вспомогательная функция для конвертации LocalDateTime в LocalDate
     */
    private fun LocalDateTime.toLocalDate(): LocalDate {
        return this.toLocalDate()
    }
}

/**
 * Data class для хранения данных виджета
 */
data class WidgetData(
    val currentGroup: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val scheduleByDate: Map<LocalDate, List<ScheduleItem>>,
    val isLoading: Boolean,
    val error: String?,
    val scrollOffset: Int = 0
)

/**
 * Менеджер состояния скролла для виджета с сохранением в SharedPreferences
 */
/**
 * Менеджер состояния скролла для виджета с сохранением в SharedPreferences
 */
object WidgetScrollManager {
    private const val PREFS_NAME = "widget_scroll"
    private const val SCROLL_OFFSET_KEY = "scroll_offset"
    private const val DAYS_PER_PAGE = 3 // Дней на одной "странице"

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
        // МОЖНО скроллить вниз если текущий offset МЕНЬШЕ чем (totalDays - DAYS_PER_PAGE)
        val canScroll = currentOffset < (totalDays - DAYS_PER_PAGE)
        android.util.Log.d("WidgetScrollManager", "Can scroll down: $canScroll (offset: $currentOffset, total: $totalDays, need: ${currentOffset < (totalDays - DAYS_PER_PAGE)})")
        return canScroll
    }

    fun canScrollUp(context: Context): Boolean {
        val currentOffset = getCurrentOffset(context)
        // МОЖНО скроллить вверх если текущий offset БОЛЬШЕ 0
        val canScroll = currentOffset > 0
        android.util.Log.d("WidgetScrollManager", "Can scroll up: $canScroll (offset: $currentOffset)")
        return canScroll
    }
}// Action для прокрутки вверх
class ScrollUpAction : androidx.glance.appwidget.action.ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        android.util.Log.d("ScrollUpAction", "Scroll up action triggered")
        WidgetScrollManager.scrollUp(context)

        // Используем forceUpdate
        val widget = ScheduleWidget()
        widget.forceUpdate(context)

        android.util.Log.d("ScrollUpAction", "Force update completed")
    }
}

// Action для прокрутки вниз
class ScrollDownAction : androidx.glance.appwidget.action.ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        android.util.Log.d("ScrollDownAction", "Scroll down action triggered")
        val widgetData = WidgetDataManager.getWidgetData(context)
        val totalDays = widgetData.scheduleByDate.size
        WidgetScrollManager.scrollDown(context, totalDays)

        // Используем forceUpdate
        val widget = ScheduleWidget()
        widget.forceUpdate(context)

        android.util.Log.d("ScrollDownAction", "Force update completed")
    }
}
/**
 * Временная функция для генерации тестовых данных
 */
private fun getTestScheduleItems(): List<ScheduleItem> {
    val items = mutableListOf<ScheduleItem>()
    val now = LocalDateTime.now()

    // Генерируем тестовые занятия на 14 дней
    for (day in 0..13) {
        val date = now.plusDays(day.toLong())

        // Добавляем 2-3 занятия в день
        for (lesson in 0..2) {
            val startTime = date.withHour(9 + lesson * 2).withMinute(0)
            val endTime = startTime.plusHours(1).plusMinutes(30)

            items.add(ScheduleItem(
                discipline = "Дисциплина ${day + 1}-${lesson + 1}",
                lessonType = if (lesson % 2 == 0) "Лекция" else "Практика",
                startTime = startTime,
                endTime = endTime,
                room = "Ауд. ${100 + day + lesson}",
                teacher = "Преподаватель ${day + 1}",
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