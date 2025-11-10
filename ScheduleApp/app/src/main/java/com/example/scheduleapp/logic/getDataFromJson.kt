package com.example.scheduleapp.logic

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
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
    val url = "http://10.248.65.211:8080/schedule/final/$group"
    val requestQueue = Volley.newRequestQueue(context)

    val stringRequest = object : StringRequest(
        Request.Method.GET, url,
        { response ->
            try {
                android.util.Log.d("API_DEBUG", "Raw response: $response")
                val scheduleItems = parseScheduleFromJson(response)
                android.util.Log.d("API_DEBUG", "Parsed ${scheduleItems.size} items")
                onSuccess(scheduleItems)
            } catch (e: Exception) {
                android.util.Log.e("API_ERROR", "Parse error: ${e.message}", e)
                onError("Ошибка парсинга: ${e.message}")
            }
        },
        { error ->
            android.util.Log.e("API_ERROR", "Network error: ${error.networkResponse?.statusCode ?: 0} - ${error.toString()}")
            onError("Ошибка сети: ${error.toString()}")
        }
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Content-Type"] = "application/json; charset=utf-8"
            headers["Accept"] = "application/json; charset=utf-8"
            return headers
        }

        override fun getBodyContentType(): String {
            return "application/json; charset=utf-8"
        }
    }

    requestQueue.add(stringRequest)
}