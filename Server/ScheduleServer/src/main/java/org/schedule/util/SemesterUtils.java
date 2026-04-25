package org.schedule.util;

import java.time.LocalDate;
import java.time.Month;

public class SemesterUtils {

    private static final String SPRING = "SPRING";
    private static final String AUTUMN = "AUTUMN";
    private static final String LEGACY = "LEGACY";

    public static String getCurrentSemester(LocalDate date) {
        int year = date.getYear();
        Month month = date.getMonth();

        if (month.getValue() >= Month.SEPTEMBER.getValue()) {
            return year + "-" + AUTUMN;
        }

        if (month == Month.JANUARY) {
            return (year - 1) + "-" + AUTUMN;
        }

        if (month.getValue() >= Month.FEBRUARY.getValue() &&
                month.getValue() <= Month.JUNE.getValue()) {
            return year + "-" + SPRING;
        }

        return year + "-" + SPRING;
    }

    public static String getCurrentSemester() {
        return getCurrentSemester(LocalDate.now());
    }

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

    public static boolean isDateInSemester(LocalDate date, String semester) {
        LocalDate start = getSemesterStartDate(semester);
        LocalDate end = getSemesterEndDate(semester);
        return !date.isBefore(start) && !date.isAfter(end);
    }

    public static boolean isSameSemester(String semester1, String semester2) {
        if (semester1 == null || semester2 == null) return false;
        return semester1.equals(semester2);
    }

    public static boolean isOutdated(String semester) {
        return !isSameSemester(semester, getCurrentSemester());
    }

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