package com.example.scheduleapp.data.state

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.scheduleapp.data.entity.ScheduleItem
import com.example.scheduleapp.data.database.ScheduleDatabase
import com.example.scheduleapp.data.database.ScheduleRepository
import com.example.scheduleapp.util.NetworkMonitor
import com.example.scheduleapp.util.SemesterUtils
import com.example.scheduleapp.widgets.WidgetUpdateHelper
import com.example.scheduleapp.workers.CacheCleanupWorker
import com.example.scheduleapp.workers.PeriodicCacheUpdateWorker
import com.example.scheduleapp.workers.SemesterAvailabilityWorker
import com.example.scheduleapp.workers.SemesterCheckWorker
import java.time.LocalDate

@SuppressLint("StaticFieldLeak")
object AppState {


    private var _lastForegroundTime by mutableStateOf<Long>(0)
    private var _lastKnownSemester by mutableStateOf<String?>(null)
    private var _selectedDate by mutableStateOf<LocalDate?>(LocalDate.now())
    val selectedDate: LocalDate? get() = _selectedDate

    fun setSelectedDate(date: LocalDate?) {
        _selectedDate = date
        context?.let { WidgetUpdateHelper.scheduleUpdateOnDateChange(it) }
    }

    var selectedScheduleItem: ScheduleItem? by mutableStateOf(null)

    private var _currentGroup by mutableStateOf<String>("")
    val currentGroup: String get() = _currentGroup

    fun setCurrentGroup(group: String) {
        _currentGroup = group
        context?.let {
            PreferencesManager.saveCurrentGroup(it, group)
            WidgetUpdateHelper.scheduleUpdateOnGroupChange(it)
        }
    }

    private var _userGroup by mutableStateOf<String>("не задано")
    val userGroup: String get() = _userGroup

    fun setUserGroup(group: String) {
        _userGroup = group
        context?.let {
            PreferencesManager.saveUserGroup(it, group)
            WidgetUpdateHelper.scheduleUpdateOnGroupChange(it)
        }
        setCurrentGroup(group)
    }

    private var _userAvatar by mutableStateOf<String?>(null)
    val userAvatar: String? get() = _userAvatar

    fun setUserAvatar(avatarPath: String?) {
        _userAvatar = avatarPath
        context?.let {
            PreferencesManager.saveUserAvatar(it, avatarPath)
        }
    }

    private var context: Context? = null
    private var _repository: ScheduleRepository? = null
    val repository: ScheduleRepository? get() = _repository

    fun initialize(context: Context) {
        this.context = context
        val database = ScheduleDatabase.getInstance(context)
        _repository = ScheduleRepository(database)
        loadSavedData(context)
        NetworkMonitor.initialize(context)
        checkSemesterOnStartup()
        schedulePeriodicCacheCheck(context)
        scheduleCacheCleanup(context)
        checkAndRunCleanupIfNeeded(context)
    }

    private var _showEmptyLessons by mutableStateOf(true)
    val showEmptyLessons: Boolean get() = _showEmptyLessons

    fun setShowEmptyLessons(showEmpty: Boolean) {
        _showEmptyLessons = showEmpty
        context?.let {
            PreferencesManager.saveShowEmptyLessons(it, showEmpty)
        }
    }

    fun toggleShowEmptyLessons() {
        setShowEmptyLessons(!_showEmptyLessons)
    }

    private fun loadSavedData(context: Context) {
        _userName = PreferencesManager.getUserName(context)
        _userGroup = PreferencesManager.getUserGroup(context)
        _userEmail = PreferencesManager.getUserEmail(context)
        _userAvatar = PreferencesManager.getUserAvatar(context)

        _currentGroup = if (_userGroup != "не задано" && _userGroup.isNotBlank()) {
            _userGroup
        } else {
            PreferencesManager.getCurrentGroup(context).ifBlank { " " }
        }

        val history = PreferencesManager.getSearchHistory(context)
        SearchHistoryManager.initialize(history)

        _showEmptyLessons = PreferencesManager.getShowEmptyLessons(context)
        _lastSemesterCheck = PreferencesManager.getLastSemesterCheck(context)
    }

    private var _userName by mutableStateOf<String>("Настройте параметры профиля")
    val userName: String get() = _userName

    fun setUserName(name: String) {
        _userName = name
        context?.let {
            PreferencesManager.saveUserName(it, name)
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

    fun setScheduleItems(items: List<ScheduleItem>) {
        _scheduleItems = items
        context?.let { WidgetUpdateHelper.scheduleUpdateOnDataChange(it) }
    }

    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading
    fun setLoading(loading: Boolean) { _isLoading = loading }

    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage
    fun setErrorMessage(message: String?) { _errorMessage = message }

    private var _lastSemesterCheck by mutableStateOf<Long>(0)
    val lastSemesterCheck: Long get() = _lastSemesterCheck

    private var _cachedSemester by mutableStateOf<String?>(null)
    val cachedSemester: String? get() = _cachedSemester

    private var _needsSemesterUpdate by mutableStateOf(false)
    val needsSemesterUpdate: Boolean get() = _needsSemesterUpdate

    private fun checkSemesterOnStartup() {
        val currentSemester = SemesterUtils.getCurrentSemester()
        val savedSemester = context?.let { PreferencesManager.getLastKnownSemester(it) }

        if (savedSemester != currentSemester) {
            _needsSemesterUpdate = true
            context?.let {
                PreferencesManager.saveLastKnownSemester(it, currentSemester)

                scheduleSemesterCheckWorker(it)
            }
        }

        _lastSemesterCheck = System.currentTimeMillis()
        context?.let { PreferencesManager.saveLastSemesterCheck(it, _lastSemesterCheck) }
    }

    private fun scheduleSemesterCheckWorker(context: Context) {
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<SemesterCheckWorker>()
            .addTag(SemesterCheckWorker.WORK_NAME)
            .build()

        androidx.work.WorkManager.getInstance(context)
            .enqueue(workRequest)
    }

    private fun schedulePeriodicCacheCheck(context: Context) {
        val periodicWorkRequest = androidx.work.PeriodicWorkRequestBuilder<PeriodicCacheUpdateWorker>(
            3, java.util.concurrent.TimeUnit.DAYS
        )
            .addTag(PeriodicCacheUpdateWorker.WORK_NAME)
            .build()

        androidx.work.WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                PeriodicCacheUpdateWorker.WORK_NAME,
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
    }

    private fun scheduleCacheCleanup(context: Context) {
        val cleanupWorkRequest = androidx.work.PeriodicWorkRequestBuilder<CacheCleanupWorker>(
            3, java.util.concurrent.TimeUnit.DAYS
        )
            .addTag(CacheCleanupWorker.WORK_NAME)
            .build()

        androidx.work.WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                CacheCleanupWorker.WORK_NAME,
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                cleanupWorkRequest
            )
    }

    private fun checkAndRunCleanupIfNeeded(context: Context) {
        val lastCleanup = PreferencesManager.getLastCacheCleanup(context)
        val currentTime = System.currentTimeMillis()
        val threeDaysInMillis = 3 * 24 * 60 * 60 * 1000L

        if (currentTime - lastCleanup > threeDaysInMillis) {
            Log.d("AppState", "Запуск очистки кэша при старте (прошло > 3 дней)")
            val workRequest = androidx.work.OneTimeWorkRequestBuilder<CacheCleanupWorker>()
                .addTag(CacheCleanupWorker.WORK_NAME)
                .build()

            androidx.work.WorkManager.getInstance(context)
                .enqueue(workRequest)
        }
    }

    suspend fun checkGroupCacheFreshness(group: String): CacheStatus {
        if (group.isBlank() || group == " ") {
            return CacheStatus.NO_CACHE
        }

        val repo = repository ?: return CacheStatus.NO_CACHE
        val ctx = context ?: return CacheStatus.ERROR

        return try {
            val activeSemester = SemesterUtils.getActiveSemester()
            val cachedSemester = repo.getCachedSemester(group)
            val cacheTtlDays = PreferencesManager.getCacheTtlDays(ctx)

            when {
                cachedSemester == null -> {
                    CacheStatus.NO_CACHE
                }
                cachedSemester != activeSemester -> {
                    repo.cleanupLegacyData()
                    CacheStatus.OUTDATED_SEMESTER
                }
                repo.isCacheExpired(group, cacheTtlDays) -> {
                    CacheStatus.EXPIRED
                }
                else -> {
                    repo.cleanupLegacyData()
                    CacheStatus.FRESH
                }
            }
        } catch (e: Exception) {
            Log.e("AppState", "Error checking cache freshness", e)
            CacheStatus.ERROR
        }
    }

    private suspend fun isCacheExpiredWithTtl(group: String, cacheTtlDays: Int): Boolean {
        return repository?.isCacheExpired(group, cacheTtlDays) ?: true
    }

    fun getCacheStatusMessage(status: CacheStatus): String {
        return when (status) {
            CacheStatus.FRESH -> "Данные актуальны"
            CacheStatus.EXPIRED -> "Требуется обновление"
            CacheStatus.OUTDATED_SEMESTER -> "Данные за прошлый семестр"
            CacheStatus.NO_CACHE -> "Нет сохраненных данных"
            CacheStatus.ERROR -> "Ошибка проверки"
        }
    }

    fun setNeedsSemesterUpdate(needs: Boolean) {
        _needsSemesterUpdate = needs
    }

    fun resetNeedsSemesterUpdate() {
        _needsSemesterUpdate = false
    }



    fun setCurrentGroupAndNavigate(group: String) {
        setCurrentGroup(group)
        setSelectedDate(LocalDate.now())
    }

    enum class CacheStatus {
        FRESH,
        EXPIRED,
        OUTDATED_SEMESTER,
        NO_CACHE,
        ERROR
    }

    fun scheduleSemesterAvailabilityCheck(group: String) {
        context?.let { ctx ->
            val workRequest = androidx.work.OneTimeWorkRequestBuilder<SemesterAvailabilityWorker>()
                .setInputData(
                    androidx.work.Data.Builder()
                        .putString(SemesterAvailabilityWorker.KEY_GROUP, group)
                        .build()
                )
                .setConstraints(
                    androidx.work.Constraints.Builder()
                        .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                        .build()
                )
                .addTag(SemesterAvailabilityWorker.WORK_NAME)
                .build()

            androidx.work.WorkManager.getInstance(ctx)
                .enqueueUniqueWork(
                    "${SemesterAvailabilityWorker.WORK_NAME}_$group",
                    androidx.work.ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }
    }

    fun onAppResumed(context: Context) {
        val currentTime = System.currentTimeMillis()
        val timeInBackground = currentTime - _lastForegroundTime

        if (timeInBackground > 60 * 60 * 1000) {
            val currentSemester = SemesterUtils.getCurrentSemester()

            if (_lastKnownSemester != null && _lastKnownSemester != currentSemester) {
                Log.d("AppState", "Семестр изменился во время фона: $_lastKnownSemester -> $currentSemester")
                _needsSemesterUpdate = true
                PreferencesManager.saveLastKnownSemester(context, currentSemester)

                if (currentGroup.isNotBlank() && currentGroup != " ") {
                    scheduleSemesterAvailabilityCheck(currentGroup)
                }

                scheduleSemesterCheckWorker(context)
            }

            _lastKnownSemester = currentSemester
        }

        _lastForegroundTime = currentTime
    }

    fun onAppPaused() {
        _lastForegroundTime = System.currentTimeMillis()
        _lastKnownSemester = SemesterUtils.getCurrentSemester()
    }

    fun shouldRefreshOnResume(): Boolean {
        return _needsSemesterUpdate
    }

}