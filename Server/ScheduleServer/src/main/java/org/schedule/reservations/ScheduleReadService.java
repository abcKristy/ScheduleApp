package org.schedule.reservations;

import org.schedule.entity.forBD.EntityType;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.mapping.CheckDataInMemory;
import org.schedule.mapping.DataGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleReadService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleReadService.class);

    private final DataGetter dataGetter;
    private final CheckDataInMemory checkHelper;

    public ScheduleReadService(DataGetter dataGetter, CheckDataInMemory checkHelper) {
        this.dataGetter = dataGetter;
        this.checkHelper = checkHelper;
    }

    @Transactional(readOnly = true)
    public List<LessonEntity> getLessonsFromCacheOrDatabase(List<String> entityList) {
        log.info("Получение занятий из кэша/БД для {} сущностей", entityList.size());

        if (entityList.isEmpty()) {
            throw new IllegalArgumentException("Список сущностей не может быть пустым");
        }

        List<LessonEntity> finalSchedule = new ArrayList<>();
        List<String> remainingEntities = new ArrayList<>(entityList);

        for (String entityString : entityList) {
            try {
                CheckDataInMemory.EntityCheckResult checkResult = checkHelper.checkEntity(entityString);

                if (!checkResult.isValid()) {
                    log.warn("Ошибка валидации сущности: '{}', пропускаем", entityString);
                    remainingEntities.remove(entityString);
                    continue;
                }

                EntityType entityType = checkResult.getEntityType();
                String entityName = checkResult.getEntityName();

                List<LessonEntity> lessons = getLessonsForEntity(entityType, entityName, checkResult);

                if (!lessons.isEmpty()) {
                    finalSchedule.addAll(lessons);
                    remainingEntities.remove(entityString);
                }

            } catch (Exception e) {
                log.error("Ошибка при получении данных для сущности: {}", entityString, e);
                // Продолжаем обработку других сущностей
            }
        }

        log.info("Получено {} занятий из кэша/БД, осталось сущностей для внешнего источника: {}",
                finalSchedule.size(), remainingEntities.size());

        return finalSchedule;
    }

    private List<LessonEntity> getLessonsForEntity(EntityType entityType, String entityName,
                                                   CheckDataInMemory.EntityCheckResult checkResult) {
        List<LessonEntity> lessons = new ArrayList<>();

        // Пробуем получить из кэша
        if (checkHelper.checkCache(entityType, entityName)) {
            lessons = dataGetter.getFromCache(entityType, entityName);
            if (!lessons.isEmpty()) {
                log.debug("Данные из кэша для {}: {}", entityType, entityName);
                return lessons;
            }
        }

        // Пробуем получить из БД
        if (checkResult.isExistsInDatabase()) {
            lessons = dataGetter.getFromDatabase(entityType, entityName);
            if (!lessons.isEmpty()) {
                log.debug("Данные из БД для {}: {}", entityType, entityName);
                return lessons;
            }
        }

        return lessons;
    }

    @Transactional(readOnly = true)
    public List<LessonEntity> getLessonsFromDatabase(List<String> remainingEntities) {
        log.info("Получение занятий из БД для {} сущностей", remainingEntities.size());

        List<LessonEntity> lessonsFromDb = new ArrayList<>();

        for (String entityString : remainingEntities) {
            try {
                CheckDataInMemory.EntityCheckResult checkResult = checkHelper.checkEntity(entityString);
                if (checkResult.isValid()) {
                    List<LessonEntity> lessons = dataGetter.getFromDatabase(
                            checkResult.getEntityType(),
                            checkResult.getEntityName()
                    );
                    lessonsFromDb.addAll(lessons);
                }
            } catch (Exception e) {
                log.error("Ошибка при получении данных из БД для сущности: {}", entityString, e);
                // Продолжаем обработку других сущностей
            }
        }

        log.info("Получено {} занятий из БД", lessonsFromDb.size());
        return lessonsFromDb;
    }
}