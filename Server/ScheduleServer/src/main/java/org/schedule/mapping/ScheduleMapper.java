package org.schedule.mapping;

import org.schedule.entity.ScheduleResponseDto;
import org.schedule.entity.apidata.MireaApi;
import org.schedule.entity.apidata.MireaApiData;
import org.schedule.entity.apidata.ResponseDto;
import org.schedule.entity.forBD.basic.GroupEntity;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.forBD.basic.RoomEntity;
import org.schedule.entity.forBD.basic.TeacherEntity;
import org.schedule.entity.schedule.ScheduleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        if (titleList.isEmpty()) {
            throw new IllegalArgumentException("Список заголовков не может быть пустым");
        }

        List<ResponseDto> result = new ArrayList<>();

        for (String title : titleList) {
            try {
                String apiUrl = mireaApiUrl + title;
                ResponseEntity<MireaApi> response = restTemplate.getForEntity(apiUrl, MireaApi.class);
                MireaApi apiResponse = response.getBody();

                if (apiResponse.getData() == null || apiResponse.getData().isEmpty()) {
                    log.warn("Нет данных для title: '{}'", title);
                    continue;
                }

                for (MireaApiData scheduleData : apiResponse.getData()) {
                    ResponseDto responseDto = new ResponseDto(
                            scheduleData.getId(),
                            scheduleData.getFullTitle(),
                            scheduleData.getScheduleTarget(),
                            scheduleData.getiCalLink()
                    );
                    result.add(responseDto);
                }

            } catch (RestClientException e) {
                log.error("Ошибка при обращении к API для title: '{}'", title, e);
                throw new RestClientException("Ошибка получения данных из API расписания", e);
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
                String iCalUrl = responseDto.getiCalLink();

                if (iCalUrl == null || iCalUrl.trim().isEmpty()) {
                    log.warn("Нет iCal ссылки для id: {}, target: {}",
                            responseDto.getId(), responseDto.getTarget());
                    continue;
                }

                log.debug("Запрос iCal по URL: {}", iCalUrl);

                ResponseEntity<String> response = restTemplate.getForEntity(iCalUrl, String.class);
                String iCalContent = response.getBody();

                if (iCalContent == null || iCalContent.trim().isEmpty()) {
                    log.warn("Пустой iCal контент для id: {}, target: {}",
                            responseDto.getId(), responseDto.getTarget());
                    continue;
                }

                ScheduleDto scheduleDto = new ScheduleDto(
                        responseDto.getId(),
                        responseDto.getTarget(),
                        responseDto.getFullTitle(),
                        iCalContent
                );

                result.add(scheduleDto);
                log.debug("Успешно получен iCal для: {}", responseDto.getFullTitle());

            } catch (RestClientException e) {
                log.error("Ошибка HTTP при получении iCal для id: {}, target: {}, url: {}",
                        responseDto.getId(), responseDto.getTarget(), responseDto.getiCalLink(), e);
            } catch (Exception e) {
                log.error("Ошибка обработки iCal для id: {}, target: {}",
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

    public ScheduleResponseDto toResponseDto(LessonEntity lesson) {
        if (lesson == null) return null;

        try {
            List<String> groupNames = lesson.getGroups().stream()
                    .map(GroupEntity::getGroupName)
                    .collect(Collectors.toList());

            // Разбиваем строки на списки
            List<String> teacherList = new ArrayList<>();
            if (lesson.getTeacher() != null && !lesson.getTeacher().trim().isEmpty()) {
                teacherList = Arrays.stream(lesson.getTeacher().split("[,\n]"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }

            List<String> roomList = new ArrayList<>();
            if (lesson.getRoom() != null && !lesson.getRoom().trim().isEmpty()) {
                roomList = Arrays.stream(lesson.getRoom().split("[,\n]"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }

            return new ScheduleResponseDto(
                    lesson.getDiscipline(),
                    lesson.getLessonType(),
                    lesson.getStartTime(),
                    lesson.getEndTime(),
                    roomList,       // ← список аудиторий
                    teacherList,    // ← список преподавателей
                    groupNames,
                    lesson.getGroupsSummary(),
                    lesson.getDescription(),
                    lesson.getRecurrence(),
                    lesson.getExceptions()
            );
        } catch (Exception e) {
            log.error("Ошибка при маппинге", e);
            return createFallbackResponse(lesson);
        }
    }

    private ScheduleResponseDto createFallbackResponse(LessonEntity lesson) {
        List<String> teacherNames = lesson.getTeachers() != null ?
                lesson.getTeachers().stream()
                        .map(TeacherEntity::getFullName)
                        .collect(Collectors.toList()) :
                List.of();

        List<String> roomNames = lesson.getRooms() != null ?
                lesson.getRooms().stream()
                        .map(RoomEntity::getRoomName)
                        .collect(Collectors.toList()) :
                List.of();

        List<String> groupNames = lesson.getGroups() != null ?
                lesson.getGroups().stream()
                        .map(GroupEntity::getGroupName)
                        .collect(Collectors.toList()) :
                List.of();

        return new ScheduleResponseDto(
                lesson.getDiscipline(),
                lesson.getLessonType(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                roomNames,
                teacherNames,
                groupNames,
                lesson.getGroupsSummary(),
                lesson.getDescription(),
                lesson.getRecurrence(),
                lesson.getExceptions()
        );
    }

    public List<ScheduleResponseDto> toResponseDtoList(List<LessonEntity> lessons) {
        if (lessons == null) {
            return List.of();
        }

        return lessons.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}