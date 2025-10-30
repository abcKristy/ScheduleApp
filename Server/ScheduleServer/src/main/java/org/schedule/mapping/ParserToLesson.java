package org.schedule.mapping;

import org.schedule.entity.forBD.LessonEntity;
import org.schedule.entity.forBD.LessonType;
import org.schedule.entity.forBD.RecurrenceRule;
import org.schedule.entity.forBD.StudentGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParserToLesson {
    private static final Logger log = LoggerFactory.getLogger(ParserToLesson.class); // Исправлено
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final Pattern EVENT_PATTERN = Pattern.compile("BEGIN:VEVENT(.*?)END:VEVENT", Pattern.DOTALL);
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("^(.*?):(.*)$", Pattern.MULTILINE);

    /**
     * Парсит iCalendar строку в список сущностей Lesson
     * @param data iCalendar строка
     * @return список Lesson entities
     */
    public List<LessonEntity> parseICalendarToLessons(String data) {
        log.info("Начало парсинга iCalendar данных");

        if (data == null || data.trim().isEmpty()) {
            log.warn("Передана пустая строка для парсинга");
            return Collections.emptyList();
        }

        // ДИАГНОСТИКА: логируем первые 500 символов данных
        log.debug("Первые 500 символов iCal данных: {}",
                data.length() > 500 ? data.substring(0, 500) + "..." : data);

        // ДИАГНОСТИКА: проверяем наличие ключевых элементов
        if (!data.contains("BEGIN:VEVENT")) {
            log.warn("iCal данные не содержат событий VEVENT");
        }
        if (!data.contains("BEGIN:VCALENDAR")) {
            log.warn("iCal данные не содержат VCALENDAR");
        }

        List<LessonEntity> lessons = new ArrayList<>();
        Matcher eventMatcher = EVENT_PATTERN.matcher(data);

        int eventCount = 0;
        int skippedCount = 0;

        while (eventMatcher.find()) {
            String eventBlock = eventBlock = eventMatcher.group(1);
            eventCount++;
            try {
                LessonEntity lesson = parseEventBlock(eventBlock);
                if (lesson != null && isValidLesson(lesson)) {
                    lessons.add(lesson);
                    log.debug("Успешно распарсено занятие: {} - {}",
                            lesson.getDiscipline(), lesson.getStartTime());
                } else {
                    skippedCount++;
                    log.debug("Событие пропущено (невалидное или неделя)");
                }
            } catch (Exception e) {
                log.error("Ошибка при парсинге события {}: {}", eventCount, e.getMessage());
            }
        }

        log.info("Парсинг завершен. Найдено событий: {}, успешно: {}, пропущено: {}",
                eventCount, lessons.size(), skippedCount);

        // ДИАГНОСТИКА: если событий нет, логируем структуру данных
        if (eventCount == 0) {
            log.warn("Не найдено ни одного события VEVENT. Структура данных:");
            String[] lines = data.split("\r\n|\r|\n");
            for (int i = 0; i < Math.min(lines.length, 20); i++) {
                log.warn("Строка {}: {}", i + 1, lines[i]);
            }
        }

        Set<LessonEntity> uniqueLessons = new HashSet<>(lessons);
        return new ArrayList<>(uniqueLessons);
    }

    /**
     * Парсит отдельный блок VEVENT
     */
    private LessonEntity parseEventBlock(String eventBlock) {
        Map<String, String> properties = extractProperties(eventBlock);

        // ДИАГНОСТИКА: логируем SUMMARY для понимания что за событие
        String summary = properties.getOrDefault("SUMMARY", "");
        log.debug("=== Извлеченные свойства ===");
        properties.forEach((key, value) -> {
            if (value.length() > 100) {
                log.debug("  {}: {}...", key, value.substring(0, 100));
            } else {
                log.debug("  {}: {}", key, value);
            }
        });

        // Пропускаем события-недели
        if (isWeekEvent(properties)) {
            log.debug("Событие пропущено: это неделя ('{}')", summary);
            return null;
        }

        LessonEntity lesson = new LessonEntity();

        try {
            // Основная информация
            lesson.setDiscipline(extractDiscipline(properties));
            lesson.setLessonType(extractLessonType(properties));
            lesson.setSummary(summary);
            lesson.setTeacher(extractTeacher(properties));
            lesson.setRoom(properties.getOrDefault("LOCATION", ""));
            lesson.setUid(properties.getOrDefault("UID", UUID.randomUUID().toString()));

            // Время
            LocalDateTime startTime = parseDateTime(properties, "DTSTART");
            LocalDateTime endTime = parseDateTime(properties, "DTEND");
            lesson.setStartTime(startTime);
            lesson.setEndTime(endTime);

            log.debug("Время занятия: {} - {}", startTime, endTime);

            // Группы
            List<StudentGroup> groups = extractGroups(properties);
            lesson.setGroups(groups);
            log.debug("Найдено групп: {}", groups.size());

            // Правила повторения
            RecurrenceRule recurrence = extractRecurrenceRule(properties);
            lesson.setRecurrence(recurrence);

            // Исключения
            List<LocalDate> exceptions = extractExceptions(properties);
            lesson.setExceptions(exceptions);

            // Проверяем валидность
            if (!isValidLesson(lesson)) {
                log.debug("Событие пропущено: невалидное занятие (отсутствуют обязательные поля)");
                log.debug("  discipline: {}, startTime: {}, endTime: {}",
                        lesson.getDiscipline(), lesson.getStartTime(), lesson.getEndTime());
                return null;
            }

            log.debug("Событие успешно обработано: {} - {}", lesson.getDiscipline(), lesson.getStartTime());
            return lesson;

        } catch (Exception e) {
            log.error("Ошибка при обработке события '{}': {}", summary, e.getMessage());
            return null;
        }
    }

    /**
     * Извлекает все свойства из блока события
     */
    /**
     * Извлекает все свойства из блока события (улучшенная версия)
     */
    /**
     * Извлекает все свойства из блока события (исправленная версия)
     */
    private Map<String, String> extractProperties(String eventBlock) {
        Map<String, String> properties = new HashMap<>();

        // Разделяем на строки
        String[] lines = eventBlock.split("\r\n|\r|\n");

        String currentKey = null;
        StringBuilder currentValue = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Проверяем, является ли строка новой property или продолжением
            if (line.contains(":") && !line.startsWith(" ")) {
                // Новая property - сохраняем предыдущую
                if (currentKey != null) {
                    properties.put(currentKey, currentValue.toString().trim());
                }

                // Начинаем новую property
                int colonIndex = line.indexOf(":");
                currentKey = line.substring(0, colonIndex).trim();
                currentValue = new StringBuilder(line.substring(colonIndex + 1).trim());
            } else {
                // Продолжение предыдущей property
                if (currentKey != null) {
                    // Убираем начальный пробел если есть (для folded lines)
                    if (line.startsWith(" ")) {
                        currentValue.append(line.substring(1));
                    } else {
                        currentValue.append(line);
                    }
                }
            }
        }

        // Добавляем последнюю property
        if (currentKey != null) {
            properties.put(currentKey, currentValue.toString().trim());
        }

        log.debug("Извлечено свойств: {}", properties.size());
        log.debug("Найденные ключи: {}", properties.keySet());

        return properties;
    }

    /**
     * Получает последний ключ из Map (для обработки многострочных значений)
     */
    private String getLastKey(Map<String, String> map) {
        if (map.isEmpty()) return null;
        List<String> keys = new ArrayList<>(map.keySet());
        return keys.get(keys.size() - 1);
    }

    /**
     * Проверяет является ли событие неделей (а не занятием)
     */
    private boolean isWeekEvent(Map<String, String> properties) {
        String summary = properties.getOrDefault("SUMMARY", "");
        boolean isWeek = summary.matches("\\d+\\s*неделя");

        // ДИАГНОСТИКА: также проверяем события только с датами (без времени)
        String dtstart = properties.get("DTSTART");
        boolean isAllDay = dtstart != null && !dtstart.contains("T") && dtstart.contains(";VALUE=DATE");

        if (isWeek) {
            log.debug("Событие определено как неделя: '{}'", summary);
        } else if (isAllDay) {
            log.debug("Событие пропущено: это событие на весь день (без времени)");
        }

        return isWeek || isAllDay;
    }

    /**
     * Извлекает название дисциплины
     */
    private String extractDiscipline(Map<String, String> properties) {
        // Пробуем получить из X-META-DISCIPLINE
        String discipline = properties.get("X-META-DISCIPLINE");
        if (discipline != null && !discipline.trim().isEmpty()) {
            return cleanText(discipline);
        }

        // Или из SUMMARY (убираем тип занятия)
        String summary = properties.getOrDefault("SUMMARY", "");
        if (summary.startsWith("ЛК ") || summary.startsWith("ПР ") || summary.startsWith("ЛАБ ")) {
            return cleanText(summary.substring(3));
        }

        return cleanText(summary);
    }

    /**
     * Извлекает тип занятия
     */
    private LessonType extractLessonType(Map<String, String> properties) {
        String categories = properties.getOrDefault("CATEGORIES", "");
        String summary = properties.getOrDefault("SUMMARY", "");

        if (categories.contains("ЛК") || summary.startsWith("ЛК")) {
            return LessonType.LK;
        } else if (categories.contains("ПР") || summary.startsWith("ПР")) {
            return LessonType.PR;
        } else if (categories.contains("ЛАБ") || summary.startsWith("ЛАБ")) {
            return LessonType.LAB;
        }

        return LessonType.LK; // По умолчанию
    }

    /**
     * Извлекает преподавателя
     */
    private String extractTeacher(Map<String, String> properties) {
        // Из DESCRIPTION
        String description = properties.getOrDefault("DESCRIPTION", "");
        if (description.contains("Преподаватель:")) {
            Pattern teacherPattern = Pattern.compile("Преподаватель:\\s*([^\\n\\r]+)");
            Matcher matcher = teacherPattern.matcher(description);
            if (matcher.find()) {
                return cleanText(matcher.group(1));
            }
        }

        // Из X-META-TEACHER
        String teacher = properties.get("X-META-TEACHER");
        if (teacher != null && !teacher.trim().isEmpty()) {
            return cleanText(teacher);
        }

        return "";
    }

    /**
     * Извлекает группы
     */
    private List<StudentGroup> extractGroups(Map<String, String> properties) {
        List<StudentGroup> groups = new ArrayList<>();

        // Из DESCRIPTION
        String description = properties.getOrDefault("DESCRIPTION", "");
        if (description.contains("Группы:")) {
            Pattern groupPattern = Pattern.compile("ИВБО-[\\d-]+");
            Matcher matcher = groupPattern.matcher(description);
            while (matcher.find()) {
                String groupName = matcher.group();
                StudentGroup group = new StudentGroup();
                group.setGroupName(groupName); // Исправлено
                groups.add(group);
            }
        }

        // Из X-META-GROUP свойств
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().startsWith("X-META-GROUP") && entry.getValue().startsWith("ИВБО")) {
                StudentGroup group = new StudentGroup();
                group.setGroupName(entry.getValue()); // Исправлено
                groups.add(group);
            }
        }

        return groups;
    }

    /**
     * Парсит дату и время
     */
    /**
     * Парсит дату и время с учетом временных зон
     */
    /**
     * Парсит дату и время с учетом параметров в ключах
     */
    private LocalDateTime parseDateTime(Map<String, String> properties, String propertyName) {
        // Ищем свойство по имени (может быть с параметрами)
        String dateTimeStr = findPropertyWithParams(properties, propertyName);

        log.debug("Парсинг {}: '{}'", propertyName, dateTimeStr);

        if (dateTimeStr == null) {
            log.debug("{} не найден в свойствах", propertyName);
            return null;
        }

        try {
            String cleanStr = dateTimeStr;

            // Обрабатываем формат с временной зоной: DTSTART;TZID=Europe/Moscow:20250901T090000
            if (cleanStr.contains(":")) {
                cleanStr = cleanStr.substring(cleanStr.indexOf(":") + 1);
            }

            // Убираем символ Z если есть (UTC)
            if (cleanStr.endsWith("Z")) {
                cleanStr = cleanStr.substring(0, cleanStr.length() - 1);
            }

            log.debug("Очищенная строка для парсинга: '{}'", cleanStr);

            // Парсим в зависимости от формата
            if (cleanStr.contains("T")) {
                return LocalDateTime.parse(cleanStr, DATE_TIME_FORMATTER);
            } else {
                // Только дата - устанавливаем время 00:00
                return LocalDateTime.parse(cleanStr + "T000000", DATE_TIME_FORMATTER);
            }
        } catch (Exception e) {
            log.error("Ошибка парсинга даты {}: '{}'. Ошибка: {}", propertyName, dateTimeStr, e.getMessage());
            return null;
        }
    }

    /**
     * Находит свойство по имени, игнорируя параметры
     */
    private String findPropertyWithParams(Map<String, String> properties, String propertyName) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            // Проверяем, начинается ли ключ с искомого имени свойства
            if (key.startsWith(propertyName + ";") || key.equals(propertyName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Извлекает правила повторения
     */
    private RecurrenceRule extractRecurrenceRule(Map<String, String> properties) {
        String rrule = properties.get("RRULE");
        if (rrule == null) return null;

        try {
            String frequency = extractRruleValue(rrule, "FREQ");
            Integer interval = extractRruleIntValue(rrule, "INTERVAL");
            LocalDateTime until = extractRruleUntil(rrule);

            if (frequency != null) {
                RecurrenceRule recurrenceRule = new RecurrenceRule();
                recurrenceRule.setFrequency(frequency);
                recurrenceRule.setInterval(interval);
                recurrenceRule.setUntil(until);
                return recurrenceRule;
            }
        } catch (Exception e) {
            log.error("Ошибка парсинга RRULE: {}", rrule, e);
        }

        return null;
    }

    /**
     * Извлекает исключения (EXDATE)
     */
    private List<LocalDate> extractExceptions(Map<String, String> properties) {
        List<LocalDate> exceptions = new ArrayList<>();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().startsWith("EXDATE")) {
                try {
                    String dateStr = entry.getValue();
                    if (dateStr.contains(":")) {
                        dateStr = dateStr.substring(dateStr.indexOf(":") + 1);
                    }
                    LocalDateTime dateTime = parseDateTime(Collections.singletonMap("DT", dateStr), "DT");
                    if (dateTime != null) {
                        exceptions.add(dateTime.toLocalDate());
                    }
                } catch (Exception e) {
                    log.error("Ошибка парсинга EXDATE: {}", entry.getValue(), e);
                }
            }
        }

        return exceptions;
    }

    /**
     * Вспомогательные методы для парсинга RRULE
     */
    private String extractRruleValue(String rrule, String key) {
        Pattern pattern = Pattern.compile(key + "=([^;]+)");
        Matcher matcher = pattern.matcher(rrule);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Integer extractRruleIntValue(String rrule, String key) {
        String value = extractRruleValue(rrule, key);
        return value != null ? Integer.parseInt(value) : null;
    }

    private LocalDateTime extractRruleUntil(String rrule) {
        String untilStr = extractRruleValue(rrule, "UNTIL");
        if (untilStr != null) {
            try {
                log.debug("Парсинг UNTIL значения: {}", untilStr);

                // Обработка различных форматов даты
                if (untilStr.endsWith("Z")) {
                    // UTC время - убираем 'Z'
                    untilStr = untilStr.substring(0, untilStr.length() - 1);
                }

                // Проверяем, содержит ли время (символ 'T')
                if (untilStr.contains("T")) {
                    return LocalDateTime.parse(untilStr, DATE_TIME_FORMATTER);
                } else {
                    // Только дата - добавляем время 23:59:59 для конца дня
                    return LocalDateTime.parse(untilStr + "T235959", DATE_TIME_FORMATTER);
                }
            } catch (Exception e) {
                log.warn("Не удалось распарсить UNTIL значение '{}'. Продолжаем без правила повторения.", untilStr);
            }
        }
        return null;
    }

    /**
     * Очищает текст от лишних пробелов и переносов
     */
    private String cleanText(String text) {
        if (text == null) return "";
        return text.replaceAll("\\s+", " ").trim();
    }

    /**
     * Проверяет валидность занятия
     */
    private boolean isValidLesson(LessonEntity lesson) {
        boolean isValid = lesson.getStartTime() != null &&
                lesson.getEndTime() != null &&
                lesson.getDiscipline() != null &&
                !lesson.getDiscipline().trim().isEmpty();

        if (!isValid) {
            log.debug("Проверка валидности занятия провалена:");
            log.debug("  startTime: {}", lesson.getStartTime());
            log.debug("  endTime: {}", lesson.getEndTime());
            log.debug("  discipline: '{}'", lesson.getDiscipline());
        }

        return isValid;
    }
}