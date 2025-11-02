package org.schedule.mapping;

import org.schedule.entity.forBD.EntityType;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.repository.LessonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataGetter {
    private static final Logger log = LoggerFactory.getLogger(DataGetter.class);
    private final LessonRepository lessonRepository;

    public DataGetter(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    /**
     * Получает занятия из кэша
     */
    public List<LessonEntity> getFromCache(EntityType entityType, String entityName) {
        log.debug("Получение из кэша для {}: {}", entityType, entityName);
        // TODO: Реализовать получение из Redis/Memcached
        return new ArrayList<>(); // Всегда пусто для заглушки
    }

    /**
     * Получает занятия из базы данных
     */
    public List<LessonEntity> getFromDatabase(EntityType entityType, String entityName) {
        log.debug("Получение из БД для {}: {}", entityType, entityName);

        try {
            switch (entityType) {
                case GROUP:
                    return lessonRepository.findByGroups_GroupName(entityName);
                case TEACHER:
                    return lessonRepository.findByTeachers_FullName(entityName);
                case ROOM:
                    return lessonRepository.findByRooms_RoomName(entityName);
                default:
                    log.warn("Неизвестный тип сущности для получения данных: {}", entityType);
                    return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("Ошибка при получении данных из БД для {} '{}': {}",
                    entityType, entityName, e.getMessage());
            return new ArrayList<>();
        }
    }
}
