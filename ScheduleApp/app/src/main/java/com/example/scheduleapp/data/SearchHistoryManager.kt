package com.example.scheduleapp.data

import androidx.compose.runtime.mutableStateListOf

object SearchHistoryManager {
    private const val MAX_HISTORY_SIZE = 10
    private val _historyList = mutableStateListOf<String>()
    val historyList: List<String> get() = _historyList

    fun addToHistory(query: String) {
        // Удаляем если уже есть (для обновления позиции)
        _historyList.remove(query)
        // Добавляем в начало
        _historyList.add(0, query)

        // Ограничиваем размер
        if (_historyList.size > MAX_HISTORY_SIZE) {
            _historyList.removeAt(_historyList.size - 1)
        }
    }

    fun clearHistory() {
        _historyList.clear()
    }
}