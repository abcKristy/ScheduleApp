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
        log.info("called getScheduleFromApi with titles: {}", titleList);
        List<ResponseDto> response = scheduleMapper.mapToResponseDto(titleList, MIREA_API_URL);
        log.info("finish getScheduleFromApi with titles: {}", response);

        return scheduleMapper.mapToScheduleDto(response);
    }

    @Transactional
    public List<LessonEntity> getScheduleForGroups(List<String> entityList) {
        log.info("Получение расписания для сущностей: {}", entityList);

        List<LessonEntity> finalSchedule = new ArrayList<>();
        List<String> remainingEntities = new ArrayList<>(entityList);

        for (String entityString : entityList) {
            // 1. Проверяем тип сущности
            CheckDataInMemory.EntityCheckResult checkResult = checkHelper.checkEntity(entityString);

            if (!checkResult.isValid()) {
                log.warn("Невалидная сущность: '{}', пропускаем", entityString);
                remainingEntities.remove(entityString);
                continue;
            }

            EntityType entityType = checkResult.getEntityType();
            String entityName = checkResult.getEntityName();

            List<LessonEntity> lessons = new ArrayList<>();

            // 2. Проверка кэша
            if (checkHelper.checkCache(entityType, entityName)) {
                lessons = dataGetter.getFromCache(entityType, entityName);
                if (!lessons.isEmpty()) {
                    log.info("Данные найдены в кэше для {}: {}", entityType, entityName);
                    finalSchedule.addAll(lessons);
                    remainingEntities.remove(entityString);
                    continue;
                }
            }

            // 3. Проверка базы данных
            if (checkResult.isExistsInDatabase()) {
                lessons = dataGetter.getFromDatabase(entityType, entityName);
                if (!lessons.isEmpty()) {
                    log.info("Данные найдены в БД для {}: {}", entityType, entityName);
                    // Сохраняем в кэш
                    saver.saveToCache(lessons);
                    finalSchedule.addAll(lessons);
                    remainingEntities.remove(entityString);
                    continue;
                }
            }
        }

        if (remainingEntities.isEmpty()) {
            log.info("Все данные найдены в кэше/БД. Возвращаем {} занятий", finalSchedule.size());
            return finalSchedule;
        }

        log.info("Данные не найдены в кэше и БД, получаем из внешнего источника для: {}", remainingEntities);

        // 4. Получение из внешнего источника
        List<ResponseDto> response = scheduleMapper.mapToResponseDto(remainingEntities, MIREA_API_URL);
        List<ScheduleDto> schedule = scheduleMapper.mapToScheduleDto(response);
        List<LessonEntity> parsedLessons = scheduleMapper.parseStringData(schedule);

        // 5. Сохраняем в БД
        saver.saveLessons(parsedLessons);

        // 6. Обновляем id_from_api
        saver.updateAllIdsFromApi(response);

        // 7. ПОЛУЧАЕМ ДАННЫЕ ИЗ БД после сохранения
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

        // 8. Сохраняем в кэш
        saver.saveToCache(lessonsFromDb);

        // 9. Добавляем в финальный результат
        finalSchedule.addAll(lessonsFromDb);

        log.info("Успешно получено и сохранено расписание для {} сущностей, найдено {} занятий",
                remainingEntities.size(), lessonsFromDb.size());

        return finalSchedule;
    }
}
