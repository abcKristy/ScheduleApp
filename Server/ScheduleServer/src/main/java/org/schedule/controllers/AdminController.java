package org.schedule.controllers;

import org.schedule.scheduler.ScheduleCleanupScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ScheduleCleanupScheduler cleanupScheduler;

    public AdminController(ScheduleCleanupScheduler cleanupScheduler) {
        this.cleanupScheduler = cleanupScheduler;
    }

    /**
     * Ручной запуск очистки данных
     * POST /admin/cleanup
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> manualCleanup() {
        log.info("Получен запрос на ручную очистку данных");

        long startTime = System.currentTimeMillis();
        cleanupScheduler.manualCleanup();
        long duration = System.currentTimeMillis() - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Очистка данных выполнена успешно");
        response.put("durationMs", duration);

        return ResponseEntity.ok(response);
    }
}