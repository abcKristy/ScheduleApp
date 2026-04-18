package com.example.scheduleapp.logic

import android.util.Log
import com.example.scheduleapp.data.entity.ScheduleItem
import com.example.scheduleapp.data.entity.RecurrenceRule
import com.example.scheduleapp.data.database.ScheduleRepository
import com.example.scheduleapp.util.SemesterUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

data class ScheduleItemResponse(
    val discipline: String,
    val lessonType: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val teacher: String,
    val groups: List<String>,
    val groupsSummary: String,
    val description: String?,
    val recurrence: RecurrenceRuleResponse? = null,
    val exceptions: List<String>? = emptyList()
)

data class RecurrenceRuleResponse(
    val frequency: String? = null,
    val interval: Int? = null,
    val until: String? = null
)


interface ScheduleApiService {
    @GET("schedule/final/{group}")
    @Headers(
        "Content-Type: application/json; charset=utf-8",
        "Accept: application/json; charset=utf-8"
    )
    suspend fun getSchedule(@Path("group") group: String): List<ScheduleItemResponse>
}

private fun createApiService(): ScheduleApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.141.151.211:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(ScheduleApiService::class.java)
}

fun parseScheduleFromResponse(response: List<ScheduleItemResponse>): List<ScheduleItem> {
    return response.map { parseScheduleItem(it) }
}

fun parseScheduleItem(response: ScheduleItemResponse): ScheduleItem {
    return ScheduleItem(
        discipline = response.discipline,
        lessonType = response.lessonType,
        startTime = LocalDateTime.parse(response.startTime, dateTimeFormatter),
        endTime = LocalDateTime.parse(response.endTime, dateTimeFormatter),
        room = response.room,
        teacher = response.teacher,
        groups = response.groups,
        groupsSummary = response.groupsSummary,
        description = response.description?.takeIf { it != "null" && it.isNotEmpty() },
        recurrence = response.recurrence?.let { recurrence ->
            RecurrenceRule(
                frequency = recurrence.frequency,
                interval = recurrence.interval,
                until = recurrence.until?.let { LocalDateTime.parse(it, dateTimeFormatter) }
            )
        },
        exceptions = response.exceptions?.map { LocalDate.parse(it, dateFormatter) } ?: emptyList()
    )
}

suspend fun getScheduleItemsWithCache(
    group: String,
    repository: ScheduleRepository? = null,
    forceRefresh: Boolean = false,
    onSuccess: (List<ScheduleItem>) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val currentSemester = SemesterUtils.getCurrentSemester()

        // Если не принудительное обновление, проверяем кэш
        if (!forceRefresh && repository != null) {
            val cachedSemester = repository.getCachedSemester(group)

            if (cachedSemester == currentSemester && repository.hasCachedScheduleForSemester(group, currentSemester)) {
                Log.d("SCHEDULE_CACHE", "Loading from database for group: $group, semester: $currentSemester")
                val cachedItems = repository.getScheduleForSemester(group, currentSemester)

                if (!repository.isCacheExpired(group)) {
                    onSuccess(cachedItems)
                    return
                } else {
                    Log.d("SCHEDULE_CACHE", "Cache expired, showing old data and refreshing")
                    onSuccess(cachedItems)
                    // Продолжаем загрузку свежих данных
                }
            } else if (cachedSemester != null && cachedSemester != currentSemester) {
                Log.d("SCHEDULE_CACHE", "Semester changed: cached=$cachedSemester, current=$currentSemester")
                // Удаляем устаревшие данные
                repository.cleanupOutdatedGroups(currentSemester)
            }
        }

        // Загружаем с сервера
        Log.d("SCHEDULE_CACHE", "Loading from server for group: $group")
        val apiService = createApiService()
        val response = apiService.getSchedule(group)
        val scheduleItems = parseScheduleFromResponse(response)

        if (repository != null && scheduleItems.isNotEmpty()) {
            repository.cacheScheduleItemsWithSemester(group, scheduleItems, currentSemester)
            Log.d("SCHEDULE_CACHE", "Saved ${scheduleItems.size} items with semester $currentSemester")
        }

        onSuccess(scheduleItems)

    } catch (e: Exception) {
        Log.e("API_ERROR", "Error: ${e.message}", e)

        if (repository != null && repository.hasCachedSchedule(group)) {
            Log.d("SCHEDULE_CACHE", "Server failed, using ANY cached data for group: $group")
            val cachedItems = repository.getSchedule(group)
            onSuccess(cachedItems)
        } else {
            onError("Сервер недоступен и нет сохраненных данных для группы '$group'")
        }
    }
}