package org.schedule.reservations;

import org.schedule.entity.apidata.ResponseDto;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.mapping.SaverToMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScheduleWriteService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleWriteService.class);

    private final SaverToMemory saver;

    public ScheduleWriteService(SaverToMemory saver) {
        this.saver = saver;
    }

    @Transactional
    public void saveLessonsAndUpdateIds(List<LessonEntity> lessons, List<ResponseDto> responseDtos) {
        log.info("Сохранение занятий и обновление ID из API, занятий: {}, объектов: {}",
                lessons.size(), responseDtos.size());

        try {
            saver.saveLessonsWithErrorHandling(lessons);
            saver.updateAllIdsFromApi(responseDtos);
            log.info("Успешно сохранено занятий и обновлены ID");
        } catch (Exception e) {
            log.error("Ошибка при сохранении занятий и обновлении ID", e);
            throw new RuntimeException("Не удалось сохранить данные", e);
        }
    }

    @Transactional
    public void saveToCache(List<LessonEntity> lessons) {
        log.debug("Сохранение в кэш, занятий: {}", lessons.size());
        try {
            saver.saveToCache(lessons);
        } catch (Exception e) {
            log.warn("Ошибка при сохранении в кэш", e);
            // Не бросаем исключение, т.к. кэш не критичен
        }
    }
}