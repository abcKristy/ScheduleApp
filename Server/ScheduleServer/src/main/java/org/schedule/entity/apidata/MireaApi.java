package org.schedule.entity.apidata;

import java.util.List;

public class MireaApi {
    private List<MireaApiData> data;
    private String nextPageToken;
    public MireaApi() {}

    public MireaApi(List<MireaApiData> data, String nextPageToken) {
        this.data = data;
        this.nextPageToken = nextPageToken;
    }

    public List<MireaApiData> getData() { return data; }
    public void setData(List<MireaApiData> data) { this.data = data; }

    public String getNextPageToken() { return nextPageToken; }
    public void setNextPageToken(String nextPageToken) { this.nextPageToken = nextPageToken; }
}
