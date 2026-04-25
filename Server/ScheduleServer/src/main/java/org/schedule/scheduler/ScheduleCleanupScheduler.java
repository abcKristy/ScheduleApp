package org.schedule.scheduler;

import org.schedule.entity.forBD.ScheduleMetadataEntity;
import org.schedule.repository.LessonRepository;
import org.schedule.repository.ScheduleMetadataRepository;
import org.schedule.util.SemesterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class ScheduleCleanupScheduler {
    private static final Logger log = LoggerFactory.getLogger(ScheduleCleanupScheduler.class);

    private final LessonRepository lessonRepository;
    private final ScheduleMetadataRepository metadataRepository;

    public ScheduleCleanupScheduler(LessonRepository lessonRepository,
                                    ScheduleMetadataRepository metadataRepository) {
        this.lessonRepository = lessonRepository;
        this.metadataRepository = metadataRepository;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupOutdatedData() {
        log.info("========== ЗАПУСК ПЛАНОВОЙ ОЧИСТКИ ДАННЫХ ==========");
        long startTime = System.currentTimeMillis();

        try {
            String currentSemester = SemesterUtils.getCurrentSemester();
            log.info("Текущий семестр: {}", currentSemester);

            cleanupLessonsWithDifferentSemester(currentSemester);
            cleanupOldLessons(currentSemester);
            cleanupOutdatedMetadata(currentSemester);
            recalculateAllMetadataCounts();
            printStatistics();

            long duration = System.currentTimeMillis() - startTime;
            log.info("========== ОЧИСТКА ЗАВЕРШЕНА ЗА {} мс ==========", duration);

        } catch (Exception e) {
            log.error("Критическая ошибка при плановой очистке данных", e);
        }
    }

    private void cleanupLessonsWithDifferentSemester(String currentSemester) {
        try {
            int deletedCount = lessonRepository.deleteBySemesterNot(currentSemester);
            log.info("Удалено {} занятий с устаревшим семестром (не {})", deletedCount, currentSemester);
        } catch (Exception e) {
            log.error("Ошибка при удалении занятий с устаревшим семестром", e);
        }
    }

    private void cleanupOldLessons(String currentSemester) {
        try {
            LocalDate semesterEndDate = SemesterUtils.getSemesterEndDate(currentSemester);
            LocalDate cutoffDate = semesterEndDate.plusDays(30);
            LocalDateTime cutoffDateTime = cutoffDate.atStartOfDay();

            log.info("Удаление занятий, завершившихся до: {}", cutoffDateTime);

            int deletedCount = lessonRepository.deleteByEndTimeBefore(cutoffDateTime);
            log.info("Удалено {} занятий старше 30 дней после окончания семестра", deletedCount);
        } catch (Exception e) {
            log.error("Ошибка при удалении старых занятий", e);
        }
    }

    private void cleanupOutdatedMetadata(String currentSemester) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);

            int deletedCount = metadataRepository.deleteOutdatedMetadata(cutoffDate, currentSemester);
            log.info("Удалено {} записей устаревших метаданных", deletedCount);
        } catch (Exception e) {
            log.error("Ошибка при удалении устаревших метаданных", e);
        }
    }

    private void recalculateAllMetadataCounts() {
        try {
            List<ScheduleMetadataEntity> allMetadata = metadataRepository.findAll();
            int updatedCount = 0;

            for (ScheduleMetadataEntity metadata : allMetadata) {
                try {
                    metadataRepository.recalculateLessonCount(metadata.getId());
                    updatedCount++;
                } catch (Exception e) {
                    log.warn("Не удалось пересчитать количество занятий для metadata id={}", metadata.getId());
                }
            }

            log.info("Обновлены счетчики для {} записей метаданных", updatedCount);
        } catch (Exception e) {
            log.error("Ошибка при пересчете счетчиков метаданных", e);
        }
    }

    private void printStatistics() {
        try {
            long totalLessons = lessonRepository.count();
            long totalMetadata = metadataRepository.count();

            List<String> semesters = lessonRepository.findDistinctSemesters();
            List<Object[]> countsBySemester = lessonRepository.countLessonsBySemester();

            log.info("=== СТАТИСТИКА БАЗЫ ДАННЫХ ===");
            log.info("Всего занятий: {}", totalLessons);
            log.info("Всего записей метаданных: {}", totalMetadata);
            log.info("Семестры в БД: {}", semesters);

            for (Object[] row : countsBySemester) {
                log.info("  Семестр {}: {} занятий", row[0], row[1]);
            }

        } catch (Exception e) {
            log.warn("Не удалось собрать статистику: {}", e.getMessage());
        }
    }

    @Transactional
    public void manualCleanup() {
        log.info("Запуск ручной очистки данных");
        cleanupOutdatedData();
    }
}