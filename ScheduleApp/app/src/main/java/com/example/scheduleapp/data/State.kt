package com.example.scheduleapp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate

object AppState {
    private var _selectedDate by mutableStateOf<LocalDate?>(LocalDate.now())
    val selectedDate: LocalDate? get() = _selectedDate
    fun setSelectedDate(date: LocalDate?) { _selectedDate = date }

    private var _currentGroup by mutableStateOf<String>("ИКБО-11-23")
    val currentGroup: String get() = _currentGroup
    fun setCurrentGroup(group: String) { _currentGroup = group }

    // Добавляем состояние для хранения расписания
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