package com.example.scheduleapp.data.state

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
    private const val KEY_SHOW_EMPTY_LESSONS = "show_empty_lessons"

    private const val KEY_LAST_KNOWN_SEMESTER = "last_known_semester"
    private const val KEY_LAST_SEMESTER_CHECK = "last_semester_check"
    private const val KEY_LAST_CACHE_CLEANUP = "last_cache_cleanup"
    private const val KEY_PENDING_RETRY_GROUPS = "pending_retry_groups"

    private const val KEY_API_HAS_NEW_SEMESTER = "api_has_new_semester"
    private const val KEY_FAILED_ATTEMPTS_COUNT = "failed_attempts_count"
    private const val KEY_LAST_FAILED_ATTEMPT = "last_failed_attempt"
    private const val KEY_AUTO_UPDATE_CACHE = "auto_update_cache"
    private const val KEY_CACHE_TTL_DAYS = "cache_ttl_days"

    fun saveShowEmptyLessons(context: Context, showEmpty: Boolean) {
        getSharedPreferences(context).edit().apply {
            putBoolean(KEY_SHOW_EMPTY_LESSONS, showEmpty)
            apply()
        }
    }

    fun getShowEmptyLessons(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_SHOW_EMPTY_LESSONS, true)
    }

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

    fun saveLastKnownSemester(context: Context, semester: String) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_LAST_KNOWN_SEMESTER, semester)
            apply()
        }
    }

    fun getLastKnownSemester(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_LAST_KNOWN_SEMESTER, null)
    }

    fun saveLastSemesterCheck(context: Context, timestamp: Long) {
        getSharedPreferences(context).edit().apply {
            putLong(KEY_LAST_SEMESTER_CHECK, timestamp)
            apply()
        }
    }

    fun getLastSemesterCheck(context: Context): Long {
        return getSharedPreferences(context).getLong(KEY_LAST_SEMESTER_CHECK, 0)
    }

    fun saveLastCacheCleanup(context: Context, timestamp: Long) {
        getSharedPreferences(context).edit().apply {
            putLong(KEY_LAST_CACHE_CLEANUP, timestamp)
            apply()
        }
    }

    fun getLastCacheCleanup(context: Context): Long {
        return getSharedPreferences(context).getLong(KEY_LAST_CACHE_CLEANUP, 0)
    }

    fun savePendingRetryGroups(context: Context, groups: Set<String>) {
        getSharedPreferences(context).edit().apply {
            putStringSet(KEY_PENDING_RETRY_GROUPS, groups)
            apply()
        }
    }

    fun getPendingRetryGroups(context: Context): Set<String> {
        return getSharedPreferences(context).getStringSet(KEY_PENDING_RETRY_GROUPS, emptySet()) ?: emptySet()
    }

    fun addPendingRetryGroup(context: Context, group: String) {
        val current = getPendingRetryGroups(context).toMutableSet()
        current.add(group)
        savePendingRetryGroups(context, current)
    }

    fun removePendingRetryGroup(context: Context, group: String) {
        val current = getPendingRetryGroups(context).toMutableSet()
        current.remove(group)
        savePendingRetryGroups(context, current)
    }

    fun setApiHasNewSemester(context: Context, hasNew: Boolean) {
        getSharedPreferences(context).edit().apply {
            putBoolean(KEY_API_HAS_NEW_SEMESTER, hasNew)
            apply()
        }
    }

    fun getApiHasNewSemester(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_API_HAS_NEW_SEMESTER, true)
    }

    fun incrementFailedAttempts(context: Context): Int {
        val current = getFailedAttemptsCount(context) + 1
        getSharedPreferences(context).edit().apply {
            putInt(KEY_FAILED_ATTEMPTS_COUNT, current)
            putLong(KEY_LAST_FAILED_ATTEMPT, System.currentTimeMillis())
            apply()
        }
        return current
    }

    fun resetFailedAttempts(context: Context) {
        getSharedPreferences(context).edit().apply {
            putInt(KEY_FAILED_ATTEMPTS_COUNT, 0)
            remove(KEY_LAST_FAILED_ATTEMPT)
            apply()
        }
    }

    fun getFailedAttemptsCount(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_FAILED_ATTEMPTS_COUNT, 0)
    }

    fun getLastFailedAttempt(context: Context): Long {
        return getSharedPreferences(context).getLong(KEY_LAST_FAILED_ATTEMPT, 0)
    }

    fun shouldUseExpeditedRetry(context: Context): Boolean {
        val attempts = getFailedAttemptsCount(context)
        return attempts >= 2
    }

    fun getRetryIntervalHours(context: Context): Int {
        val attempts = getFailedAttemptsCount(context)
        return when {
            attempts == 0 -> 168 // 7 дней
            attempts == 1 -> 72  // 3 дня
            attempts == 2 -> 24  // 1 день
            attempts >= 3 -> 6   // 6 часов
            else -> 168
        }
    }
    fun setAutoUpdateCache(context: Context, enabled: Boolean) {
        getSharedPreferences(context).edit().apply {
            putBoolean(KEY_AUTO_UPDATE_CACHE, enabled)
            apply()
        }
    }

    fun isAutoUpdateCacheEnabled(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_AUTO_UPDATE_CACHE, true)
    }

    fun setCacheTtlDays(context: Context, days: Int) {
        getSharedPreferences(context).edit().apply {
            putInt(KEY_CACHE_TTL_DAYS, days)
            apply()
        }
    }

    fun getCacheTtlDays(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_CACHE_TTL_DAYS, 7)
    }
}