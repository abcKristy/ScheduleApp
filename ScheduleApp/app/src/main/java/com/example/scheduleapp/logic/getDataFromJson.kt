package com.example.scheduleapp.logic

import android.util.Log
import com.example.scheduleapp.data.ScheduleItem
import com.example.scheduleapp.data.RecurrenceRule
import com.example.scheduleapp.database.ScheduleRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
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

// Data classes для парсинга JSON
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

data class GroupsResponse(
    val groups: List<String>
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
        .baseUrl("http://10.150.247.211:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(ScheduleApiService::class.java)
}

// Функции преобразования из Response в Domain модель
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

suspend fun getScheduleItems(
    group: String,
    onSuccess: (List<ScheduleItem>) -> Unit,
    onError: (String) -> Unit
) {
    try {
        Log.d("API_DEBUG", "Fetching schedule for group: $group")
        val apiService = createApiService()
        val response = apiService.getSchedule(group)

        Log.d("API_DEBUG", "Received ${response.size} items")
        val scheduleItems = parseScheduleFromResponse(response)
        Log.d("API_DEBUG", "Parsed ${scheduleItems.size} items")

        scheduleItems.forEachIndexed { index, item ->
            Log.d("API_DEBUG", "Item $index: ${item.discipline}, recurrence: ${item.recurrence}, exceptions: ${item.exceptions.size}")
        }

        onSuccess(scheduleItems)

    } catch (e: Exception) {
        Log.e("API_ERROR", "Error: ${e.message}", e)
        onError("Ошибка: ${e.message}")
    }
}

suspend fun getScheduleItemsWithCache(
    group: String,
    repository: ScheduleRepository? = null,
    onSuccess: (List<ScheduleItem>) -> Unit,
    onError: (String) -> Unit
) {
    try {
        // Сначала проверяем локальную базу
        if (repository != null && repository.hasCachedSchedule(group)) {
            Log.d("SCHEDULE_CACHE", "Loading schedule from database for group: $group")
            val cachedItems = repository.getSchedule(group)
            onSuccess(cachedItems)

            // ДОПОЛНИТЕЛЬНО: обновляем данные с сервера в фоне
            try {
                val apiService = createApiService()
                val response = apiService.getSchedule(group)
                val scheduleItems = parseScheduleFromResponse(response)
                if (scheduleItems.isNotEmpty()) {
                    repository.cacheScheduleItems(group, scheduleItems)
                    Log.d("SCHEDULE_CACHE", "Background update for group: $group")
                }
            } catch (e: Exception) {
                Log.d("SCHEDULE_CACHE", "Background update failed, using cached data")
            }
            return
        }

        // Если нет в базе, загружаем с сервера
        Log.d("SCHEDULE_CACHE", "Loading schedule from server for group: $group")
        val apiService = createApiService()
        val response = apiService.getSchedule(group)
        val scheduleItems = parseScheduleFromResponse(response)

        // Сохраняем в базу данных (накапливаем)
        if (repository != null && scheduleItems.isNotEmpty()) {
            repository.cacheScheduleItems(group, scheduleItems)
            Log.d("SCHEDULE_CACHE", "Saved ${scheduleItems.size} items to database")
        }

        onSuccess(scheduleItems)

    } catch (e: Exception) {
        Log.e("API_ERROR", "Error: ${e.message}", e)

        // Пробуем загрузить из базы данных даже при ошибке сети
        if (repository != null && repository.hasCachedSchedule(group)) {
            Log.d("SCHEDULE_CACHE", "Server failed, using database cache for group: $group")
            val cachedItems = repository.getSchedule(group)
            onSuccess(cachedItems)
        } else {
            // Если нет в БД и сервер недоступен
            Log.e("SCHEDULE_CACHE", "No cached data for group: $group and server unavailable")
            onError("Ошибка: ${e.message}. Нет данных в кэше.")
        }
    }
}