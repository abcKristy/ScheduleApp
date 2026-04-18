package org.schedule.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ScheduleMetadataResponseDto {

    private String entityType;
    private String entityName;
    private String semester;
    private String currentSemester;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdated;

    private Integer lessonCount;
    private boolean needsUpdate;
    private String displayName;

    public ScheduleMetadataResponseDto() {}

    public ScheduleMetadataResponseDto(String entityType, String entityName,
                                       String semester, String currentSemester,
                                       LocalDateTime lastUpdated, Integer lessonCount) {
        this.entityType = entityType;
        this.entityName = entityName;
        this.semester = semester;
        this.currentSemester = currentSemester;
        this.lastUpdated = lastUpdated;
        this.lessonCount = lessonCount;
        this.needsUpdate = !currentSemester.equals(semester);
    }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(String currentSemester) { this.currentSemester = currentSemester; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public Integer getLessonCount() { return lessonCount; }
    public void setLessonCount(Integer lessonCount) { this.lessonCount = lessonCount; }

    public boolean isNeedsUpdate() { return needsUpdate; }
    public void setNeedsUpdate(boolean needsUpdate) { this.needsUpdate = needsUpdate; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}