package org.schedule.entity.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MireaScheduleData {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("scheduleTarget")
    private Integer scheduleTarget;

    @JsonProperty("isStatic")
    private Boolean isStatic;

    @JsonProperty("personalScheduleId")
    private Long personalScheduleId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("iCalContent")
    private String iCalContent;

    @JsonProperty("iCalLink")
    private String iCalLink;

    @JsonProperty("scheduleImageLink")
    private String scheduleImageLink;

    @JsonProperty("scheduleUpdateImageLink")
    private String scheduleUpdateImageLink;

    public MireaScheduleData() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getScheduleTarget() { return scheduleTarget; }
    public void setScheduleTarget(Integer scheduleTarget) { this.scheduleTarget = scheduleTarget; }

    public Boolean getIsStatic() { return isStatic; }
    public void setIsStatic(Boolean isStatic) { this.isStatic = isStatic; }

    public Long getPersonalScheduleId() { return personalScheduleId; }
    public void setPersonalScheduleId(Long personalScheduleId) { this.personalScheduleId = personalScheduleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getICalContent() { return iCalContent; }
    public void setICalContent(String iCalContent) { this.iCalContent = iCalContent; }

    public String getICalLink() { return iCalLink; }
    public void setICalLink(String iCalLink) { this.iCalLink = iCalLink; }

    public String getScheduleImageLink() { return scheduleImageLink; }
    public void setScheduleImageLink(String scheduleImageLink) { this.scheduleImageLink = scheduleImageLink; }

    public String getScheduleUpdateImageLink() { return scheduleUpdateImageLink; }
    public void setScheduleUpdateImageLink(String scheduleUpdateImageLink) { this.scheduleUpdateImageLink = scheduleUpdateImageLink; }
}