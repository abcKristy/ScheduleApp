package org.schedule.entity;

public class ScheduleDto {
    private Long id;
    private Integer scheduleTarget;
    private String title;
    private String iCalContent;

    public ScheduleDto() {}

    public ScheduleDto(Long id, Integer scheduleTarget, String title, String iCalContent) {
        this.id = id;
        this.scheduleTarget = scheduleTarget;
        this.title = title;
        this.iCalContent = iCalContent;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getScheduleTarget() { return scheduleTarget; }
    public void setScheduleTarget(Integer scheduleTarget) { this.scheduleTarget = scheduleTarget; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getICalContent() { return iCalContent; }
    public void setICalContent(String iCalContent) { this.iCalContent = iCalContent; }

    @Override
    public String toString() {
        return "ScheduleDto{" +
                "id=" + id +
                ", scheduleTarget=" + scheduleTarget +
                ", title='" + title + '\'' +
                ", iCalContent='" + (iCalContent != null ? iCalContent.substring(0, Math.min(50, iCalContent.length())) + "..." : "null") + '\'' +
                '}';
    }
}
