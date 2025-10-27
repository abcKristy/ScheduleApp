package org.schedule.mapping;

import org.schedule.entity.*;
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
        log.info("Mapping {} titles", titleList.size());

        List<ResponseDto> result = new ArrayList<>();

        for (String title : titleList) {
            try {
                String apiUrl = mireaApiUrl + title;

                ResponseEntity<MireaApi> response = restTemplate.getForEntity(
                        apiUrl,
                        MireaApi.class
                );

                MireaApi apiResponse = response.getBody();

                if (apiResponse == null || apiResponse.getData() == null || apiResponse.getData().isEmpty()) {
                    log.warn("No data for title: '{}'", title);
                    continue;
                }

                for (MireaApiData scheduleData : apiResponse.getData()) {
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

    public List<ScheduleDto> mapToScheduleDto(List<ResponseDto> responseDtos) {
        log.info("Converting {} ResponseDto objects to ScheduleDto", responseDtos.size());

        List<ScheduleDto> result = new ArrayList<>();

        for (ResponseDto responseDto : responseDtos) {
            try {
                // Формируем URL: s=target_id
                String apiUrl = String.format("https://schedule-of.mirea.ru/_next/data/aiSpo0O7vLwD8bZTeuvDJ/index.json?s=%d_%d",
                        responseDto.getTarget(), responseDto.getId());

                log.info("Fetching schedule data from: {}", apiUrl);

                // Делаем запрос к API расписания
                ResponseEntity<MireaSchedule> response = restTemplate.getForEntity(
                        apiUrl,
                        MireaSchedule.class
                );

                MireaSchedule mireaSchedule = response.getBody();

                if (mireaSchedule == null ||
                        mireaSchedule.getPageProps() == null ||
                        mireaSchedule.getPageProps().getScheduleLoadInfo() == null ||
                        mireaSchedule.getPageProps().getScheduleLoadInfo().isEmpty()) {

                    log.warn("No schedule data found for id: {}, target: {}",
                            responseDto.getId(), responseDto.getTarget());
                    continue;
                }

                // Берем первый элемент из scheduleLoadInfo
                MireaScheduleData scheduleData = mireaSchedule.getPageProps().getScheduleLoadInfo().get(0);

                // Создаем ScheduleDto
                ScheduleDto scheduleDto = new ScheduleDto(
                        scheduleData.getId(),
                        scheduleData.getScheduleTarget(),
                        scheduleData.getTitle(),
                        scheduleData.getICalContent()
                );

                log.info("Created ScheduleDto: id={}, scheduleTarget={}, title='{}'",
                        scheduleDto.getId(), scheduleDto.getScheduleTarget(), scheduleDto.getTitle());

                result.add(scheduleDto);

            } catch (Exception e) {
                log.error("Error converting ResponseDto to ScheduleDto for id: {}, target: {}",
                        responseDto.getId(), responseDto.getTarget(), e);
            }
        }

        log.info("Successfully converted {} ResponseDto objects to {} ScheduleDto objects",
                responseDtos.size(), result.size());
        return result;
    }
}