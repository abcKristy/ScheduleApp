package com.example.scheduleapp.data

import java.time.LocalDateTime
import java.time.LocalDate

fun TestSchedule(): List<ScheduleItem> {
    return listOf(
        ScheduleItem(
            discipline = "Обоснование и разработка требований к программным системам",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 1, 16, 20),
            endTime = LocalDateTime.of(2025, 9, 1, 17, 50),
            room = "И-204-а (В-78)",
            teacher = "Ахмедова Хамида Гаджиалиевна",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 25, 23, 59)
            ),
            exceptions = listOf(
                LocalDate.of(2025, 10, 6), // Перенос занятия
                LocalDate.of(2025, 11, 3)  // Праздничный день
            )
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 2, 12, 40),
            endTime = LocalDateTime.of(2025, 9, 2, 14, 10),
            room = "А-421 (В-78)",
            teacher = "Егоров Никита Сергеевич",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = "Практическое занятие по разработке UI",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 20, 23, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 11, 10)) // Перенос
        ),
        ScheduleItem(
            discipline = "Обоснование и разработка требований к программным системам",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 8, 16, 20),
            endTime = LocalDateTime.of(2025, 9, 8, 17, 50),
            room = "И-204-а (В-78)",
            teacher = "Ахмедова Хамида Гаджиалиевна",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 25, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Основы сетевых технологий",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 1, 9, 0),
            endTime = LocalDateTime.of(2025, 9, 1, 10, 30),
            room = "Б-210 (В-78)",
            teacher = "Круглов Анатолий Михайлович",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = "Лабораторная работа №1",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 15, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Технологические основы интернета вещей",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 11, 12, 40),
            endTime = LocalDateTime.of(2025, 9, 11, 14, 10),
            room = "А-17 (В-78)",
            teacher = "Образцов Владимир Михайлович",
            groups = listOf("ИКБО-52-23", "ИКБО-50-23", "ИКБО-62-23", "ИКБО-51-23", "ИКБО-61-23", "ИКБО-60-23"),
            groupsSummary = "ИКБО-50-23, ИКБО-51-23, ИКБО-52-23, ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = "Лекция по основам IoT",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 18, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Моделирование бизнес-процессов",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 11, 9, 0),
            endTime = LocalDateTime.of(2025, 9, 11, 10, 30),
            room = "А-424-1 (В-78)",
            teacher = "Карамышев Антон Николаевич",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = "Работа с BPMN",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 11, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 9, 14, 20),
            endTime = LocalDateTime.of(2025, 9, 9, 15, 50),
            room = "А-421 (В-78)",
            teacher = "Егоров Никита Сергеевич",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = "Работа с базами данных в Android",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 20, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 2, 14, 20),
            endTime = LocalDateTime.of(2025, 9, 2, 15, 50),
            room = "А-421 (В-78)",
            teacher = "Егоров Никита Сергеевич",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = "Основы Kotlin Coroutines",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 20, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Моделирование бизнес-процессов",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 4, 10, 40),
            endTime = LocalDateTime.of(2025, 9, 4, 12, 10),
            room = "А-424-1 (В-78)",
            teacher = "Карамышев Антон Николаевич",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = "Анализ бизнес-процессов",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 11, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Моделирование сред и разработка приложений виртуальной и дополненной реальности",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 6, 12, 40),
            endTime = LocalDateTime.of(2025, 9, 6, 14, 10),
            room = "А-423 (В-78)",
            teacher = "Тюшкевич Николай Максимович",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = "Введение в Unity",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 13, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Технологические основы интернета вещей",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 2, 18, 0),
            endTime = LocalDateTime.of(2025, 9, 2, 19, 30),
            room = "Г-301-в (В-78)",
            teacher = "Образцов Владимир Михайлович",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = "Работа с Arduino",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 16, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Обоснование и разработка требований к программным системам",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 1, 14, 20),
            endTime = LocalDateTime.of(2025, 9, 1, 15, 50),
            room = "А-6 (В-78)",
            teacher = "Ахмедова Хамида Гаджиалиевна",
            groups = listOf("ИКБО-62-23", "ИКБО-61-23", "ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = "Лекция по методологиям разработки требований",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 25, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 1, 14, 20),
            endTime = LocalDateTime.of(2025, 9, 1, 15, 50),
            room = "А-421 (В-78)",
            teacher = "Егоров Никита Сергеевич",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = "Введение в Android Development",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 20, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 2, 16, 20),
            endTime = LocalDateTime.of(2025, 9, 2, 17, 50),
            room = "А-5 (В-78)",
            teacher = "Степанов Павел Валериевич",
            groups = listOf("ИКБО-62-23", "ИКБО-61-23", "ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = "Архитектура мобильных приложений",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 20, 23, 59)
            )
        ),
        ScheduleItem(
            discipline = "Разработка баз данных",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 6, 10, 40),
            endTime = LocalDateTime.of(2025, 9, 6, 12, 10),
            room = "И-212-б (В-78)",
            teacher = "Ужахов Нурдин Люреханович",
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = "SQL запросы",
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 1,
                until = LocalDateTime.of(2025, 12, 13, 23, 59)
            )
        )
    )
}