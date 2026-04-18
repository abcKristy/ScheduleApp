package com.example.scheduleapp.data.state

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.scheduleapp.data.entity.ScheduleItem
import com.example.scheduleapp.data.database.ScheduleDatabase
import com.example.scheduleapp.data.database.ScheduleRepository
import com.example.scheduleapp.util.SemesterUtils
import com.example.scheduleapp.widgets.WidgetUpdateHelper
import java.time.LocalDate

@SuppressLint("StaticFieldLeak")
object AppState {
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
        checkSemesterOnStartup()
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

    // ========== НОВЫЕ ПОЛЯ И МЕТОДЫ ДЛЯ РАБОТЫ С СЕМЕСТРОМ ==========

    private var _lastSemesterCheck by mutableStateOf<Long>(0)
    val lastSemesterCheck: Long get() = _lastSemesterCheck

    private var _cachedSemester by mutableStateOf<String?>(null)
    val cachedSemester: String? get() = _cachedSemester

    private var _needsSemesterUpdate by mutableStateOf(false)
    val needsSemesterUpdate: Boolean get() = _needsSemesterUpdate

    /**
     * Проверка семестра при запуске приложения
     */
    private fun checkSemesterOnStartup() {
        val currentSemester = SemesterUtils.getCurrentSemester()
        val savedSemester = PreferencesManager.getLastKnownSemester(context!!)

        if (savedSemester != currentSemester) {
            setNeedsSemesterUpdate(true)
            PreferencesManager.saveLastKnownSemester(context!!, currentSemester)
        }

        _lastSemesterCheck = System.currentTimeMillis()
        PreferencesManager.saveLastSemesterCheck(context!!, _lastSemesterCheck)
    }

    /**
     * Проверка актуальности кэша для группы
     */
    suspend fun checkGroupCacheFreshness(group: String): CacheStatus {
        if (group.isBlank() || group == " ") {
            return CacheStatus.NO_CACHE
        }

        val repo = repository ?: return CacheStatus.NO_CACHE

        return try {
            val currentSemester = SemesterUtils.getCurrentSemester()
            val cachedSemester = repo.getCachedSemester(group)

            when {
                cachedSemester == null -> CacheStatus.NO_CACHE
                cachedSemester != currentSemester -> CacheStatus.OUTDATED_SEMESTER
                repo.isCacheExpired(group) -> CacheStatus.EXPIRED
                else -> CacheStatus.FRESH
            }
        } catch (e: Exception) {
            CacheStatus.ERROR
        }
    }

    /**
     * Получить читаемое описание статуса кэша
     */
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

    /**
     * Статус кэша для группы
     */
    enum class CacheStatus {
        FRESH,          // Актуальные данные
        EXPIRED,        // TTL истек
        OUTDATED_SEMESTER, // Данные за другой семестр
        NO_CACHE,       // Нет данных в кэше
        ERROR           // Ошибка проверки
    }

    /**
     * Принудительное обновление текущей группы
     */
    suspend fun refreshCurrentGroup() {
        if (currentGroup.isBlank() || currentGroup == " ") return

        _isLoading = true
        _errorMessage = "Обновление расписания..."

        // Здесь будет вызов загрузки
        // Логика вынесена в ScreenList
    }

    /**
     * Сброс состояния загрузки
     */
    fun resetLoadingState() {
        _isLoading = false
        _errorMessage = null
    }
}