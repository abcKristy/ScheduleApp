package org.schedule.reservations;

import org.schedule.entity.ResponseDto;
import org.schedule.entity.ScheduleDto;
import org.schedule.mapping.ScheduleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);
    private final ScheduleMapper scheduleMapper;
    private static final String MIREA_API_URL = "https://schedule-of.mirea.ru/schedule/api/search?match=";

    public ScheduleService(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    public List<ScheduleDto> getSchedule(List<String> titleList) {
        log.info("called getSchedule with titles: {}", titleList);
        List<ResponseDto> response = scheduleMapper.mapToResponseDto(titleList, MIREA_API_URL);
        log.info("finish getSchedule with titles: {}", response);

        return scheduleMapper.mapToScheduleDto(response);
    }
}
