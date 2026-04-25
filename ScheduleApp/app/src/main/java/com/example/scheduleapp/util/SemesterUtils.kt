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

    fun isSummerHolidayPeriod(date: LocalDate = LocalDate.now()): Boolean {
        val month = date.monthValue
        return month == 7 || month == 8
    }

    fun getLastCompletedSemester(date: LocalDate = LocalDate.now()): String {
        val year = date.year
        val month = date.monthValue

        return when {
            month >= 9 -> "$year-$AUTUMN"
            month in 7..8 -> "$year-$SPRING"
            month in 2..6 -> "$year-$SPRING"
            month == 1 -> "${year - 1}-$AUTUMN"
            else -> "$year-$SPRING"
        }
    }

    fun getActiveSemester(date: LocalDate = LocalDate.now()): String {
        return if (isSummerHolidayPeriod(date)) {
            getLastCompletedSemester(date)
        } else {
            getCurrentSemester(date)
        }
    }

    fun getNextSemesterName(date: LocalDate = LocalDate.now()): String {
        val month = date.monthValue
        val year = date.year

        return when {
            month in 7..8 -> "Осенний семестр ${year}/${year + 1}"
            month in 1..6 -> "Осенний семестр ${year}/${year + 1}"
            month >= 9 -> "Весенний семестр ${year + 1}"
            else -> "следующий семестр"
        }
    }

    fun getNextSemesterStartDate(date: LocalDate = LocalDate.now()): LocalDate {
        val year = date.year
        val month = date.monthValue

        return when {
            month in 7..8 -> LocalDate.of(year, 9, 1)
            month in 1..6 -> LocalDate.of(year, 9, 1)
            month >= 9 -> LocalDate.of(year + 1, 2, 1)
            else -> LocalDate.of(year, 9, 1)
        }
    }

    fun getDaysUntilNextSemester(date: LocalDate = LocalDate.now()): Long {
        val nextStart = getNextSemesterStartDate(date)
        return java.time.temporal.ChronoUnit.DAYS.between(date, nextStart)
    }

    fun getSummerHolidayMessage(): String {
        val nextSemester = getNextSemesterName()
        val daysLeft = getDaysUntilNextSemester()

        return when (daysLeft) {
            0L -> "Сегодня начинается $nextSemester!"
            1L -> "Завтра начинается $nextSemester"
            in 2L..7L -> "До начала $nextSemester осталось $daysLeft дней"
            else -> "Сейчас период летних каникул. $nextSemester начнется ${daysLeft} дней"
        }
    }

    fun getPeriodStatus(): PeriodStatus {
        val today = LocalDate.now()
        val month = today.monthValue

        return when {
            month in 7..8 -> PeriodStatus.SUMMER_HOLIDAYS
            month in 1..1 -> PeriodStatus.WINTER_SESSION
            month in 6..6 -> PeriodStatus.SUMMER_SESSION
            else -> PeriodStatus.ACTIVE_SEMESTER
        }
    }

    enum class PeriodStatus {
        ACTIVE_SEMESTER,
        SUMMER_HOLIDAYS,
        WINTER_SESSION,
        SUMMER_SESSION
    }
}