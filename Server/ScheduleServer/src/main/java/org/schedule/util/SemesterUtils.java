package org.schedule.util;

import java.time.LocalDate;
import java.time.Month;

/**
 * Утилитный класс для работы с семестрами
 */
public class SemesterUtils {

    // Формат семестра: "2026-SPRING" или "2025-AUTUMN"
    private static final String SPRING = "SPRING";
    private static final String AUTUMN = "AUTUMN";
    private static final String LEGACY = "LEGACY";

    /**
     * Определяет текущий семестр на основе даты
     *
     * @param date дата, для которой нужно определить семестр
     * @return строка с идентификатором семестра
     */
    public static String getCurrentSemester(LocalDate date) {
        int year = date.getYear();
        Month month = date.getMonth();

        // Осенний семестр: сентябрь - январь (следующего года)
        if (month.getValue() >= Month.SEPTEMBER.getValue()) {
            return year + "-" + AUTUMN;
        }

        // Осенний семестр (январь относится к прошлому году)
        if (month == Month.JANUARY) {
            return (year - 1) + "-" + AUTUMN;
        }

        // Весенний семестр: февраль - июнь
        if (month.getValue() >= Month.FEBRUARY.getValue() &&
                month.getValue() <= Month.JUNE.getValue()) {
            return year + "-" + SPRING;
        }

        // Летние каникулы (июль, август) - возвращаем последний завершившийся семестр
        return year + "-" + SPRING;
    }

    /**
     * Определяет текущий семестр на основе текущей даты
     */
    public static String getCurrentSemester() {
        return getCurrentSemester(LocalDate.now());
    }

    /**
     * Возвращает дату начала семестра
     */
    public static LocalDate getSemesterStartDate(String semester) {
        String[] parts = semester.split("-");
        int year = Integer.parseInt(parts[0]);
        String type = parts[1];

        if (SPRING.equals(type)) {
            return LocalDate.of(year, 2, 1);
        } else {
            return LocalDate.of(year, 9, 1);
        }
    }

    /**
     * Возвращает дату окончания семестра
     */
    public static LocalDate getSemesterEndDate(String semester) {
        String[] parts = semester.split("-");
        int year = Integer.parseInt(parts[0]);
        String type = parts[1];

        if (SPRING.equals(type)) {
            return LocalDate.of(year, 6, 30);
        } else {
            return LocalDate.of(year + 1, 1, 31);
        }
    }

    /**
     * Проверяет, что дата находится в указанном семестре
     */
    public static boolean isDateInSemester(LocalDate date, String semester) {
        LocalDate start = getSemesterStartDate(semester);
        LocalDate end = getSemesterEndDate(semester);
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Сравнивает два семестра
     * @return true, если семестры одинаковые
     */
    public static boolean isSameSemester(String semester1, String semester2) {
        if (semester1 == null || semester2 == null) return false;
        return semester1.equals(semester2);
    }

    /**
     * Проверяет, является ли семестр устаревшим (не совпадает с текущим)
     */
    public static boolean isOutdated(String semester) {
        return !isSameSemester(semester, getCurrentSemester());
    }

    /**
     * Возвращает читаемое название семестра
     */
    public static String getDisplayName(String semester) {
        if (LEGACY.equals(semester)) {
            return "Архив";
        }

        String[] parts = semester.split("-");
        String year = parts[0];
        String type = parts[1];

        String season = SPRING.equals(type) ? "Весенний семестр" : "Осенний семестр";

        if (AUTUMN.equals(type)) {
            int nextYear = Integer.parseInt(year) + 1;
            return season + " " + year + "/" + nextYear;
        }

        return season + " " + year;
    }
}