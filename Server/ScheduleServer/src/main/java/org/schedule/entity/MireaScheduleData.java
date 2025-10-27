package org.schedule.entity;

public class MireaScheduleData {
    private Long id;
    private String targetTitle;
    private String fullTitle;
    private Integer scheduleTarget;
    private String iCalLink;
    private String scheduleImageLink;
    private String scheduleUpdateImageLink;
    private String scheduleUIAddToCalendarLink;

    public MireaScheduleData() {}

    public MireaScheduleData(Long id, String targetTitle, String fullTitle, Integer scheduleTarget) {
        this.id = id;
        this.targetTitle = targetTitle;
        this.fullTitle = fullTitle;
        this.scheduleTarget = scheduleTarget;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTargetTitle() { return targetTitle; }
    public void setTargetTitle(String targetTitle) { this.targetTitle = targetTitle; }

    public String getFullTitle() { return fullTitle; }
    public void setFullTitle(String fullTitle) { this.fullTitle = fullTitle; }

    public Integer getScheduleTarget() { return scheduleTarget; }
    public void setScheduleTarget(Integer scheduleTarget) { this.scheduleTarget = scheduleTarget; }

    public String getICalLink() { return iCalLink; }
    public void setICalLink(String iCalLink) { this.iCalLink = iCalLink; }

    public String getScheduleImageLink() { return scheduleImageLink; }
    public void setScheduleImageLink(String scheduleImageLink) { this.scheduleImageLink = scheduleImageLink; }

    public String getScheduleUpdateImageLink() { return scheduleUpdateImageLink; }
    public void setScheduleUpdateImageLink(String scheduleUpdateImageLink) { this.scheduleUpdateImageLink = scheduleUpdateImageLink; }

    public String getScheduleUIAddToCalendarLink() { return scheduleUIAddToCalendarLink; }
    public void setScheduleUIAddToCalendarLink(String scheduleUIAddToCalendarLink) { this.scheduleUIAddToCalendarLink = scheduleUIAddToCalendarLink; }
}