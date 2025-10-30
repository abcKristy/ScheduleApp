package org.schedule.entity.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MireaSchedule {
    @JsonProperty("pageProps")
    private PageProps pageProps;

    @JsonProperty("__N_SSP")
    private Boolean nSsp;

    public PageProps getPageProps() { return pageProps; }
    public void setPageProps(PageProps pageProps) { this.pageProps = pageProps; }

    public Boolean getNSsp() { return nSsp; }
    public void setNSsp(Boolean nSsp) { this.nSsp = nSsp; }

    public static class PageProps {
        @JsonProperty("scheduleLoadInfo")
        private List<MireaScheduleData> scheduleLoadInfo;

        public List<MireaScheduleData> getScheduleLoadInfo() { return scheduleLoadInfo; }
        public void setScheduleLoadInfo(List<MireaScheduleData> scheduleLoadInfo) { this.scheduleLoadInfo = scheduleLoadInfo; }
    }
}
