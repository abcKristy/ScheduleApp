package org.schedule.reservations;

import jakarta.transaction.Transactional;
import org.schedule.entity.apidata.ResponseDto;
import org.schedule.entity.forBD.EntityType;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.schedule.ScheduleDto;
import org.schedule.mapping.CheckDataInMemory;
import org.schedule.mapping.DataGetter;
import org.schedule.mapping.SaverToMemory;
import org.schedule.mapping.ScheduleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ScheduleService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);
    private final ScheduleMapper scheduleMapper;
    private final CheckDataInMemory checkHelper;
    public final SaverToMemory saver;
    private static final String MIREA_API_URL = "https://schedule-of.mirea.ru/schedule/api/search?match=";
    private final DataGetter dataGetter;

    public ScheduleService(ScheduleMapper scheduleMapper, CheckDataInMemory checkHelper, SaverToMemory saver, DataGetter dataGetter) {
        this.scheduleMapper = scheduleMapper;
        this.checkHelper = checkHelper;
        this.saver = saver;
        this.dataGetter = dataGetter;
    }

    public List<ScheduleDto> getScheduleFromApi(List<String> titleList) {
        log.info("Вход в getScheduleFromApi с titles: {}", titleList);
        List<ResponseDto> response = scheduleMapper.mapToResponseDto(titleList, MIREA_API_URL);
        List<ScheduleDto> result = scheduleMapper.mapToScheduleDto(response);
        log.info("Выход из getScheduleFromApi, результат: {} элементов", result.size());
        return result;
    }

    @Transactional
    public List<LessonEntity> getScheduleForGroups(List<String> entityList) {
        log.info("Вход в getScheduleForGroups с entityList: {} элементов", entityList.size());

        List<LessonEntity> finalSchedule = new ArrayList<>();
        List<String> remainingEntities = new ArrayList<>(entityList);

        for (String entityString : entityList) {
            CheckDataInMemory.EntityCheckResult checkResult = checkHelper.checkEntity(entityString);

            if (!checkResult.isValid()) {
                log.warn("Ошибка валидации сущности: '{}', пропускаем", entityString);
                remainingEntities.remove(entityString);
                continue;
            }

            EntityType entityType = checkResult.getEntityType();
            String entityName = checkResult.getEntityName();

            List<LessonEntity> lessons = new ArrayList<>();

            if (checkHelper.checkCache(entityType, entityName)) {
                lessons = dataGetter.getFromCache(entityType, entityName);
                if (!lessons.isEmpty()) {
                    log.debug("Данные из кэша для {}: {}", entityType, entityName);
                    finalSchedule.addAll(lessons);
                    remainingEntities.remove(entityString);
                    continue;
                }
            }

            if (checkResult.isExistsInDatabase()) {
                lessons = dataGetter.getFromDatabase(entityType, entityName);
                if (!lessons.isEmpty()) {
                    log.debug("Данные из БД для {}: {}", entityType, entityName);
                    saver.saveToCache(lessons);
                    finalSchedule.addAll(lessons);
                    remainingEntities.remove(entityString);
                    continue;
                }
            }
        }

        if (remainingEntities.isEmpty()) {
            log.info("Выход из getScheduleForGroups - все данные из кэша/БД, результат: {} занятий", finalSchedule.size());
            return finalSchedule;
        }

        log.info("Получение данных из внешнего источника для {} сущностей", remainingEntities.size());

        try {
            List<ResponseDto> response = scheduleMapper.mapToResponseDto(remainingEntities, MIREA_API_URL);
            List<ScheduleDto> schedule = scheduleMapper.mapToScheduleDto(response);
            List<LessonEntity> parsedLessons = scheduleMapper.parseStringData(schedule);

            saver.saveLessons(parsedLessons);
            saver.updateAllIdsFromApi(response);

            List<LessonEntity> lessonsFromDb = new ArrayList<>();
            for (String entityString : remainingEntities) {
                CheckDataInMemory.EntityCheckResult checkResult = checkHelper.checkEntity(entityString);
                if (checkResult.isValid()) {
                    List<LessonEntity> lessons = dataGetter.getFromDatabase(
                            checkResult.getEntityType(),
                            checkResult.getEntityName()
                    );
                    lessonsFromDb.addAll(lessons);
                }
            }

            saver.saveToCache(lessonsFromDb);
            finalSchedule.addAll(lessonsFromDb);

            log.info("Выход из getScheduleForGroups - данные из внешнего источника, результат: {} занятий", finalSchedule.size());
            return finalSchedule;
        } catch (Exception e) {
            log.error("Ошибка в getScheduleForGroups при работе с внешним источником", e);
            throw e;
        }
    }
}