package com.example.scheduleapp.database


import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return value?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    @TypeConverter
    fun fromLocalDateList(value: List<LocalDate>?): String? {
        return value?.joinToString(",") { it.format(DateTimeFormatter.ISO_LOCAL_DATE) }
    }

    @TypeConverter
    fun toLocalDateList(value: String?): List<LocalDate> {
        return value?.split(",")?.filter { it.isNotBlank() }?.map {
            LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
        } ?: emptyList()
    }
}