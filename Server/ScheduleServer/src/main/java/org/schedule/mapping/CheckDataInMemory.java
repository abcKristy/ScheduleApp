package org.schedule.mapping;

import org.hibernate.type.EntityType;
import org.schedule.entity.forBD.basic.GroupEntity;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.forBD.basic.RoomEntity;
import org.schedule.entity.forBD.basic.TeacherEntity;
import org.schedule.repository.GroupRepository;
import org.schedule.repository.RoomRepository;
import org.schedule.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class CheckDataInMemory {
    private static final Logger log = LoggerFactory.getLogger(ScheduleMapper.class);
    private final RoomRepository roomRepository;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;

    public CheckDataInMemory(RoomRepository roomRepository, GroupRepository groupRepository, TeacherRepository teacherRepository) {
        this.roomRepository = roomRepository;
        this.groupRepository = groupRepository;
        this.teacherRepository = teacherRepository;
    }

    public LessonEntity checkCache(String group){
        //TODO проверка данных по расписанию в кэше последних запросов
        // Реализовать проверку Redis/Memcached
        // String cacheKey = generateCacheKey(groupNames);
        // return (List<Lesson>) redisTemplate.opsForValue().get(cacheKey);
        log.debug("Проверка кэша для групп: {}", group);

        // Заглушка - всегда возвращаем null (не найдено в кэше)
        // В реальной реализации здесь будет логика проверки Redis
        return null;
    }

    public LessonEntity checkDatabase(String entityName) {
        log.debug("Проверка базы данных для: {}", entityName);

        if (entityName == null || entityName.trim().isEmpty()) {
            log.warn("Передано пустое название для проверки в БД");
            return null;
        }

        try {
            // Определяем тип сущности по маске
            EntityType entityType = determineEntityType(entityName);

            switch (entityType) {
                case GROUP:
                    return checkGroupInDatabase(entityName);
                case TEACHER:
                    return checkTeacherInDatabase(entityName);
                case ROOM:
                    return checkRoomInDatabase(entityName);
                case UNKNOWN:
                    log.warn("Не удалось определить тип сущности для: '{}'", entityName);
                    return null;
                default:
                    log.warn("Неизвестный тип сущности для: '{}'", entityName);
                    return null;
            }

        } catch (Exception e) {
            log.error("Ошибка при проверке сущности '{}' в БД: {}", entityName, e.getMessage());
            return null;
        }
    }
    /**
     * Определяет тип сущности по названию
     */
    private EntityType determineEntityType(String entityName) {
        if (entityName == null) return EntityType.UNKNOWN;

        String cleanName = entityName.trim();

        // Паттерн для групп: буквы-цифры-дефисы (ИКБО-60-23, ИВБО-01-21 и т.д.)
        if (cleanName.matches("[А-ЯA-Z]{2,10}-[\\d-]+")) {
            return EntityType.GROUP;
        }

        // Паттерн для аудиторий: содержит буквы и цифры, может быть с дефисами/скобками
        // Примеры: И-204-а, Г-301-в, А-101, (В-78)
        if (cleanName.matches(".*[А-ЯA-Z].*\\d+.*") ||
                cleanName.matches(".*\\(.*\\).*") ||
                cleanName.matches("[А-ЯA-Z]-\\d+.*")) {
            return EntityType.ROOM;
        }

        // Паттерн для преподавателей: ФИО (3 слова, начинаются с заглавных)
        // Примеры: Овчинников Михаил Александрович, Иванов И. И.
        String[] words = cleanName.split("\\s+");
        if (words.length >= 2) {
            boolean allWordsStartWithUpperCase = Arrays.stream(words)
                    .allMatch(word -> !word.isEmpty() && Character.isUpperCase(word.charAt(0)));
            if (allWordsStartWithUpperCase) {
                return EntityType.TEACHER;
            }
        }

        return EntityType.UNKNOWN;
    }

    /**
     * Проверяет группу в БД
     */
    private LessonEntity checkGroupInDatabase(String groupName) {
        Optional<GroupEntity> groupOpt = groupRepository.findByGroupName(groupName);

        if (groupOpt.isPresent()) {
            GroupEntity group = groupOpt.get();
            if (group.getIdFromApi() != null) {
                log.debug("Группа '{}' найдена в БД с id_from_api={}", groupName, group.getIdFromApi());
                // Возвращаем пример занятия для этой группы (можно доработать)
                return getSampleLessonForGroup(group);
            } else {
                log.debug("Группа '{}' найдена в БД, но id_from_api не установлен", groupName);
                return null;
            }
        } else {
            log.debug("Группа '{}' не найдена в БД", groupName);
            return null;
        }
    }

    /**
     * Проверяет преподавателя в БД
     */
    private LessonEntity checkTeacherInDatabase(String teacherName) {
        Optional<TeacherEntity> teacherOpt = teacherRepository.findByFullName(teacherName);

        if (teacherOpt.isPresent()) {
            TeacherEntity teacher = teacherOpt.get();
            if (teacher.getIdFromApi() != null) {
                log.debug("Преподаватель '{}' найден в БД с id_from_api={}", teacherName, teacher.getIdFromApi());
                // Возвращаем пример занятия для этого преподавателя
                return getSampleLessonForTeacher(teacher);
            } else {
                log.debug("Преподаватель '{}' найден в БД, но id_from_api не установлен", teacherName);
                return null;
            }
        } else {
            log.debug("Преподаватель '{}' не найден в БД", teacherName);
            return null;
        }
    }

    /**
     * Проверяет аудиторию в БД
     */
    private LessonEntity checkRoomInDatabase(String roomName) {
        Optional<RoomEntity> roomOpt = roomRepository.findByRoomName(roomName);

        if (roomOpt.isPresent()) {
            RoomEntity room = roomOpt.get();
            if (room.getIdFromApi() != null) {
                log.debug("Аудитория '{}' найдена в БД с id_from_api={}", roomName, room.getIdFromApi());
                // Возвращаем пример занятия для этой аудитории
                return getSampleLessonForRoom(room);
            } else {
                log.debug("Аудитория '{}' найдена в БД, но id_from_api не установлен", roomName);
                return null;
            }
        } else {
            log.debug("Аудитория '{}' не найдена в БД", roomName);
            return null;
        }
    }

    /**
     * Создает пример занятия для группы (заглушка)
     */
    private LessonEntity getSampleLessonForGroup(GroupEntity group) {
        // TODO: Реализовать получение реального занятия из БД
        LessonEntity lesson = new LessonEntity();
        lesson.setDiscipline("Пример занятия для группы " + group.getGroupName());
        lesson.setGroups(List.of(group));
        return lesson;
    }

    /**
     * Создает пример занятия для преподавателя (заглушка)
     */
    private LessonEntity getSampleLessonForTeacher(TeacherEntity teacher) {
        // TODO: Реализовать получение реального занятия из БД
        LessonEntity lesson = new LessonEntity();
        lesson.setDiscipline("Пример занятия преподавателя " + teacher.getFullName());
        lesson.setTeacher(teacher.getFullName());
        lesson.setTeachers(List.of(teacher));
        return lesson;
    }

    /**
     * Создает пример занятия для аудитории (заглушка)
     */
    private LessonEntity getSampleLessonForRoom(RoomEntity room) {
        // TODO: Реализовать получение реального занятия из БД
        LessonEntity lesson = new LessonEntity();
        lesson.setDiscipline("Пример занятия в аудитории " + room.getRoomName());
        lesson.setRoom(room.getRoomName());
        lesson.setRooms(List.of(room));
        return lesson;
    }

    /**
     * Enum для типов сущностей
     */
    private enum EntityType {
        GROUP, TEACHER, ROOM, UNKNOWN
    }
}
