package com.example.scheduleapp.widgets

import android.content.Context
import com.example.scheduleapp.data.state.AppState
import com.example.scheduleapp.data.entity.ScheduleItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Мост между виджетом и основным приложением
 * Обеспечивает доступ к данным расписания для виджета
 */
object WidgetDataManager {

    /**
     * Получает данные для отображения в виджете (14 дней вперед)
     */
    suspend fun getWidgetData(context: Context): WidgetData {
        return try {
            // Инициализируем AppState если нужно
            if (AppState.repository == null) {
                AppState.initialize(context)
            }

            val currentGroup = AppState.currentGroup
            val startDate = AppState.selectedDate ?: LocalDate.now()

            // Получаем расписание для текущей группы
            val scheduleItems = if (currentGroup.isNotBlank() && currentGroup != " ") {
                AppState.repository?.getSchedule(currentGroup) ?: emptyList()
            } else {
                emptyList()
            }

            // Фильтруем занятия на ближайшие 14 дней
            val endDate = startDate.plusDays(13) // +13 дней = 14 дней всего
            val filteredItems = filterScheduleForDateRange(scheduleItems, startDate, endDate)

            // Группируем по датам и сортируем
            val groupedByDate = filteredItems
                .groupBy { it.startTime.toLocalDate() }
                .toSortedMap()

            WidgetData(
                currentGroup = currentGroup,
                startDate = startDate,
                endDate = endDate,
                scheduleByDate = groupedByDate,
                isLoading = false,
                error = null
            )
        } catch (e: Exception) {
            WidgetData(
                currentGroup = AppState.currentGroup,
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(13),
                scheduleByDate = emptyMap(),
                isLoading = false,
                error = "Ошибка загрузки данных"
            )
        }
    }

    /**
     * Фильтрует расписание по диапазону дат (14 дней)
     */
    private fun filterScheduleForDateRange(
        scheduleItems: List<ScheduleItem>,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ScheduleItem> {
        return scheduleItems.filter { scheduleItem ->
            val itemDate = scheduleItem.startTime.toLocalDate()

            // Проверяем входит ли дата в диапазон
            if (itemDate.isBefore(startDate) || itemDate.isAfter(endDate)) {
                return@filter false
            }

            // Проверяем повторяющиеся занятия
            val recurrence = scheduleItem.recurrence
            if (recurrence != null) {
                // Для повторяющихся занятий используем существующую логику фильтрации
                shouldShowOnDate(scheduleItem, itemDate)
            } else {
                // Для обычных занятий - только точное совпадение даты
                true
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
     * Форматирует диапазон дат для заголовка
     */
    fun formatDateRangeForWidget(startDate: LocalDate, endDate: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))
        val start = startDate.format(dateFormatter)
        val end = endDate.format(dateFormatter)
        return "$start - $end"
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
 * Data class для хранения данных виджета (14 дней)
 */
data class WidgetData(
    val currentGroup: String,
    val startDate: LocalDate,      // Начальная дата диапазона
    val endDate: LocalDate,        // Конечная дата диапазона (+13 дней)
    val scheduleByDate: Map<LocalDate, List<ScheduleItem>>, // Сгруппировано по датам
    val isLoading: Boolean,
    val error: String?
)