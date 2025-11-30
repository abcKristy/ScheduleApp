package com.example.scheduleapp.widgets

import android.content.Context
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.data.entity.ScheduleItem
import com.example.scheduleapp.logic.createScheduleDayForDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Мост между виджетом и основным приложением
 * Обеспечивает доступ к данным расписания для виджета
 */
object WidgetDataManager {

    /**
     * Получает данные для отображения в виджете
     */
    suspend fun getWidgetData(context: Context): WidgetData {
        return try {
            // Инициализируем AppState если нужно
            if (AppState.repository == null) {
                AppState.initialize(context)
            }

            val currentGroup = AppState.currentGroup
            val currentDate = AppState.selectedDate ?: LocalDate.now()

            // Получаем расписание для текущей группы
            val scheduleItems = if (currentGroup.isNotBlank() && currentGroup != " ") {
                AppState.repository?.getSchedule(currentGroup) ?: emptyList()
            } else {
                emptyList()
            }

            // Фильтруем занятия по текущей дате
            val filteredItems = filterScheduleForDate(scheduleItems, currentDate)

            // Сортируем по времени начала
            val sortedItems = filteredItems.sortedBy { it.startTime }

            WidgetData(
                currentGroup = currentGroup,
                currentDate = currentDate,
                scheduleItems = sortedItems,
                isLoading = false,
                error = null
            )
        } catch (e: Exception) {
            WidgetData(
                currentGroup = AppState.currentGroup,
                currentDate = LocalDate.now(),
                scheduleItems = emptyList(),
                isLoading = false,
                error = "Ошибка загрузки данных"
            )
        }
    }

    /**
     * Фильтрует расписание по указанной дате
     */
    private fun filterScheduleForDate(
        scheduleItems: List<ScheduleItem>,
        targetDate: LocalDate
    ): List<ScheduleItem> {
        return scheduleItems.filter { scheduleItem ->
            val itemDate = scheduleItem.startTime.toLocalDate()

            // Проверяем повторяющиеся занятия
            val recurrence = scheduleItem.recurrence
            if (recurrence != null) {
                // Для повторяющихся занятий используем существующую логику фильтрации
                shouldShowOnDate(scheduleItem, targetDate)
            } else {
                // Для обычных занятий - только точное совпадение даты
                itemDate == targetDate
            }
        }
    }

    /**
     * Проверяет, должно ли занятие отображаться на указанную дату
     * (адаптированная версия из ScheduleFilter.kt)
     */
    private fun shouldShowOnDate(scheduleItem: ScheduleItem, targetDate: LocalDate): Boolean {
        val itemDate = scheduleItem.startTime.toLocalDate()
        val itemDayOfWeek = scheduleItem.startTime.dayOfWeek
        val targetDayOfWeek = targetDate.dayOfWeek

        // Проверяем исключения
        if (scheduleItem.exceptions.any { it == targetDate }) {
            return false
        }

        // Если дата совпадает точно с оригинальной датой занятия
        if (itemDate == targetDate) {
            return true
        }

        // Проверяем, совпадает ли день недели
        if (itemDayOfWeek != targetDayOfWeek) {
            return false
        }

        val recurrence = scheduleItem.recurrence ?: return false

        // Проверяем правила повторения
        return isDateInRecurrence(scheduleItem, targetDate)
    }

    /**
     * Проверяет, входит ли дата в правило повторения
     */
    private fun isDateInRecurrence(scheduleItem: ScheduleItem, targetDate: LocalDate): Boolean {
        val recurrence = scheduleItem.recurrence ?: return false
        val startDate = scheduleItem.startTime.toLocalDate()

        if (targetDate.isBefore(startDate)) {
            return false
        }

        if (recurrence.until != null && targetDate.atStartOfDay().isAfter(recurrence.until)) {
            return false
        }

        val weeksBetween = java.time.temporal.ChronoUnit.WEEKS.between(startDate, targetDate)
        if (recurrence.interval != null && recurrence.interval > 0) {
            if (weeksBetween % recurrence.interval != 0L) {
                return false
            }
        }

        return when (recurrence.frequency?.uppercase()) {
            "WEEKLY" -> true
            else -> true
        }
    }

    /**
     * Форматирует дату для отображения в виджете
     */
    fun formatDateForWidget(date: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
        val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale("ru"))

        val dateString = date.format(dateFormatter)
        val dayOfWeekString = date.format(dayOfWeekFormatter)

        return "$dateString - ${dayOfWeekString.lowercase()}"
    }

    /**
     * Форматирует время занятия для отображения
     */
    fun formatLessonTime(scheduleItem: ScheduleItem): String {
        return "${scheduleItem.formattedStartTime}-${scheduleItem.formattedEndTime}"
    }

    /**
     * Форматирует тип занятия для отображения
     */
    fun formatLessonType(lessonType: String): String {
        return when (lessonType.uppercase()) {
            "LECTURE", "LK", "ЛЕКЦИЯ" -> "Лекция"
            "PRACTICE", "PR", "ПРАКТИКА" -> "Практика"
            "LAB", "LABORATORY", "ЛАБОРАТОРНАЯ" -> "Лабораторная"
            "SEMINAR", "SEM", "СЕМИНАР" -> "Семинар"
            "EMPTY" -> "Окно"
            else -> lessonType
        }
    }

    /**
     * Проверяет, есть ли валидные данные для отображения
     */
    fun hasValidData(): Boolean {
        return AppState.currentGroup.isNotBlank() && AppState.currentGroup != " "
    }

    /**
     * Получает текущую группу
     */
    fun getCurrentGroup(): String = AppState.currentGroup
}

/**
 * Data class для хранения данных виджета
 */
data class WidgetData(
    val currentGroup: String,
    val currentDate: LocalDate,
    val scheduleItems: List<ScheduleItem>,
    val isLoading: Boolean,
    val error: String?
)