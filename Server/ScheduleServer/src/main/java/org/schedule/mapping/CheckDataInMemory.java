package org.schedule.mapping;

import org.schedule.entity.forBD.basic.LessonEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckDataInMemory {
    private static final Logger log = LoggerFactory.getLogger(ScheduleMapper.class);

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

    public LessonEntity checkDatabase(String group) {
        //TODO проверка данных по расписанию в базе данных последних запросов Реализовать запрос к БД
        // return lessonRepository.findByGroupNamesAndDateRange(groupNames, startDate, endDate);
        log.debug("Проверка базы данных для групп: {}", group);
        // Заглушка - возвращаем пустой список
        // В реальной реализации здесь будет запрос к репозиторию
        return null;
    }
}
