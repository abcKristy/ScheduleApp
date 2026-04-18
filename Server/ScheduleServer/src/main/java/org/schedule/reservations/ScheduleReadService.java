package org.schedule.reservations;

import org.schedule.entity.forBD.EntityType;
import org.schedule.entity.forBD.ScheduleMetadataEntity;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.mapping.CheckDataInMemory;
import org.schedule.mapping.DataGetter;
import org.schedule.repository.LessonRepository;
import org.schedule.repository.ScheduleMetadataRepository;
import org.schedule.util.SemesterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleReadService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleReadService.class);

    private final DataGetter dataGetter;
    private final CheckDataInMemory checkHelper;
    private final ScheduleMetadataRepository metadataRepository;
    private final LessonRepository lessonRepository;

    public ScheduleReadService(DataGetter dataGetter,
                               CheckDataInMemory checkHelper,
                               ScheduleMetadataRepository metadataRepository,
                               LessonRepository lessonRepository) {
        this.dataGetter = dataGetter;
        this.checkHelper = checkHelper;
        this.metadataRepository = metadataRepository;
        this.lessonRepository = lessonRepository;
    }

    @Transactional(readOnly = true)
    public List<LessonEntity> getLessonsFromCacheOrDatabase(List<String> entityList) {
        log.info("Получение занятий из кэша/БД для {} сущностей", entityList.size());

        if (entityList.isEmpty()) {
            throw new IllegalArgumentException("Список сущностей не может быть пустым");
        }

        List<LessonEntity> finalSchedule = new ArrayList<>();
        List<String> remainingEntities = new ArrayList<>(entityList);

        String currentSemester = SemesterUtils.getCurrentSemester();
        log.debug("Текущий семестр: {}", currentSemester);

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

                Optional<ScheduleMetadataEntity> metadataOpt =
                        metadataRepository.findByEntityTypeAndEntityNameAndSemester(
                                entityType.name(), entityName, currentSemester);

                if (metadataOpt.isPresent()) {
                    ScheduleMetadataEntity metadata = metadataOpt.get();
                    log.debug("Найдены метаданные: {} {}, семестр: {}, занятий: {}",
                            entityType, entityName, metadata.getSemester(), metadata.getLessonCount());

                    List<LessonEntity> lessons = dataGetter.getFromDatabase(entityType, entityName);
                    if (!lessons.isEmpty()) {
                        finalSchedule.addAll(lessons);
                        remainingEntities.remove(entityString);
                        log.debug("Данные из БД для {} {}: {} занятий", entityType, entityName, lessons.size());
                    }
                } else {
                    log.debug("Метаданные не найдены для {} {} в семестре {}",
                            entityType, entityName, currentSemester);

                    List<LessonEntity> oldLessons = dataGetter.getFromDatabase(entityType, entityName);
                    if (!oldLessons.isEmpty()) {
                        log.info("Найдены устаревшие данные для {} {} ({} занятий), требуется обновление",
                                entityType, entityName, oldLessons.size());
                    }
                }

            } catch (Exception e) {
                log.error("Ошибка при получении данных для сущности: {}", entityString, e);
            }
        }

        log.info("Получено {} занятий из кэша/БД, осталось сущностей для внешнего источника: {}",
                finalSchedule.size(), remainingEntities.size());

        return finalSchedule;
    }

    @Transactional
    public void cleanupOutdatedLessons(EntityType entityType, String entityName, String oldSemester) {
        log.info("Очистка устаревших занятий для {} {} (семестр: {})",
                entityType, entityName, oldSemester);

        try {
            List<LessonEntity> oldLessons = dataGetter.getFromDatabase(entityType, entityName);
            if (!oldLessons.isEmpty()) {
                lessonRepository.deleteAll(oldLessons);
                log.info("Удалено {} устаревших занятий для {} {}",
                        oldLessons.size(), entityType, entityName);
            }

            metadataRepository.deleteByEntityTypeAndEntityNameAndSemester(
                    entityType.name(), entityName, oldSemester);

        } catch (Exception e) {
            log.error("Ошибка при очистке устаревших данных для {} {}", entityType, entityName, e);
        }
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
            }
        }

        log.info("Получено {} занятий из БД", lessonsFromDb.size());
        return lessonsFromDb;
    }

    public boolean needsUpdate(EntityType entityType, String entityName) {
        String currentSemester = SemesterUtils.getCurrentSemester();
        Optional<ScheduleMetadataEntity> metadataOpt =
                metadataRepository.findByEntityTypeAndEntityNameAndSemester(
                        entityType.name(), entityName, currentSemester);

        if (metadataOpt.isEmpty()) {
            log.debug("{} {} требует обновления: метаданные не найдены", entityType, entityName);
            return true;
        }

        ScheduleMetadataEntity metadata = metadataOpt.get();
        boolean isOutdated = SemesterUtils.isOutdated(metadata.getSemester());

        log.debug("{} {}: семестр в БД={}, текущий={}, требуется обновление={}",
                entityType, entityName, metadata.getSemester(), currentSemester, isOutdated);

        return isOutdated;
    }
}