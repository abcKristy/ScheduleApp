package com.example.scheduleapp.data.state

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
        _historyList.remove(query)
        _historyList.add(0, query)

        if (_historyList.size > MAX_HISTORY_SIZE) {
            _historyList.removeAt(_historyList.size - 1)
        }

        PreferencesManager.saveSearchHistory(context, _historyList)
    }

    fun removeFromHistory(context: Context, query: String) {
        _historyList.remove(query)
        PreferencesManager.saveSearchHistory(context, _historyList)
    }
}