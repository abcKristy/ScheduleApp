package org.schedule.mapping;

import org.schedule.entity.MireaApiResponse;
import org.schedule.entity.MireaScheduleData;
import org.schedule.entity.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleMapper {
    private static final Logger log = LoggerFactory.getLogger(ScheduleMapper.class);
    private final RestTemplate restTemplate;

    public ScheduleMapper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ResponseDto> mapToResponseDto(List<String> titleList, String mireaApiUrl) {
        log.info("Mapping {} titles", titleList.size());

        List<ResponseDto> result = new ArrayList<>();

        for (String title : titleList) {
            try {
                String apiUrl = mireaApiUrl + title;

                ResponseEntity<MireaApiResponse> response = restTemplate.getForEntity(
                        apiUrl,
                        MireaApiResponse.class
                );

                MireaApiResponse apiResponse = response.getBody();

                if (apiResponse == null || apiResponse.getData() == null || apiResponse.getData().isEmpty()) {
                    log.warn("No data for title: '{}'", title);
                    continue;
                }

                for (MireaScheduleData scheduleData : apiResponse.getData()) {
                    ResponseDto responseDto = new ResponseDto(
                            scheduleData.getId(),
                            scheduleData.getFullTitle(),
                            scheduleData.getScheduleTarget()  // scheduleTarget -> target
                    );
                    result.add(responseDto);
                    log.info("Created: {}", responseDto);
                }

            } catch (Exception e) {
                log.error("Error processing title: '{}'", title, e);
            }
        }

        log.info("Mapping completed. Created {} objects", result.size());
        return result;
    }
}