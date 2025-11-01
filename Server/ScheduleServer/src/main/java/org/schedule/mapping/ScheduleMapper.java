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

    public List<LessonEntity> parseStringData(List<ScheduleDto> schedule) {
        log.info("Начало парсинга {} ScheduleDto объектов", schedule.size());

        List<LessonEntity> lessons = new ArrayList<>();

        for (ScheduleDto dto : schedule) {
            String data = dto.getiCalContent();

            if (data == null) {
                log.warn("Пустые iCal данные (null) для ScheduleDto: {}", dto.getId());
                continue;
            }

            if (data.trim().isEmpty()) {
                log.warn("Пустые iCal данные (empty string) для ScheduleDto: {}", dto.getId());
                continue;
            }

            log.info("Парсинг ScheduleDto {}: длина iCal данных = {} символов",
                    dto.getId(), data.length());

            List<LessonEntity> parsedLessons = parser.parseICalendarToLessons(data,dto.getTitle());

            if (parsedLessons != null && !parsedLessons.isEmpty()) {
                lessons.addAll(parsedLessons);
                log.info("Добавлено {} занятий из ScheduleDto {}", parsedLessons.size(), dto.getId());
            } else {
                log.warn("Не удалось распарсить занятия для ScheduleDto {} (0 занятий)", dto.getId());

                // ДИАГНОСТИКА: проверяем содержимое iCalContent
                if (data.contains("BEGIN:VCALENDAR") && data.contains("BEGIN:VEVENT")) {
                    log.warn("iCal данные содержат VEVENT, но парсер не нашел занятий");
                } else {
                    log.warn("iCal данные не содержат ожидаемую структуру VEVENT/VCALENDAR");
                }
            }
        }

        log.info("Парсинг завершен. Всего получено {} занятий из {} ScheduleDto",
                lessons.size(), schedule.size());
        return lessons;
    }
}