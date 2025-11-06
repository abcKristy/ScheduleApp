package org.schedule.mapping;

import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.forBD.LessonType;
import org.schedule.entity.forBD.RecurrenceRule;
import org.schedule.entity.forBD.basic.GroupEntity;
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
    private static final Logger log = LoggerFactory.getLogger(ParserToLesson.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final Pattern EVENT_PATTERN = Pattern.compile("BEGIN:VEVENT(.*?)END:VEVENT", Pattern.DOTALL);

    public List<LessonEntity> parseICalendarToLessons(String data, String scheduleTitle) {
        log.info("Вход в parseICalendarToLessons, заголовок: {}", scheduleTitle);

        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Данные для парсинга не могут быть пустыми");
        }

        List<LessonEntity> lessons = new ArrayList<>();
        Matcher eventMatcher = EVENT_PATTERN.matcher(data);

        int eventCount = 0;
        int skippedCount = 0;

        while (eventMatcher.find()) {
            String eventBlock = eventMatcher.group(1);
            eventCount++;
            try {
                LessonEntity lesson = parseEventBlock(eventBlock, scheduleTitle);
                if (lesson != null && isValidLesson(lesson)) {
                    lessons.add(lesson);
                } else {
                    skippedCount++;
                }
            } catch (Exception e) {
                log.error("Ошибка при парсинге события {}", eventCount, e);
            }
        }

        Set<LessonEntity> uniqueLessons = new HashSet<>(lessons);
        log.info("Выход из parseICalendarToLessons, результат: {} уникальных занятий из {} событий",
                uniqueLessons.size(), eventCount);

        return new ArrayList<>(uniqueLessons);
    }

    private LessonEntity parseEventBlock(String eventBlock, String scheduleTitle) {
        Map<String, String> properties = extractProperties(eventBlock);

        if (isWeekEvent(properties)) {
            return null;
        }

        LessonEntity lesson = new LessonEntity();

        try {
            lesson.setDiscipline(extractDiscipline(properties));
            lesson.setLessonType(extractLessonType(properties));
            lesson.setTeacher(extractTeacher(properties, scheduleTitle));
            lesson.setRoom(properties.getOrDefault("LOCATION", ""));

            LocalDateTime startTime = parseDateTime(properties, "DTSTART");
            LocalDateTime endTime = parseDateTime(properties, "DTEND");
            lesson.setStartTime(startTime);
            lesson.setEndTime(endTime);

            List<GroupEntity> groups = extractGroups(properties);
            lesson.setGroups(groups);

            RecurrenceRule recurrence = extractRecurrenceRule(properties);
            lesson.setRecurrence(recurrence);

            List<LocalDate> exceptions = extractExceptions(properties);
            lesson.setExceptions(exceptions);

            if (!isValidLesson(lesson)) {
                return null;
            }

            return lesson;

        } catch (Exception e) {
            log.error("Ошибка при обработке события", e);
            return null;
        }
    }

    private Map<String, String> extractProperties(String eventBlock) {
        Map<String, String> properties = new HashMap<>();

        String[] lines = eventBlock.split("\r\n");

        String currentKey = null;
        StringBuilder currentValue = new StringBuilder();

        for (String line : lines) {
            if (line.isEmpty()) continue;

            if (line.contains(":") && !line.startsWith(" ")) {
                if (currentKey != null) {
                    properties.put(currentKey, currentValue.toString());
                }

                int colonIndex = line.indexOf(":");
                currentKey = line.substring(0, colonIndex).trim();
                currentValue = new StringBuilder(line.substring(colonIndex + 1));
            } else {
                if (currentKey != null) {
                    if (line.startsWith(" ")) {
                        String continuation = line.substring(1);
                        currentValue.append(continuation);
                    } else {
                        if (currentValue.length() > 0 && !endsWithWhitespace(currentValue)) {
                            currentValue.append(" ");
                        }
                        currentValue.append(line);
                    }
                }
            }
        }

        if (currentKey != null) {
            properties.put(currentKey, currentValue.toString());
        }

        return properties;
    }

    private boolean endsWithWhitespace(StringBuilder sb) {
        if (sb.length() == 0) return false;
        char lastChar = sb.charAt(sb.length() - 1);
        return Character.isWhitespace(lastChar);
    }

    private boolean isWeekEvent(Map<String, String> properties) {
        String summary = properties.getOrDefault("SUMMARY", "");
        boolean isWeek = summary.matches("\\d+\\s*неделя");

        String dtstart = properties.get("DTSTART");
        boolean isAllDay = dtstart != null && !dtstart.contains("T") && dtstart.contains(";VALUE=DATE");

        return isWeek || isAllDay;
    }

    private String extractDiscipline(Map<String, String> properties) {
        String discipline = properties.get("X-META-DISCIPLINE");
        if (discipline != null && !discipline.trim().isEmpty()) {
            return cleanText(discipline);
        }

        String summary = properties.getOrDefault("SUMMARY", "");
        String cleanSummary = summary;

        if (cleanSummary.startsWith("ЛК ") || cleanSummary.startsWith("ПР ") || cleanSummary.startsWith("ЛАБ ")) {
            cleanSummary = cleanSummary.substring(3);
        }

        cleanSummary = cleanSummary.replaceAll("\\([А-ЯЁ][а-яё]+\\s+[А-ЯЁ]\\.\\s*[А-ЯЁ]\\.\\)$", "").trim();
        cleanSummary = cleanSummary.replaceAll("\\([А-ЯЁ][а-яё]+\\s+[А-ЯЁ][а-яё]+\\s+[А-ЯЁ][а-яё]+\\)$", "").trim();

        return cleanText(cleanSummary);
    }

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

        return LessonType.LK;
    }

    private String extractTeacher(Map<String, String> properties, String scheduleTitle) {
        String teacher = properties.get("X-META-TEACHER");
        if (teacher != null && !teacher.trim().isEmpty()) {
            return cleanText(teacher);
        }

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("X-META-TEACHER") && !entry.getValue().isEmpty()) {
                String teacherValue = entry.getValue().trim();
                if (!teacherValue.isEmpty()) {
                    return cleanText(teacherValue);
                }
            }
        }

        String summary = properties.getOrDefault("SUMMARY", "");
        if (summary.contains("(") && summary.contains(")")) {
            Pattern teacherInSummaryPattern = Pattern.compile("\\(([А-ЯЁ][а-яё]+\\s+[А-ЯЁ]\\.\\s*[А-ЯЁ]\\.)\\)");
            Matcher matcher = teacherInSummaryPattern.matcher(summary);
            if (matcher.find()) {
                return cleanText(matcher.group(1));
            }
        }

        if (isLikelyTeacherName(scheduleTitle)) {
            return cleanText(scheduleTitle);
        }

        return "";
    }

    private boolean isLikelyTeacherName(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        String cleanText = text.trim();

        if (cleanText.matches("[А-ЯЁ][а-яё]+\\s+[А-ЯЁ]\\.\\s*[А-ЯЁ]\\.")) {
            return true;
        }

        String[] words = cleanText.split("\\s+");
        if (words.length >= 2) {
            return Arrays.stream(words)
                    .allMatch(word -> !word.isEmpty() && Character.isUpperCase(word.charAt(0)));
        }

        return false;
    }

    private List<GroupEntity> extractGroups(Map<String, String> properties) {
        List<GroupEntity> groups = new ArrayList<>();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().startsWith("X-META-GROUP") && !entry.getValue().trim().isEmpty()) {
                String groupName = entry.getValue().trim();
                if (groupName.matches("[А-ЯA-Z]{2,10}-[\\d-]+")) {
                    GroupEntity group = new GroupEntity();
                    group.setGroupName(groupName);
                    groups.add(group);
                }
            }
        }

        return groups;
    }

    private LocalDateTime parseDateTime(Map<String, String> properties, String propertyName) {
        String dateTimeStr = findPropertyWithParams(properties, propertyName);

        if (dateTimeStr == null) {
            return null;
        }

        try {
            String cleanStr = dateTimeStr;

            if (cleanStr.contains(":")) {
                cleanStr = cleanStr.substring(cleanStr.indexOf(":") + 1);
            }

            if (cleanStr.endsWith("Z")) {
                cleanStr = cleanStr.substring(0, cleanStr.length() - 1);
            }

            if (cleanStr.contains("T")) {
                return LocalDateTime.parse(cleanStr, DATE_TIME_FORMATTER);
            } else {
                return LocalDateTime.parse(cleanStr + "T000000", DATE_TIME_FORMATTER);
            }
        } catch (Exception e) {
            log.error("Ошибка парсинга даты {}", propertyName);
            return null;
        }
    }

    private String findPropertyWithParams(Map<String, String> properties, String propertyName) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(propertyName + ";") || key.equals(propertyName)) {
                return entry.getValue();
            }
        }
        return null;
    }

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
            log.error("Ошибка парсинга RRULE");
        }

        return null;
    }

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
                    log.error("Ошибка парсинга EXDATE");
                }
            }
        }

        return exceptions;
    }

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
                if (untilStr.endsWith("Z")) {
                    untilStr = untilStr.substring(0, untilStr.length() - 1);
                }

                if (untilStr.contains("T")) {
                    return LocalDateTime.parse(untilStr, DATE_TIME_FORMATTER);
                } else {
                    return LocalDateTime.parse(untilStr + "T235959", DATE_TIME_FORMATTER);
                }
            } catch (Exception e) {
                log.warn("Не удалось распарсить UNTIL значение");
            }
        }
        return null;
    }

    private String cleanText(String text) {
        if (text == null) return "";
        return text.replaceAll("\\s+", " ").trim();
    }

    private boolean isValidLesson(LessonEntity lesson) {
        return lesson.getStartTime() != null &&
                lesson.getEndTime() != null &&
                lesson.getDiscipline() != null &&
                !lesson.getDiscipline().trim().isEmpty();
    }
}