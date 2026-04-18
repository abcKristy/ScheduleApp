package org.schedule.scheduler;

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

    /**
     * Ежедневная очистка устаревших данных в 03:00
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupOutdatedData() {
        log.info("Запуск плановой очистки устаревших данных");

        try {
            String currentSemester = SemesterUtils.getCurrentSemester();

            // Удаляем занятия, не относящиеся к текущему семестру
            int deletedLessons = lessonRepository.deleteBySemesterNot(currentSemester);
            log.info("Удалено {} занятий с устаревшим семестром", deletedLessons);

            // Удаляем метаданные с устаревшим семестром
            int deletedMetadata = metadataRepository.deleteBySemesterNot(currentSemester);
            log.info("Удалено {} записей метаданных с устаревшим семестром", deletedMetadata);

            // Удаляем занятия старше 30 дней после окончания семестра
            LocalDate cutoffDate = LocalDate.now().minusDays(30);
            LocalDateTime cutoffDateTime = cutoffDate.atStartOfDay();
            // TODO: добавить метод в репозиторий для удаления по дате

        } catch (Exception e) {
            log.error("Ошибка при плановой очистке данных", e);
        }
    }
}