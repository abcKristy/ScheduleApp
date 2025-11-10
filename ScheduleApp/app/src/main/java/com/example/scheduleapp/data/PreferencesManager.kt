// PreferencesManager.kt
package com.example.scheduleapp.data

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private const val PREFS_NAME = "schedule_app_prefs"
    private const val KEY_CURRENT_GROUP = "current_group"
    private const val KEY_SEARCH_HISTORY = "search_history"
    private const val HISTORY_SEPARATOR = "|||"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveCurrentGroup(context: Context, group: String) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_CURRENT_GROUP, group)
            apply()
        }
    }

    fun saveSearchHistory(context: Context, history: List<String>) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_SEARCH_HISTORY, history.joinToString(HISTORY_SEPARATOR))
            apply()
        }
    }

    fun getCurrentGroup(context: Context): String {
        return getSharedPreferences(context).getString(KEY_CURRENT_GROUP, "ИКБО-11-23") ?: "ИКБО-11-23"
    }

    fun getSearchHistory(context: Context): List<String> {
        val historyString = getSharedPreferences(context).getString(KEY_SEARCH_HISTORY, "") ?: ""
        return if (historyString.isBlank()) {
            emptyList()
        } else {
            historyString.split(HISTORY_SEPARATOR)
        }
    }
}