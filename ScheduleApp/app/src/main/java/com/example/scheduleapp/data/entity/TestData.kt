package com.example.scheduleapp.data.entity

import java.time.LocalDateTime
import java.time.LocalDate

fun TestSchedule(): List<ScheduleItem> {
    return listOf(
        ScheduleItem(
            discipline = "Моделирование бизнес-процессов",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 1, 12, 40),
            endTime = LocalDateTime.of(2025, 9, 1, 14, 10),
            rooms = listOf("А-9 (В-78)"),
            teachers = listOf("Карамышев Антон Николаевич"),
            groups = listOf(
                "ИКБО-52-23",
                "ИКБО-62-23",
                "ИКБО-50-23",
                "ИКБО-51-23",
                "ИКБО-61-23",
                "ИКБО-60-23"
            ),
            groupsSummary = "ИКБО-50-23, ИКБО-51-23, ИКБО-52-23, ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 22))
        ),
        ScheduleItem(
            discipline = "Моделирование бизнес-процессов",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 11, 10, 40),
            endTime = LocalDateTime.of(2025, 9, 11, 12, 10),
            rooms = listOf("А-424-1 (В-78)"),
            teachers = listOf("Карамышев Антон Николаевич"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2026, 1, 1))
        ),
        ScheduleItem(
            discipline = "Разработка баз данных",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 8, 12, 40),
            endTime = LocalDateTime.of(2025, 9, 8, 14, 10),
            rooms = listOf("А-9 (В-78)"),
            teachers = listOf("Семыкина Наталья Александровна"),
            groups = listOf(
                "ИКБО-52-23",
                "ИКБО-62-23",
                "ИКБО-50-23",
                "ИКБО-51-23",
                "ИКБО-61-23",
                "ИКБО-60-23"
            ),
            groupsSummary = "ИКБО-50-23, ИКБО-51-23, ИКБО-52-23, ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 29))
        ),
        ScheduleItem(
            discipline = "Тестирование и верификация программного обеспечения",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 1, 10, 40),
            endTime = LocalDateTime.of(2025, 9, 1, 12, 10),
            rooms = listOf("И-208 (В-78)"),
            teachers = listOf("Мельников Денис Александрович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 22))
        ),
        ScheduleItem(
            discipline = "Моделирование бизнес-процессов",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 4, 9, 0),
            endTime = LocalDateTime.of(2025, 9, 4, 10, 30),
            rooms = listOf("А-424-1 (В-78)"),
            teachers = listOf("Карамышев Антон Николаевич"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 25))
        ),
        ScheduleItem(
            discipline = "Тестирование и верификация программного обеспечения",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 5, 9, 0),
            endTime = LocalDateTime.of(2025, 9, 5, 10, 30),
            rooms = listOf("А-18 (В-78)"),
            teachers = listOf("Петренко Александр Анатольевич"),
            groups = listOf(
                "ИКБО-52-23",
                "ИКБО-62-23",
                "ИКБО-50-23",
                "ИКБО-51-23",
                "ИКБО-61-23",
                "ИКБО-60-23"
            ),
            groupsSummary = "ИКБО-50-23, ИКБО-51-23, ИКБО-52-23, ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 26))
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 2, 16, 20),
            endTime = LocalDateTime.of(2025, 9, 2, 17, 50),
            rooms = listOf("А-5 (В-78)"),
            teachers = listOf("Степанов Павел Валериевич"),
            groups = listOf("ИКБО-62-23", "ИКБО-61-23", "ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 23))
        ),
        ScheduleItem(
            discipline = "Обоснование и разработка требований к программным системам",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 8, 16, 20),
            endTime = LocalDateTime.of(2025, 9, 8, 17, 50),
            rooms = listOf("И-204-а (В-78)"),
            teachers = listOf("Ахмедова Хамида Гаджиалиевна"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 29))
        ),
        ScheduleItem(
            discipline = "Обоснование и разработка требований к программным системам",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 8, 14, 20),
            endTime = LocalDateTime.of(2025, 9, 8, 15, 50),
            rooms = listOf("А-6 (В-78)"),
            teachers = listOf("Ахмедова Хамида Гаджиалиевна"),
            groups = listOf("ИКБО-62-23", "ИКБО-61-23", "ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 29))
        ),
        ScheduleItem(
            discipline = "Технологические основы интернета вещей",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 9, 18, 0),
            endTime = LocalDateTime.of(2025, 9, 9, 19, 30),
            rooms = listOf("Г-301-в (В-78)"),
            teachers = listOf("Образцов Владимир Михайлович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 11, 4), LocalDate.of(2025, 12, 30))
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 9, 14, 20),
            endTime = LocalDateTime.of(2025, 9, 9, 15, 50),
            rooms = listOf("А-421 (В-78)"),
            teachers = listOf("Егоров Никита Сергеевич"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 11, 4), LocalDate.of(2025, 12, 30))
        ),
        ScheduleItem(
            discipline = "Технологические основы интернета вещей",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 11, 12, 40),
            endTime = LocalDateTime.of(2025, 9, 11, 14, 10),
            rooms = listOf("А-17 (В-78)"),
            teachers = listOf("Образцов Владимир Михайлович"),
            groups = listOf(
                "ИКБО-52-23",
                "ИКБО-62-23",
                "ИКБО-50-23",
                "ИКБО-51-23",
                "ИКБО-61-23",
                "ИКБО-60-23"
            ),
            groupsSummary = "ИКБО-50-23, ИКБО-51-23, ИКБО-52-23, ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2026, 1, 1))
        ),
        ScheduleItem(
            discipline = "Технологические основы интернета вещей",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 2, 18, 0),
            endTime = LocalDateTime.of(2025, 9, 2, 19, 30),
            rooms = listOf("Г-301-в (В-78)"),
            teachers = listOf("Образцов Владимир Михайлович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 23))
        ),
        ScheduleItem(
            discipline = "Обоснование и разработка требований к программным системам",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 1, 16, 20),
            endTime = LocalDateTime.of(2025, 9, 1, 17, 50),
            rooms = listOf("И-204-а (В-78)"),
            teachers = listOf("Ахмедова Хамида Гаджиалиевна"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 22))
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 1, 12, 40),
            endTime = LocalDateTime.of(2025, 9, 1, 14, 10),
            rooms = listOf("А-421 (В-78)"),
            teachers = listOf("Егоров Никита Сергеевич"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(
                LocalDate.of(2025, 9, 1),
                LocalDate.of(2025, 9, 15),
                LocalDate.of(2025, 9, 29),
                LocalDate.of(2025, 10, 13),
                LocalDate.of(2025, 10, 27),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 24),
                LocalDate.of(2025, 12, 8)
            )
        ),
        ScheduleItem(
            discipline = "Моделирование сред и разработка приложений виртуальной и дополненной реальности",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 6, 12, 40),
            endTime = LocalDateTime.of(2025, 9, 6, 14, 10),
            rooms = listOf("А-423 (В-78)"),
            teachers = listOf("Тюшкевич Николай Максимович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 27))
        ),
        ScheduleItem(
            discipline = "Разработка баз данных",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 6, 9, 0),
            endTime = LocalDateTime.of(2025, 9, 6, 10, 30),
            rooms = listOf("И-212-б (В-78)"),
            teachers = listOf("Ужахов Нурдин Люреханович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 27))
        ),
        ScheduleItem(
            discipline = "Тестирование и верификация программного обеспечения",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 8, 10, 40),
            endTime = LocalDateTime.of(2025, 9, 8, 12, 10),
            rooms = listOf("И-208 (В-78)"),
            teachers = listOf("Мельников Денис Александрович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 29))
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 2, 14, 20),
            endTime = LocalDateTime.of(2025, 9, 2, 15, 50),
            rooms = listOf("А-421 (В-78)"),
            teachers = listOf("Егоров Никита Сергеевич"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 23))
        ),
        ScheduleItem(
            discipline = "Моделирование бизнес-процессов",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 4, 10, 40),
            endTime = LocalDateTime.of(2025, 9, 4, 12, 10),
            rooms = listOf("А-424-1 (В-78)"),
            teachers = listOf("Карамышев Антон Николаевич"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 25))
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 9, 12, 40),
            endTime = LocalDateTime.of(2025, 9, 9, 14, 10),
            rooms = listOf("А-421 (В-78)"),
            teachers = listOf("Егоров Никита Сергеевич"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 11, 4), LocalDate.of(2025, 12, 30))
        ),
        ScheduleItem(
            discipline = "Разработка баз данных",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 6, 10, 40),
            endTime = LocalDateTime.of(2025, 9, 6, 12, 10),
            rooms = listOf("И-212-б (В-78)"),
            teachers = listOf("Ужахов Нурдин Люреханович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 27))
        ),
        ScheduleItem(
            discipline = "Основы сетевых технологий",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 1, 9, 0),
            endTime = LocalDateTime.of(2025, 9, 1, 10, 30),
            rooms = listOf("Б-210 (В-78)"),
            teachers = listOf("Круглов Анатолий Михайлович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 22))
        ),
        ScheduleItem(
            discipline = "Технологические основы интернета вещей",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 1, 18, 0),
            endTime = LocalDateTime.of(2025, 9, 1, 19, 30),
            rooms = listOf("Г-301-в (В-78)"),
            teachers = listOf("Образцов Владимир Михайлович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(
                LocalDate.of(2025, 9, 1),
                LocalDate.of(2025, 9, 15),
                LocalDate.of(2025, 9, 29),
                LocalDate.of(2025, 10, 13),
                LocalDate.of(2025, 10, 27),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 24),
                LocalDate.of(2025, 12, 8)
            )
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 1, 16, 20),
            endTime = LocalDateTime.of(2025, 9, 1, 17, 50),
            rooms = listOf("А-5 (В-78)"),
            teachers = listOf("Степанов Павел Валериевич"),
            groups = listOf("ИКБО-62-23", "ИКБО-61-23", "ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(
                LocalDate.of(2025, 9, 1),
                LocalDate.of(2025, 9, 15),
                LocalDate.of(2025, 9, 29),
                LocalDate.of(2025, 10, 13),
                LocalDate.of(2025, 10, 27),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 24),
                LocalDate.of(2025, 12, 8)
            )
        ),
        ScheduleItem(
            discipline = "Основы сетевых технологий",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 5, 10, 40),
            endTime = LocalDateTime.of(2025, 9, 5, 12, 10),
            rooms = listOf("А-16 (В-78)"),
            teachers = listOf("Заботкина Екатерина Михайловна"),
            groups = listOf(
                "ИКБО-52-23",
                "ИКБО-62-23",
                "ИКБО-50-23",
                "ИКБО-51-23",
                "ИКБО-61-23",
                "ИКБО-60-23"
            ),
            groupsSummary = "ИКБО-50-23, ИКБО-51-23, ИКБО-52-23, ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 26))
        ),
        ScheduleItem(
            discipline = "Моделирование сред и разработка приложений виртуальной и дополненной реальности",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 13, 10, 40),
            endTime = LocalDateTime.of(2025, 9, 13, 12, 10),
            rooms = listOf("А-423 (В-78)"),
            teachers = listOf("Тюшкевич Николай Максимович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2026, 1, 3))
        ),
        ScheduleItem(
            discipline = "Моделирование сред и разработка приложений виртуальной и дополненной реальности",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 12, 9, 0),
            endTime = LocalDateTime.of(2025, 9, 12, 10, 30),
            rooms = listOf("А-18 (В-78)"),
            teachers = listOf("Синицын Анатолий Васильевич"),
            groups = listOf(
                "ИКБО-52-23",
                "ИКБО-62-23",
                "ИКБО-50-23",
                "ИКБО-51-23",
                "ИКБО-61-23",
                "ИКБО-60-23"
            ),
            groupsSummary = "ИКБО-50-23, ИКБО-51-23, ИКБО-52-23, ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2026, 1, 2))
        ),
        ScheduleItem(
            discipline = "Моделирование сред и разработка приложений виртуальной и дополненной реальности",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 13, 12, 40),
            endTime = LocalDateTime.of(2025, 9, 13, 14, 10),
            rooms = listOf("А-423 (В-78)"),
            teachers = listOf("Тюшкевич Николай Максимович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2026, 1, 3))
        ),
        ScheduleItem(
            discipline = "Обоснование и разработка требований к программным системам",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 1, 14, 20),
            endTime = LocalDateTime.of(2025, 9, 1, 15, 50),
            rooms = listOf("А-6 (В-78)"),
            teachers = listOf("Ахмедова Хамида Гаджиалиевна"),
            groups = listOf("ИКБО-62-23", "ИКБО-61-23", "ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 22))
        ),
        ScheduleItem(
            discipline = "Моделирование бизнес-процессов",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 11, 9, 0),
            endTime = LocalDateTime.of(2025, 9, 11, 10, 30),
            rooms = listOf("А-424-1 (В-78)"),
            teachers = listOf("Карамышев Антон Николаевич"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2026, 1, 1))
        ),
        ScheduleItem(
            discipline = "Разработка баз данных",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 13, 9, 0),
            endTime = LocalDateTime.of(2025, 9, 13, 10, 30),
            rooms = listOf("И-212-б (В-78)"),
            teachers = listOf("Ужахов Нурдин Люреханович"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2026, 1, 3))
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 2, 12, 40),
            endTime = LocalDateTime.of(2025, 9, 2, 14, 10),
            rooms = listOf("А-421 (В-78)"),
            teachers = listOf("Егоров Никита Сергеевич"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 12, 23))
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "PR",
            startTime = LocalDateTime.of(2025, 9, 1, 14, 20),
            endTime = LocalDateTime.of(2025, 9, 1, 15, 50),
            rooms = listOf("А-421 (В-78)"),
            teachers = listOf("Егоров Никита Сергеевич"),
            groups = listOf("ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(
                LocalDate.of(2025, 9, 1),
                LocalDate.of(2025, 9, 15),
                LocalDate.of(2025, 9, 29),
                LocalDate.of(2025, 10, 13),
                LocalDate.of(2025, 10, 27),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 24),
                LocalDate.of(2025, 12, 8)
            )
        ),
        ScheduleItem(
            discipline = "Основы сетевых технологий",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 12, 10, 40),
            endTime = LocalDateTime.of(2025, 9, 12, 12, 10),
            rooms = listOf("А-16 (В-78)"),
            teachers = listOf("Заботкина Екатерина Михайловна"),
            groups = listOf(
                "ИКБО-52-23",
                "ИКБО-62-23",
                "ИКБО-50-23",
                "ИКБО-51-23",
                "ИКБО-61-23",
                "ИКБО-60-23"
            ),
            groupsSummary = "ИКБО-50-23, ИКБО-51-23, ИКБО-52-23, ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2026, 1, 2))
        ),
        ScheduleItem(
            discipline = "Проектирование и разработка мобильных приложений на языке Котлин",
            lessonType = "LK",
            startTime = LocalDateTime.of(2025, 9, 9, 16, 20),
            endTime = LocalDateTime.of(2025, 9, 9, 17, 50),
            rooms = listOf("А-5 (В-78)"),
            teachers = listOf("Степанов Павел Валериевич"),
            groups = listOf("ИКБО-62-23", "ИКБО-61-23", "ИКБО-60-23"),
            groupsSummary = "ИКБО-60-23, ИКБО-61-23, ИКБО-62-23",
            description = null,
            recurrence = RecurrenceRule(
                frequency = "WEEKLY",
                interval = 2,
                until = LocalDateTime.of(2025, 12, 30, 20, 59, 59)
            ),
            exceptions = listOf(LocalDate.of(2025, 11, 4), LocalDate.of(2025, 12, 30))
        )
    )
}