package com.example.scheduleapp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate

object AppState {
    private var _selectedDate by mutableStateOf<LocalDate?>(LocalDate.now())
    val selectedDate: LocalDate? get() = _selectedDate
    fun setSelectedDate(date: LocalDate?) { _selectedDate = date }

    private var _currentGroup by mutableStateOf<String>("someone")
    val scheduleItems: String get() = _currentGroup
    fun setScheduleItems(name: String) { _currentGroup = name }
}