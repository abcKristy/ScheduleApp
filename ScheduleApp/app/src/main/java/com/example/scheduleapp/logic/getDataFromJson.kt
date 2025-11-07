package com.example.scheduleapp.logic

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.scheduleapp.data.AppState
import com.example.scheduleapp.data.ScheduleItem
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resumeWithException

private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

fun parseScheduleFromJson(jsonString: String): List<ScheduleItem> {
    val scheduleItems = mutableListOf<ScheduleItem>()

    try {
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val scheduleItem = parseScheduleItem(jsonObject)
            scheduleItems.add(scheduleItem)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return scheduleItems
}

fun parseScheduleItem(jsonObject: JSONObject): ScheduleItem {
    return ScheduleItem(
        id = jsonObject.getString("id"),
        discipline = jsonObject.getString("discipline"),
        lessonType = jsonObject.getString("lessonType"),
        startTime = LocalDateTime.parse(jsonObject.getString("startTime"),dateTimeFormatter),
        endTime = LocalDateTime.parse(jsonObject.getString("endTime"),dateTimeFormatter),
        room = jsonObject.getString("room"),
        teacher = jsonObject.getString("teacher"),
        groups = parseGroups(jsonObject.getJSONArray("groups")),
        groupsSummary = jsonObject.getString("groupsSummary"),
        description = jsonObject.optString("description").takeIf { it != "null" && it.isNotEmpty()}
    )
}

fun parseGroups(groupsArray: JSONArray): List<String> {
    val groups = mutableListOf<String>()
    for(i in 0 until groupsArray.length())
        groups.add(groupsArray.getString(i))
    return groups
}

// Функция с использованием корутин (рекомендуется)
fun getScheduleItems(
    context: Context,
    group: String,
    onSuccess: (List<ScheduleItem>) -> Unit,
    onError: (String) -> Unit
) {
    val url = "http://localhost:8080/schedule/final/$group"
    val requestQueue = Volley.newRequestQueue(context)

    val stringRequest = StringRequest(
        Request.Method.GET, url,
        { response ->
            try {
                val scheduleItems = parseScheduleFromJson(response)
                onSuccess(scheduleItems)
            } catch (e: Exception) {
                onError("Ошибка парсинга: ${e.message}")
            }
        },
        { error ->
            onError("Ошибка сети: ${error.message}")
        }
    )

    requestQueue.add(stringRequest)
}
