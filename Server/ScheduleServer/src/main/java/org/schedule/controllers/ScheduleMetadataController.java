package org.schedule.controllers;

import org.schedule.entity.ScheduleMetadataResponseDto;
import org.schedule.reservations.ScheduleMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule/metadata")
public class ScheduleMetadataController {
    private static final Logger log = LoggerFactory.getLogger(ScheduleMetadataController.class);

    private final ScheduleMetadataService metadataService;

    public ScheduleMetadataController(ScheduleMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    /**
     * Проверка метаданных для одной сущности
     *
     * GET /schedule/metadata?entity=ИВБО-01-22
     */
    @GetMapping
    public ResponseEntity<ScheduleMetadataResponseDto> checkMetadata(@RequestParam("entity") String entity) {
        log.info("GET /schedule/metadata?entity={}", entity);

        if (entity == null || entity.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ScheduleMetadataResponseDto response = metadataService.checkMetadata(entity);
        return ResponseEntity.ok(response);
    }

    /**
     * Проверка метаданных для сущности через path variable
     *
     * GET /schedule/metadata/ИВБО-01-22
     */
    @GetMapping("/{entity}")
    public ResponseEntity<ScheduleMetadataResponseDto> checkMetadataByPath(@PathVariable("entity") String entity) {
        log.info("GET /schedule/metadata/{}", entity);

        if (entity == null || entity.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ScheduleMetadataResponseDto response = metadataService.checkMetadata(entity);
        return ResponseEntity.ok(response);
    }

    /**
     * Пакетная проверка метаданных
     *
     * POST /schedule/metadata/batch
     * Body: ["ИВБО-01-22", "ИКБО-60-23", "Иванов И.И."]
     */
    @PostMapping("/batch")
    public ResponseEntity<List<ScheduleMetadataResponseDto>> checkMetadataBatch(@RequestBody List<String> entities) {
        log.info("POST /schedule/metadata/batch with {} entities", entities.size());

        if (entities == null || entities.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<ScheduleMetadataResponseDto> responses = metadataService.checkMetadataBatch(entities);
        return ResponseEntity.ok(responses);
    }

    /**
     * Проверка актуальности — упрощенный ответ (только флаг needsUpdate)
     *
     * HEAD /schedule/metadata?entity=ИВБО-01-22
     */
    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkMetadataHead(@RequestParam("entity") String entity) {
        log.info("HEAD /schedule/metadata?entity={}", entity);

        if (entity == null || entity.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ScheduleMetadataResponseDto response = metadataService.checkMetadata(entity);

        if (response.isNeedsUpdate()) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}