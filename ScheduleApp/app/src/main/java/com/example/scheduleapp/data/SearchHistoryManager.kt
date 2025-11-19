// SearchHistoryManager.kt
package com.example.scheduleapp.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf

object SearchHistoryManager {
    private const val MAX_HISTORY_SIZE = 10
    private val _historyList = mutableStateListOf<String>()
    val historyList: List<String> get() = _historyList

    fun initialize(history: List<String>) {
        _historyList.clear()
        _historyList.addAll(history)
    }

    fun addToHistory(context: Context, query: String) {
        // Удаляем если уже есть (для обновления позиции)
        _historyList.remove(query)
        // Добавляем в начало
        _historyList.add(0, query)

        // Ограничиваем размер
        if (_historyList.size > MAX_HISTORY_SIZE) {
            _historyList.removeAt(_historyList.size - 1)
        }

        // Сохраняем в SharedPreferences
        PreferencesManager.saveSearchHistory(context, _historyList)
    }

    fun clearHistory(context: Context) {
        _historyList.clear()
        PreferencesManager.saveSearchHistory(context, emptyList())
    }

    fun removeFromHistory(context: Context, query: String) {
        _historyList.remove(query)
        PreferencesManager.saveSearchHistory(context, _historyList)
    }
}