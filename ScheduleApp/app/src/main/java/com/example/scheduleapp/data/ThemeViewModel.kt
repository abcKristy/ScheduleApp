package com.example.scheduleapp.data

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

val LocalThemeViewModel = staticCompositionLocalOf<ThemeViewModel?> { null }

class ThemeViewModel : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun initializeTheme(context: Context) {
        viewModelScope.launch {
            val savedTheme = PreferencesManager.getDarkTheme(context)
            _isDarkTheme.value = savedTheme
        }
    }

    fun toggleTheme(context: Context) {
        viewModelScope.launch {
            val newTheme = !_isDarkTheme.value
            _isDarkTheme.value = newTheme
            PreferencesManager.saveDarkTheme(context, newTheme)
        }
    }

    fun setDarkTheme(context: Context, isDark: Boolean) {
        viewModelScope.launch {
            _isDarkTheme.value = isDark
            PreferencesManager.saveDarkTheme(context, isDark)
        }
    }
}