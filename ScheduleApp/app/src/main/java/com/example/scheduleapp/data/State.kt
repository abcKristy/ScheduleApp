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
        context?.let {
            PreferencesManager.saveCurrentGroup(it, group)
        }
    }

    private var context: Context? = null

    fun initialize(context: Context) {
        this.context = context
        loadSavedData(context)
    }

    private fun loadSavedData(context: Context) {
        _currentGroup = PreferencesManager.getCurrentGroup(context)

        val history = PreferencesManager.getSearchHistory(context)
        SearchHistoryManager.initialize(history)

        _userName = PreferencesManager.getUserName(context)
        _userGroup = PreferencesManager.getUserGroup(context)
        _userEmail = PreferencesManager.getUserEmail(context)
    }

    private var _userName by mutableStateOf<String>("Настройте параметры профиля")
    val userName: String get() = _userName
    fun setUserName(name: String) {
        _userName = name
        context?.let {
            PreferencesManager.saveUserName(it, name)
        }
    }

    private var _userGroup by mutableStateOf<String>("не задано")
    val userGroup: String get() = _userGroup
    fun setUserGroup(group: String) {
        _userGroup = group
        context?.let {
            PreferencesManager.saveUserGroup(it, group)
        }
    }

    private var _userEmail by mutableStateOf<String>("не задано")
    val userEmail: String get() = _userEmail
    fun setUserEmail(email: String) {
        _userEmail = email
        context?.let {
            PreferencesManager.saveUserEmail(it, email)
        }
    }
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