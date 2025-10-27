package org.schedule.entity;

import java.util.List;

public class MireaApiResponse {
    private List<MireaScheduleData> data;
    private String nextPageToken;

    // constructors, getters, setters
    public MireaApiResponse() {}

    public MireaApiResponse(List<MireaScheduleData> data, String nextPageToken) {
        this.data = data;
        this.nextPageToken = nextPageToken;
    }

    public List<MireaScheduleData> getData() { return data; }
    public void setData(List<MireaScheduleData> data) { this.data = data; }

    public String getNextPageToken() { return nextPageToken; }
    public void setNextPageToken(String nextPageToken) { this.nextPageToken = nextPageToken; }
}
