package com.example.scheduleapp.logic

import android.util.Log
import com.example.scheduleapp.data.ScheduleItem
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

// Data classes для парсинга JSON
data class ScheduleItemResponse(
    val id: String,
    val discipline: String,
    val lessonType: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val teacher: String,
    val groups: List<String>,
    val groupsSummary: String,
    val description: String?
)

data class GroupsResponse(
    val groups: List<String>
)

// Retrofit интерфейс для API
interface ScheduleApiService {
    @GET("schedule/final/{group}")
    @Headers(
        "Content-Type: application/json; charset=utf-8",
        "Accept: application/json; charset=utf-8"
    )
    suspend fun getSchedule(@Path("group") group: String): List<ScheduleItemResponse>
}

// Создание Retrofit клиента
private fun createApiService(): ScheduleApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.248.65.211:8080/")
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
        id = response.id,
        discipline = response.discipline,
        lessonType = response.lessonType,
        startTime = LocalDateTime.parse(response.startTime, dateTimeFormatter),
        endTime = LocalDateTime.parse(response.endTime, dateTimeFormatter),
        room = response.room,
        teacher = response.teacher,
        groups = response.groups,
        groupsSummary = response.groupsSummary,
        description = response.description?.takeIf { it != "null" && it.isNotEmpty() }
    )
}

// Основная функция с использованием Retrofit
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
        onSuccess(scheduleItems)

    } catch (e: Exception) {
        Log.e("API_ERROR", "Error: ${e.message}", e)
        onError("Ошибка: ${e.message}")
    }
}