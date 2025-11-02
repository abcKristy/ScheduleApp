package org.schedule.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.schedule.entity.forBD.LessonType;

import java.time.LocalDateTime;
import java.util.List;

public class ScheduleResponseDto {
    private Long id;
    private String discipline;
    private LessonType lessonType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    private String room;
    private String teacher;
    private List<String> groups;
    private String groupsSummary;
    private String description;

    // Конструкторы
    public ScheduleResponseDto() {}

    public ScheduleResponseDto(Long id, String discipline, LessonType lessonType,
                               LocalDateTime startTime, LocalDateTime endTime,
                               String room, String teacher, List<String> groups,
                               String groupsSummary, String description) {
        this.id = id;
        this.discipline = discipline;
        this.lessonType = lessonType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.teacher = teacher;
        this.groups = groups;
        this.groupsSummary = groupsSummary;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDiscipline() { return discipline; }
    public void setDiscipline(String discipline) { this.discipline = discipline; }

    public LessonType getLessonType() { return lessonType; }
    public void setLessonType(LessonType lessonType) { this.lessonType = lessonType; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }

    public List<String> getGroups() { return groups; }
    public void setGroups(List<String> groups) { this.groups = groups; }

    public String getGroupsSummary() { return groupsSummary; }
    public void setGroupsSummary(String groupsSummary) { this.groupsSummary = groupsSummary; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}