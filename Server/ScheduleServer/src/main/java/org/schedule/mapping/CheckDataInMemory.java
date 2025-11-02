package org.schedule.mapping;

import org.schedule.entity.forBD.EntityType;
import org.schedule.entity.forBD.basic.GroupEntity;
import org.schedule.entity.forBD.basic.RoomEntity;
import org.schedule.entity.forBD.basic.TeacherEntity;
import org.schedule.repository.GroupRepository;
import org.schedule.repository.RoomRepository;
import org.schedule.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class CheckDataInMemory {
    private static final Logger log = LoggerFactory.getLogger(CheckDataInMemory.class);
    private final RoomRepository roomRepository;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;

    public CheckDataInMemory(RoomRepository roomRepository, GroupRepository groupRepository, TeacherRepository teacherRepository) {
        this.roomRepository = roomRepository;
        this.groupRepository = groupRepository;
        this.teacherRepository = teacherRepository;
    }
    public boolean checkCache(EntityType entityType, String entityName) {
        log.debug("Проверка кэша для {}: {}", entityType, entityName);
        // TODO: Реализовать проверку Redis/Memcached
        return false; // Всегда false для заглушки
    }
    public EntityCheckResult checkEntity(String entityString) {
        log.debug("Определение типа сущности для: {}", entityString);

        if (entityString == null || entityString.trim().isEmpty()) {
            log.warn("Передана пустая строка для определения сущности");
            return new EntityCheckResult(null, null, false);
        }

        String cleanName = entityString.trim();
        EntityType entityType = determineEntityType(cleanName);

        if (entityType == null) {
            log.warn("Не удалось определить тип сущности для: '{}'", cleanName);
            return new EntityCheckResult(null, null, false);
        }

        boolean existsInDb = checkDatabaseExistence(entityType, cleanName);

        log.debug("Определен тип сущности: {} -> {} (в БД: {})", cleanName, entityType, existsInDb);
        return new EntityCheckResult(entityType, cleanName, existsInDb);
    }
    private EntityType determineEntityType(String entityName) {
        if (entityName == null) return null;

        String cleanName = entityName.trim();

        // Паттерн для групп: буквы-цифры-дефисы
        if (cleanName.matches("[А-ЯA-Z]{2,10}-[\\d-]+")) {
            return EntityType.GROUP;
        }

        // Паттерн для аудиторий: содержит буквы и цифры
        if (cleanName.matches(".*[А-ЯA-Z].*\\d+.*") ||
                cleanName.matches(".*\\(.*\\).*") ||
                cleanName.matches("[А-ЯA-Z]-\\d+.*")) {
            return EntityType.ROOM;
        }

        // Паттерн для преподавателей: ФИО (слова начинаются с заглавных)
        String[] words = cleanName.split("\\s+");
        if (words.length >= 2) {
            boolean allWordsStartWithUpperCase = Arrays.stream(words)
                    .allMatch(word -> !word.isEmpty() && Character.isUpperCase(word.charAt(0)));
            if (allWordsStartWithUpperCase) {
                return EntityType.TEACHER;
            }
        }

        return null;
    }
    private boolean checkDatabaseExistence(EntityType entityType, String entityName) {
        try {
            switch (entityType) {
                case GROUP:
                    Optional<GroupEntity> groupOpt = groupRepository.findByGroupName(entityName);
                    return groupOpt.isPresent() && groupOpt.get().getIdFromApi() != null;
                case TEACHER:
                    Optional<TeacherEntity> teacherOpt = teacherRepository.findByFullName(entityName);
                    return teacherOpt.isPresent() && teacherOpt.get().getIdFromApi() != null;
                case ROOM:
                    Optional<RoomEntity> roomOpt = roomRepository.findByRoomName(entityName);
                    return roomOpt.isPresent() && roomOpt.get().getIdFromApi() != null;
                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("Ошибка при проверке существования сущности {} '{}' в БД: {}",
                    entityType, entityName, e.getMessage());
            return false;
        }
    }

    public static class EntityCheckResult {
        private final EntityType entityType;
        private final String entityName;
        private final boolean existsInDatabase;

        public EntityCheckResult(EntityType entityType, String entityName, boolean existsInDatabase) {
            this.entityType = entityType;
            this.entityName = entityName;
            this.existsInDatabase = existsInDatabase;
        }

        public EntityType getEntityType() { return entityType; }
        public String getEntityName() { return entityName; }
        public boolean isExistsInDatabase() { return existsInDatabase; }
        public boolean isValid() { return entityType != null && entityName != null; }
    }
}