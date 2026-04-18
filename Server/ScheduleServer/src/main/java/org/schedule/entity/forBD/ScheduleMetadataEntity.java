package org.schedule.entity.forBD;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_metadata",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"entity_type", "entity_name", "semester"})
        })
public class ScheduleMetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 20)
    private String entityType;  // "GROUP", "TEACHER", "ROOM"

    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @Column(name = "semester", nullable = false, length = 20)
    private String semester;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Column(name = "lesson_count")
    private Integer lessonCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Конструкторы
    public ScheduleMetadataEntity() {}

    public ScheduleMetadataEntity(String entityType, String entityName, String semester) {
        this.entityType = entityType;
        this.entityName = entityName;
        this.semester = semester;
        this.lastUpdated = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public Integer getLessonCount() { return lessonCount; }
    public void setLessonCount(Integer lessonCount) { this.lessonCount = lessonCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}