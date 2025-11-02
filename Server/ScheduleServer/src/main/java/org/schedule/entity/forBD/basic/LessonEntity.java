package org.schedule.entity.forBD.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.schedule.entity.forBD.LessonType;
import org.schedule.entity.forBD.RecurrenceRule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lessons")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LessonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discipline", nullable = false)
    private String discipline;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type", nullable = false)
    private LessonType lessonType;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "room")
    private String room;

    @Column(name = "teacher")
    private String teacher;

    // Удалено поле summary, добавлено поле для хранения групп в виде строки
    @Column(name = "groups_summary")
    private String groupsSummary; // Строка с названиями групп для быстрого доступа

    @Column(name = "description")
    private String description;

    @Column(name = "uid", unique = true)
    private String uid;

    @ManyToMany
    @JoinTable(
            name = "lesson_group",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @ToString.Exclude
    @JsonIgnore // Игнорируем при сериализации в JSON
    private List<GroupEntity> groups = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "lesson_teacher",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    @ToString.Exclude
    @JsonIgnore // Игнорируем при сериализации в JSON
    private List<TeacherEntity> teachers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "lesson_room",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    @ToString.Exclude
    @JsonIgnore // Игнорируем при сериализации в JSON
    private List<RoomEntity> rooms = new ArrayList<>();

    @Embedded
    private RecurrenceRule recurrence;

    @ElementCollection
    @CollectionTable(name = "lesson_exceptions", joinColumns = @JoinColumn(name = "lesson_id"))
    @Column(name = "exception_date")
    private List<LocalDate> exceptions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonEntity that = (LessonEntity) o;
        return Objects.equals(discipline, that.discipline) &&
                lessonType == that.lessonType &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(room, that.room) &&
                Objects.equals(teacher, that.teacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discipline, lessonType, startTime, endTime, room, teacher);
    }

    public List<GroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupEntity> groups) {
        this.groups = groups;
        // Автоматически обновляем groupsSummary при установке групп
        if (groups != null && !groups.isEmpty()) {
            this.groupsSummary = groups.stream()
                    .map(GroupEntity::getGroupName)
                    .reduce((g1, g2) -> g1 + ", " + g2)
                    .orElse("");
        } else {
            this.groupsSummary = "";
        }
    }

    public RecurrenceRule getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(RecurrenceRule recurrence) {
        this.recurrence = recurrence;
    }

    public List<LocalDate> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<LocalDate> exceptions) {
        this.exceptions = exceptions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getGroupsSummary() {
        return groupsSummary;
    }

    public void setGroupsSummary(String groupsSummary) {
        this.groupsSummary = groupsSummary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<TeacherEntity> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherEntity> teachers) {
        this.teachers = teachers;
    }

    public List<RoomEntity> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomEntity> rooms) {
        this.rooms = rooms;
    }
}