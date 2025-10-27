package org.schedule.mapping;

import org.schedule.entity.MireaApiResponse;
import org.schedule.entity.MireaScheduleData;
import org.schedule.entity.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
        log.info("Starting mapping for {} titles: {}", titleList.size(), titleList);

        List<ResponseDto> result = new ArrayList<>();

        for (String title : titleList) {
            try {
                String apiUrl = mireaApiUrl + title;
                // Получаем ответ от API
                ResponseEntity<MireaApiResponse> response = restTemplate.getForEntity(
                        apiUrl,
                        MireaApiResponse.class
                );

                MireaApiResponse apiResponse = response.getBody();

                // ПРАВИЛЬНАЯ ПРОВЕРКА - data может быть пустым массивом, но не null
                if (apiResponse == null) {
                    log.warn("API response is null for title: '{}'", title);
                    continue;
                }

                if (apiResponse.getData() == null) {
                    log.warn("Data field is null for title: '{}'", title);
                    continue;
                }

                if (apiResponse.getData().isEmpty()) {
                    log.warn("Data array is empty for title: '{}'", title);
                    continue;
                }

                for (MireaScheduleData scheduleData : apiResponse.getData()) {
                    // Создаем ResponseDto из данных
                    ResponseDto responseDto = new ResponseDto(
                            scheduleData.getId(),
                            scheduleData.getTargetTitle(),
                            scheduleData.getFullTitle()
                    );
                    result.add(responseDto);
                }
            } catch (Exception e) {
            }
        }
        return result;
    }
}