package com.example.scheduleapp.util

import java.time.LocalDate
import java.time.Month

object SemesterUtils {

    const val SPRING = "SPRING"
    const val AUTUMN = "AUTUMN"
    const val LEGACY = "LEGACY"

    fun getCurrentSemester(date: LocalDate = LocalDate.now()): String {
        val year = date.year
        val month = date.month

        return when {
            month.value >= Month.SEPTEMBER.value -> "$year-$AUTUMN"
            month == Month.JANUARY -> "${year - 1}-$AUTUMN"
            month.value >= Month.FEBRUARY.value && month.value <= Month.JUNE.value -> "$year-$SPRING"
            else -> "$year-$SPRING"
        }
    }

    fun getSemesterStartDate(semester: String): LocalDate {
        val parts = semester.split("-")
        val year = parts[0].toInt()
        val type = parts[1]

        return if (type == SPRING) {
            LocalDate.of(year, 2, 1)
        } else {
            LocalDate.of(year, 9, 1)
        }
    }

    fun getSemesterEndDate(semester: String): LocalDate {
        val parts = semester.split("-")
        val year = parts[0].toInt()
        val type = parts[1]

        return if (type == SPRING) {
            LocalDate.of(year, 6, 30)
        } else {
            LocalDate.of(year + 1, 1, 31)
        }
    }

    fun isSameSemester(semester1: String?, semester2: String?): Boolean {
        if (semester1 == null || semester2 == null) return false
        return semester1 == semester2
    }

    fun isOutdated(cachedSemester: String?): Boolean {
        if (cachedSemester == null) return true
        return !isSameSemester(cachedSemester, getCurrentSemester())
    }

    fun isCacheExpired(expiresAt: Long): Boolean {
        return System.currentTimeMillis() > expiresAt
    }

    fun getDisplayName(semester: String): String {
        if (semester == LEGACY) return "Архив"

        val parts = semester.split("-")
        val year = parts[0]
        val type = parts[1]

        val season = if (type == SPRING) "Весенний семестр" else "Осенний семестр"

        return if (type == AUTUMN) {
            val nextYear = year.toInt() + 1
            "$season $year/$nextYear"
        } else {
            "$season $year"
        }
    }

    fun isHolidayPeriod(date: LocalDate = LocalDate.now()): Boolean {
        val month = date.month
        return month == Month.JULY || month == Month.AUGUST
    }

    fun getHolidayMessage(): String {
        val nextSemester = when (LocalDate.now().month) {
            Month.JULY, Month.AUGUST -> "осеннего семестра (начало в сентябре)"
            else -> ""
        }
        return "Сейчас период каникул. Расписание $nextSemester появится позже."
    }
}