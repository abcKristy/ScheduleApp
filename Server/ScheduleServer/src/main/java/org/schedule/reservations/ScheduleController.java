package org.schedule.reservations;

import org.schedule.entity.ScheduleResponseDto;
import org.schedule.mapping.ScheduleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/schedule")
@Validated
public class ScheduleController {
    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);

    private final ScheduleService scheduleService;
    private final ScheduleMapper mapper;

    public ScheduleController(ScheduleService scheduleService, ScheduleMapper mapper) {
        this.scheduleService = scheduleService;
        this.mapper = mapper;
    }
    @GetMapping("/final/{titles}")
    public ResponseEntity<List<ScheduleResponseDto>> getScheduleForGroupsLegacy(
            @PathVariable("titles") String titles
    ) {
        log.info("called getScheduleForGroupsLegacy with titles: {}", titles);

        List<String> titleList = Arrays.stream(titles.split(":"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        List<ScheduleResponseDto> result = scheduleService.getScheduleForGroups(titleList);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}