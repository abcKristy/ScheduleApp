package org.schedule.reservations;

import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.schedule.ScheduleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/api/{titles}")
    public ResponseEntity<List<ScheduleDto>> getScheduleFromApi(
            @PathVariable("titles") String titles
    ) {
        log.info("called getScheduleFromApi with titles: {}", titles);

        List<String> titleList = Arrays.stream(titles.split(":"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(scheduleService.getScheduleFromApi(titleList));
    }

    @GetMapping("/final/{titles}")
    public ResponseEntity<List<LessonEntity>> getScheduleForGroups(
            @PathVariable("titles") String titles
    ) {
        log.info("called getScheduleFromApi with titles: {}", titles);

        List<String> titleList = Arrays.stream(titles.split(":"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(scheduleService.getScheduleForGroups(titleList));
    }

}
