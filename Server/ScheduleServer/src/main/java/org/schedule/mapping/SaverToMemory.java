package org.schedule.mapping;

import org.schedule.entity.forBD.LessonEntity;
import org.schedule.reservations.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SaverToMemory {
    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    public void saveToCache(LessonEntity schedule) {
        log.debug("Сохранение {} занятий в кэш для групп", schedule);

    }

    public void saveToDatabase(LessonEntity schedule) {
        // TODO: Реализовать сохранение в БД
        // lessonRepository.saveAll(lessons);

        log.debug("Сохранение {} занятий в базу данных", schedule);

    }
}
