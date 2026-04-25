package org.schedule.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.schedule.entity.forBD.LessonType;
import org.schedule.entity.forBD.RecurrenceRule;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public class ScheduleResponseDto {
    private String discipline;
    private LessonType lessonType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    private List<String> rooms;
    private List<String> teachers;
    private List<String> groups;
    private String groupsSummary;
    private String description;

    // Новые поля
    private RecurrenceRule recurrence;
    private List<LocalDate> exceptions;

    // Конструкторы
    public ScheduleResponseDto() {}

    public ScheduleResponseDto(String discipline, LessonType lessonType,
                               LocalDateTime startTime, LocalDateTime endTime,
                               List<String> rooms, List<String> teachers, List<String> groups,
                               String groupsSummary, String description) {
        this.discipline = discipline;
        this.lessonType = lessonType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rooms = rooms;
        this.teachers = teachers;
        this.groups = groups;
        this.groupsSummary = groupsSummary;
        this.description = description;
    }

    // Полный конструктор со всеми полями
    public ScheduleResponseDto(String discipline, LessonType lessonType,
                               LocalDateTime startTime, LocalDateTime endTime,
                               List<String> rooms, List<String> teachers, List<String> groups,
                               String groupsSummary, String description,
                               RecurrenceRule recurrence, List<LocalDate> exceptions) {
        this.discipline = discipline;
        this.lessonType = lessonType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rooms = rooms;
        this.teachers = teachers;
        this.groups = groups;
        this.groupsSummary = groupsSummary;
        this.description = description;
        this.recurrence = recurrence;
        this.exceptions = exceptions;
    }

    // Getters and Setters
    public String getDiscipline() { return discipline; }
    public void setDiscipline(String discipline) { this.discipline = discipline; }

    public LessonType getLessonType() { return lessonType; }
    public void setLessonType(LessonType lessonType) { this.lessonType = lessonType; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public List<String> getRooms() { return rooms; }
    public void setRooms(List<String> rooms) { this.rooms = rooms; }

    public List<String> getTeachers() { return teachers; }
    public void setTeachers(List<String> teachers) { this.teachers = teachers; }
    public List<String> getGroups() { return groups; }
    public void setGroups(List<String> groups) { this.groups = groups; }

    public String getGroupsSummary() { return groupsSummary; }
    public void setGroupsSummary(String groupsSummary) { this.groupsSummary = groupsSummary; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Новые геттеры и сеттеры
    public RecurrenceRule getRecurrence() { return recurrence; }
    public void setRecurrence(RecurrenceRule recurrence) { this.recurrence = recurrence; }

    public List<LocalDate> getExceptions() { return exceptions; }
    public void setExceptions(List<LocalDate> exceptions) { this.exceptions = exceptions; }
}