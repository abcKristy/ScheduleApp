package org.schedule.entity;

public class ResponseDto {
    private Long id;
    private String targetTitle;
    private String fullTitle;

    // constructors, getters, setters
    public ResponseDto() {}

    public ResponseDto(Long id, String targetTitle, String fullTitle) {
        this.id = id;
        this.targetTitle = targetTitle;
        this.fullTitle = fullTitle;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTargetTitle() { return targetTitle; }
    public void setTargetTitle(String targetTitle) { this.targetTitle = targetTitle; }

    public String getFullTitle() { return fullTitle; }
    public void setFullTitle(String fullTitle) { this.fullTitle = fullTitle; }

    @Override
    public String toString() {
        return "ScheduleResponseDto{" +
                "id=" + id +
                ", targetTitle='" + targetTitle + '\'' +
                ", fullTitle='" + fullTitle + '\'' +
                '}';
    }
}
