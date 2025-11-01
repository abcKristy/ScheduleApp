package org.schedule.reservations;

import jakarta.transaction.Transactional;
import org.schedule.entity.apidata.ResponseDto;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.schedule.ScheduleDto;
import org.schedule.mapping.CheckDataInMemory;
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

    public ScheduleService(ScheduleMapper scheduleMapper, CheckDataInMemory checkHelper, SaverToMemory saver) {
        this.scheduleMapper = scheduleMapper;
        this.checkHelper = checkHelper;
        this.saver = saver;
    }

    public List<ScheduleDto> getScheduleFromApi(List<String> titleList) {
        log.info("called getScheduleFromApi with titles: {}", titleList);
        List<ResponseDto> response = scheduleMapper.mapToResponseDto(titleList, MIREA_API_URL);
        log.info("finish getScheduleFromApi with titles: {}", response);

        return scheduleMapper.mapToScheduleDto(response);
    }

    @Transactional
    public List<LessonEntity> getScheduleForGroups(List<String> titleList){
        log.info("Получение расписания для групп: {}", titleList);

        List<LessonEntity> finalSchedule = new ArrayList<>();
        List<String> remainingGroups = new ArrayList<>(titleList);

        for (String group : titleList) {
            LessonEntity cachedSchedule = checkHelper.checkCache(group);
            if(cachedSchedule != null){
                log.info("Расписание найдено в кэше для группы: {}", group);
                finalSchedule.add(cachedSchedule);
                remainingGroups.remove(group);
                continue;
            }

            LessonEntity dbSchedule = checkHelper.checkDatabase(group);
            if(dbSchedule != null){
                log.info("Расписание найдено в базе данных для группы: {}", group);
                saver.saveToCache(dbSchedule);
                finalSchedule.add(dbSchedule);
                remainingGroups.remove(group);
            }
        }

        if (remainingGroups.isEmpty()) {
            log.info("Все расписания найдены в кэше/БД. Возвращаем {} занятий", finalSchedule.size());
            return finalSchedule;
        }

        log.info("Расписание не найдено в кэше и БД, получаем данные из внешнего источника для групп {}", remainingGroups);

        List<ResponseDto> response = scheduleMapper.mapToResponseDto(remainingGroups, MIREA_API_URL);
        List<ScheduleDto> schedule = scheduleMapper.mapToScheduleDto(response);
        List<LessonEntity> parsedLessons = scheduleMapper.parseStringData(schedule);

        for (LessonEntity parsed : parsedLessons) {
            saver.saveToDatabase(parsed);
            saver.saveToCache(parsed);
        }
        saver.updateAllIdsFromApi(response);

        finalSchedule.addAll(parsedLessons);

        log.info("Успешно получено и сохранено расписание для {} групп, найдено {} занятий",
                remainingGroups.size(), parsedLessons.size());

        log.info("Успешно получено и сохранено расписание num {}", finalSchedule.size());

        return finalSchedule;
    }
}
