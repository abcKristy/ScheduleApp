package org.schedule.mapping;

import org.schedule.entity.apidata.MireaApi;
import org.schedule.entity.apidata.MireaApiData;
import org.schedule.entity.apidata.ResponseDto;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.schedule.MireaSchedule;
import org.schedule.entity.schedule.MireaScheduleData;
import org.schedule.entity.schedule.ScheduleDto;
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
    private final ParserToLesson parser;

    public ScheduleMapper(RestTemplate restTemplate, ParserToLesson parser) {
        this.restTemplate = restTemplate;
        this.parser = parser;
    }

    public List<ResponseDto> mapToResponseDto(List<String> titleList, String mireaApiUrl) {
        log.info("Вход в mapToResponseDto, titles: {} элементов", titleList.size());

        List<ResponseDto> result = new ArrayList<>();

        for (String title : titleList) {
            try {
                String apiUrl = mireaApiUrl + title;
                ResponseEntity<MireaApi> response = restTemplate.getForEntity(apiUrl, MireaApi.class);
                MireaApi apiResponse = response.getBody();

                if (apiResponse == null || apiResponse.getData() == null || apiResponse.getData().isEmpty()) {
                    log.warn("Нет данных для title: '{}'", title);
                    continue;
                }

                for (MireaApiData scheduleData : apiResponse.getData()) {
                    ResponseDto responseDto = new ResponseDto(
                            scheduleData.getId(),
                            scheduleData.getFullTitle(),
                            scheduleData.getScheduleTarget()
                    );
                    result.add(responseDto);
                }

            } catch (Exception e) {
                log.error("Ошибка обработки title: '{}'", title, e);
            }
        }

        log.info("Выход из mapToResponseDto, результат: {} объектов", result.size());
        return result;
    }

    public List<ScheduleDto> mapToScheduleDto(List<ResponseDto> responseDtos) {
        log.info("Вход в mapToScheduleDto, ResponseDto: {} элементов", responseDtos.size());

        List<ScheduleDto> result = new ArrayList<>();

        for (ResponseDto responseDto : responseDtos) {
            try {
                String apiUrl = String.format("https://schedule-of.mirea.ru/_next/data/aiSpo0O7vLwD8bZTeuvDJ/index.json?s=%d_%d",
                        responseDto.getTarget(), responseDto.getId());

                ResponseEntity<MireaSchedule> response = restTemplate.getForEntity(apiUrl, MireaSchedule.class);
                MireaSchedule mireaSchedule = response.getBody();

                if (mireaSchedule == null ||
                        mireaSchedule.getPageProps() == null ||
                        mireaSchedule.getPageProps().getScheduleLoadInfo() == null ||
                        mireaSchedule.getPageProps().getScheduleLoadInfo().isEmpty()) {

                    log.warn("Нет данных расписания для id: {}, target: {}", responseDto.getId(), responseDto.getTarget());
                    continue;
                }

                MireaScheduleData scheduleData = mireaSchedule.getPageProps().getScheduleLoadInfo().get(0);
                ScheduleDto scheduleDto = new ScheduleDto(
                        scheduleData.getId(),
                        scheduleData.getScheduleTarget(),
                        scheduleData.getTitle(),
                        scheduleData.getICalContent()
                );

                result.add(scheduleDto);

            } catch (Exception e) {
                log.error("Ошибка конвертации ResponseDto в ScheduleDto для id: {}, target: {}",
                        responseDto.getId(), responseDto.getTarget(), e);
            }
        }

        log.info("Выход из mapToScheduleDto, результат: {} объектов", result.size());
        return result;
    }

    public List<LessonEntity> parseStringData(List<ScheduleDto> schedule) {
        log.info("Вход в parseStringData, ScheduleDto: {} элементов", schedule.size());

        List<LessonEntity> lessons = new ArrayList<>();

        for (ScheduleDto dto : schedule) {
            String data = dto.getiCalContent();

            if (data == null || data.trim().isEmpty()) {
                log.warn("Пустые iCal данные для ScheduleDto: {}", dto.getId());
                continue;
            }

            List<LessonEntity> parsedLessons = parser.parseICalendarToLessons(data, dto.getTitle());

            if (parsedLessons != null && !parsedLessons.isEmpty()) {
                lessons.addAll(parsedLessons);
            } else {
                log.warn("Парсер не нашел занятий для ScheduleDto: {}", dto.getId());
            }
        }

        log.info("Выход из parseStringData, результат: {} занятий", lessons.size());
        return lessons;
    }
}