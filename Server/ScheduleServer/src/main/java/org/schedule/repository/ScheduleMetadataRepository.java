package org.schedule.repository;

import org.schedule.entity.forBD.ScheduleMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleMetadataRepository extends JpaRepository<ScheduleMetadataEntity, Long> {

    // Найти метаданные для конкретной сущности и семестра
    Optional<ScheduleMetadataEntity> findByEntityTypeAndEntityNameAndSemester(
            String entityType, String entityName, String semester);

    // Найти все метаданные для сущности (все семестры)
    List<ScheduleMetadataEntity> findByEntityTypeAndEntityName(
            String entityType, String entityName);

    // Найти все сущности определенного типа с устаревшим семестром
    List<ScheduleMetadataEntity> findByEntityTypeAndSemesterNot(
            String entityType, String currentSemester);

    // Обновить время последнего обновления и количество занятий
    @Modifying
    @Transactional
    @Query("UPDATE ScheduleMetadataEntity m SET m.lastUpdated = :lastUpdated, " +
            "m.lessonCount = :lessonCount WHERE m.id = :id")
    void updateMetadata(@Param("id") Long id,
                        @Param("lastUpdated") LocalDateTime lastUpdated,
                        @Param("lessonCount") Integer lessonCount);

    // Удалить метаданные для сущности и семестра
    @Modifying
    @Transactional
    void deleteByEntityTypeAndEntityNameAndSemester(
            String entityType, String entityName, String semester);

    // Проверить существование метаданных
    boolean existsByEntityTypeAndEntityNameAndSemester(
            String entityType, String entityName, String semester);
}