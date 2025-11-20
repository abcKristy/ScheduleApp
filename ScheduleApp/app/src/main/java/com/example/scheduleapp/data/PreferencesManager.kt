package com.example.scheduleapp.data

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private const val PREFS_NAME = "schedule_app_prefs"
    private const val KEY_CURRENT_GROUP = "current_group"
    private const val KEY_SEARCH_HISTORY = "search_history"
    private const val HISTORY_SEPARATOR = "|||"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_GROUP = "user_group"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_AVATAR = "user_avatar"
    private const val KEY_DARK_THEME = "dark_theme"

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
        return getSharedPreferences(context).getString(KEY_CURRENT_GROUP, "") ?: ""
    }

    fun getSearchHistory(context: Context): List<String> {
        val historyString = getSharedPreferences(context).getString(KEY_SEARCH_HISTORY, "") ?: ""
        return if (historyString.isBlank()) {
            emptyList()
        } else {
            historyString.split(HISTORY_SEPARATOR)
        }
    }

    fun saveUserName(context: Context, name: String) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_USER_NAME, name)
            apply()
        }
    }

    fun saveUserGroup(context: Context, group: String) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_USER_GROUP, group)
            apply()
        }
    }

    fun saveUserEmail(context: Context, email: String) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    fun getUserName(context: Context): String {
        return getSharedPreferences(context).getString(KEY_USER_NAME, "Задайте в настройках") ?: "Задайте в настройках"
    }

    fun getUserGroup(context: Context): String {
        return getSharedPreferences(context).getString(KEY_USER_GROUP, "не задано") ?: "не задано"
    }

    fun getUserEmail(context: Context): String {
        return getSharedPreferences(context).getString(KEY_USER_EMAIL, "не задано") ?: "не задано"
    }

    fun saveUserAvatar(context: Context, avatarPath: String?) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_USER_AVATAR, avatarPath)
            apply()
        }
    }

    fun getUserAvatar(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_AVATAR, null)
    }

    fun saveDarkTheme(context: Context, isDarkTheme: Boolean) {
        getSharedPreferences(context).edit().apply {
            putBoolean(KEY_DARK_THEME, isDarkTheme)
            apply()
        }
    }

    fun getDarkTheme(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_DARK_THEME, false)
    }
}