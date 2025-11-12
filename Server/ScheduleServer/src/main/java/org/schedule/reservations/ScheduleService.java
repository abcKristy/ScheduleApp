package org.schedule.reservations;

import org.schedule.entity.ScheduleResponseDto;
import org.schedule.entity.apidata.ResponseDto;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.schedule.ScheduleDto;
import org.schedule.mapping.ScheduleMapper;
import org.schedule.entity.forBD.basic.RoomEntity;
import org.schedule.entity.forBD.basic.TeacherEntity;
import org.schedule.entity.forBD.basic.GroupEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            // Шаг 1: Получаем данные из кэша/БД (только чтение)
            List<LessonEntity> lessonsFromDb = readService.getLessonsFromCacheOrDatabase(entityList);

            // Если все данные найдены в кэше/БД - возвращаем результат как DTO
            if (lessonsFromDb.size() >= entityList.size()) {
                log.info("Все данные получены из кэша/БД, результат: {} занятий", lessonsFromDb.size());
                return mapper.toResponseDtoList(lessonsFromDb);
            }

            // Шаг 2: Получаем недостающие данные из внешнего API
            List<String> remainingEntities = getRemainingEntities(entityList, lessonsFromDb);
            log.info("Получение данных из внешнего источника для {} сущностей", remainingEntities.size());

            List<ResponseDto> response = scheduleMapper.mapToResponseDto(remainingEntities, MIREA_API_URL);
            List<ScheduleDto> schedule = scheduleMapper.mapToScheduleDto(response);
            List<LessonEntity> parsedLessons = scheduleMapper.parseStringData(schedule);

            // Шаг 3: Сохраняем новые данные в БД (отдельная транзакция записи)
            writeService.saveLessonsAndUpdateIds(parsedLessons, response);

            // Шаг 4: Получаем сохраненные данные из БД (чтение)
            List<LessonEntity> newLessonsFromDb = readService.getLessonsFromDatabase(remainingEntities);

            // Объединяем все занятия и преобразуем в DTO
            List<LessonEntity> allLessons = new ArrayList<>(lessonsFromDb);
            allLessons.addAll(newLessonsFromDb);

            List<ScheduleResponseDto> result = mapper.toResponseDtoList(allLessons);

            log.info("Выход из getScheduleForGroups, результат: {} занятий", result.size());
            return result;

        } catch (Exception e) {
            log.error("Критическая ошибка в getScheduleForGroups", e);
            throw new RuntimeException("Не удалось получить расписание", e);
        }
    }

    // В ScheduleService добавим:
    private List<String> getRemainingEntities(List<String> requestedEntities, List<LessonEntity> foundLessons) {
        if (foundLessons.isEmpty()) {
            return new ArrayList<>(requestedEntities);
        }

        // Собираем все сущности, которые покрыты найденными занятиями
        Set<String> coveredEntities = new HashSet<>();

        for (LessonEntity lesson : foundLessons) {
            // Группы
            if (lesson.getGroups() != null) {
                lesson.getGroups().stream()
                        .map(GroupEntity::getGroupName)
                        .forEach(coveredEntities::add);
            }
            // Преподаватели
            if (lesson.getTeachers() != null) {
                lesson.getTeachers().stream()
                        .map(TeacherEntity::getFullName)
                        .forEach(coveredEntities::add);
            }
            // Аудитории
            if (lesson.getRooms() != null) {
                lesson.getRooms().stream()
                        .map(RoomEntity::getRoomName)
                        .forEach(coveredEntities::add);
            }
        }

        // Возвращаем только те сущности, которые не покрыты
        List<String> remaining = requestedEntities.stream()
                .filter(entity -> !coveredEntities.contains(entity))
                .collect(Collectors.toList());

        log.debug("Непокрытые сущности: {}/{}", remaining.size(), requestedEntities.size());
        return remaining;
    }
}