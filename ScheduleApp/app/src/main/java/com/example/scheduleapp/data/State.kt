// AppState.kt
package com.example.scheduleapp.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate

@SuppressLint("StaticFieldLeak")
object AppState {
    private var _selectedDate by mutableStateOf<LocalDate?>(LocalDate.now())
    val selectedDate: LocalDate? get() = _selectedDate
    fun setSelectedDate(date: LocalDate?) { _selectedDate = date }

    private var _currentGroup by mutableStateOf<String>("ИКБО-11-23")
    val currentGroup: String get() = _currentGroup
    fun setCurrentGroup(group: String) {
        _currentGroup = group
        // Автоматически сохраняем при изменении
        context?.let {
            PreferencesManager.saveCurrentGroup(it, group)
        }
    }

    // Добавляем контекст для сохранения
    private var context: Context? = null

    fun initialize(context: Context) {
        this.context = context
        loadSavedData(context)
    }

    private fun loadSavedData(context: Context) {
        // Загружаем текущую группу
        _currentGroup = PreferencesManager.getCurrentGroup(context)

        // Загружаем историю поиска
        val history = PreferencesManager.getSearchHistory(context)
        SearchHistoryManager.initialize(history)
    }

    // ... остальной существующий код без изменений ...
    private var _scheduleItems by mutableStateOf<List<ScheduleItem>>(emptyList())
    val scheduleItems: List<ScheduleItem> get() = _scheduleItems
    fun setScheduleItems(items: List<ScheduleItem>) { _scheduleItems = items }

    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading
    fun setLoading(loading: Boolean) { _isLoading = loading }

    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage
    fun setErrorMessage(message: String?) { _errorMessage = message }
}