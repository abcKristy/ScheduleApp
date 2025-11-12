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

    public List<LessonEntity> getFromDatabase(EntityType entityType, String entityName) {
        log.debug("Вход в getFromDatabase, тип: {}, имя: {}", entityType, entityName);

        if (entityType == null || entityName == null || entityName.trim().isEmpty()) {
            throw new IllegalArgumentException("Тип и имя сущности не могут быть пустыми");
        }

        try {
            List<LessonEntity> result;

            switch (entityType) {
                case GROUP:
                    result = lessonRepository.findByGroups_GroupName(entityName);
                    break;
                case TEACHER:
                    result = lessonRepository.findByTeachers_FullName(entityName);
                    break;
                case ROOM:
                    result = lessonRepository.findByRooms_RoomName(entityName);
                    break;
                default:
                    log.warn("Неизвестный тип сущности: {}", entityType);
                    result = new ArrayList<>();
            }

            log.debug("Выход из getFromDatabase, результат: {} элементов", result.size());
            return result;

        } catch (Exception e) {
            log.error("Ошибка в getFromDatabase для {} '{}'", entityType, entityName, e);
            throw new RuntimeException("Ошибка получения данных из БД", e);
        }
    }
}