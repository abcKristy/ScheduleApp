package org.schedule.reservations;

import org.schedule.entity.ScheduleResponseDto;
import org.schedule.entity.apidata.ResponseDto;
import org.schedule.entity.forBD.EntityType;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.schedule.ScheduleDto;
import org.schedule.mapping.ScheduleMapper;
import org.schedule.entity.forBD.basic.RoomEntity;
import org.schedule.entity.forBD.basic.TeacherEntity;
import org.schedule.entity.forBD.basic.GroupEntity;
import org.schedule.util.SemesterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final ScheduleMapper scheduleMapper;
    private final ScheduleReadService readService;
    private final ScheduleWriteService writeService;
    private final ScheduleMapper mapper;

    private static final String MIREA_API_URL = "https://schedule-of.mirea.ru/schedule/api/search?match=";

    public ScheduleService(ScheduleMapper scheduleMapper,
                           ScheduleReadService readService,
                           ScheduleWriteService writeService,
                           ScheduleMapper mapper) {
        this.scheduleMapper = scheduleMapper;
        this.readService = readService;
        this.writeService = writeService;
        this.mapper = mapper;
    }
    public List<ScheduleResponseDto> getScheduleForGroups(List<String> entityList) {
        log.info("Вход в getScheduleForGroups с entityList: {} элементов", entityList.size());

        if (entityList.isEmpty()) {
            throw new IllegalArgumentException("Список сущностей не может быть пустым");
        }

        try {
            List<LessonEntity> lessonsFromDb = readService.getLessonsFromCacheOrDatabase(entityList);

            List<String> remainingEntities = getRemainingEntities(entityList, lessonsFromDb);

            if (!remainingEntities.isEmpty()) {
                log.info("Получение данных из внешнего источника для {} сущностей", remainingEntities.size());

                EntityType entityType = determineEntityType(remainingEntities.get(0));
                String currentSemester = SemesterUtils.getCurrentSemester();

                boolean needsUpdate = readService.needsUpdate(entityType, remainingEntities.get(0));
                if (needsUpdate) {
                    log.info("Обнаружены устаревшие данные для {} {}, выполняем очистку",
                            entityType, remainingEntities.get(0));
                    readService.cleanupOutdatedLessons(entityType, remainingEntities.get(0), "LEGACY");
                }

                List<ResponseDto> response = scheduleMapper.mapToResponseDto(remainingEntities, MIREA_API_URL);
                List<ScheduleDto> schedule = scheduleMapper.mapToScheduleDto(response);
                List<LessonEntity> parsedLessons = scheduleMapper.parseStringData(schedule);
                log.debug("Распаршено {} занятий", parsedLessons.size());

                writeService.saveLessonsAndUpdateIds(parsedLessons, response, entityType, remainingEntities.get(0));
            }

            List<LessonEntity> allLessonsFromDb = readService.getLessonsFromDatabase(entityList);

            List<ScheduleResponseDto> result = mapper.toResponseDtoList(allLessonsFromDb);

            log.info("Выход из getScheduleForGroups, результат: {} занятий", result.size());
            return result;

        } catch (Exception e) {
            log.error("Критическая ошибка в getScheduleForGroups", e);
            throw new RuntimeException("Не удалось получить расписание", e);
        }
    }

    private List<String> getRemainingEntities(List<String> requestedEntities, List<LessonEntity> foundLessons) {
        if (foundLessons.isEmpty()) {
            return new ArrayList<>(requestedEntities);
        }

        String currentSemester = SemesterUtils.getCurrentSemester();
        Set<String> coveredEntities = new HashSet<>();

        for (LessonEntity lesson : foundLessons) {
            if (!currentSemester.equals(lesson.getSemester())) {
                continue;
            }

            if (lesson.getGroups() != null) {
                lesson.getGroups().stream()
                        .map(GroupEntity::getGroupName)
                        .forEach(coveredEntities::add);
            }
            if (lesson.getTeachers() != null) {
                lesson.getTeachers().stream()
                        .map(TeacherEntity::getFullName)
                        .forEach(coveredEntities::add);
            }
            if (lesson.getRooms() != null) {
                lesson.getRooms().stream()
                        .map(RoomEntity::getRoomName)
                        .forEach(coveredEntities::add);
            }
        }

        List<String> remaining = requestedEntities.stream()
                .filter(entity -> !coveredEntities.contains(entity))
                .collect(Collectors.toList());

        log.debug("Непокрытые сущности: {}/{}", remaining.size(), requestedEntities.size());
        return remaining;
    }

    private EntityType determineEntityType(String entityString) {
        if (entityString == null) return EntityType.GROUP;

        String cleanName = entityString.trim();

        if (cleanName.matches("[А-ЯA-Z0-9]{2,4}-\\d{2}-\\d{2}")) {
            return EntityType.GROUP;
        }

        String[] words = cleanName.split("\\s+");
        if (words.length >= 2) {
            boolean allWordsStartWithUpperCase = Arrays.stream(words)
                    .allMatch(word -> !word.isEmpty() && Character.isUpperCase(word.charAt(0)));
            if (allWordsStartWithUpperCase) {
                return EntityType.TEACHER;
            }
        }

        return EntityType.ROOM;
    }
}