package org.schedule.entity.apidata;

public class ResponseDto {
    private Long id;
    private String fullTitle;
    private Integer target;
    private String iCalLink;

    public ResponseDto() {}

    public ResponseDto(Long id, String fullTitle, Integer target, String iCalLink) {
        this.id = id;
        this.fullTitle = fullTitle;
        this.target = target;
        this.iCalLink = iCalLink;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullTitle() { return fullTitle; }
    public void setFullTitle(String fullTitle) { this.fullTitle = fullTitle; }

    public Integer getTarget() { return target; }
    public void setTarget(Integer target) { this.target = target; }


    public String getiCalLink() { return iCalLink; }
    public void setiCalLink(String iCalLink) { this.iCalLink = iCalLink; }

    @Override
    public String toString() {
        return "ResponseDto{" +
                "id=" + id +
                ", fullTitle='" + fullTitle + '\'' +
                ", target=" + target +
                '}';
    }
}