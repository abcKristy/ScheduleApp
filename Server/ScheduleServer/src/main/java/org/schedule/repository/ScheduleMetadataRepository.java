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

    Optional<ScheduleMetadataEntity> findByEntityTypeAndEntityNameAndSemester(
            String entityType, String entityName, String semester);

    List<ScheduleMetadataEntity> findByEntityTypeAndEntityName(
            String entityType, String entityName);

    List<ScheduleMetadataEntity> findByEntityTypeAndSemesterNot(
            String entityType, String currentSemester);

    @Query("SELECT m FROM ScheduleMetadataEntity m WHERE " +
            "m.entityType = :entityType AND m.entityName IN :entityNames AND m.semester = :semester")
    List<ScheduleMetadataEntity> findByEntityTypeAndEntityNameInAndSemester(
            @Param("entityType") String entityType,
            @Param("entityNames") List<String> entityNames,
            @Param("semester") String semester);

    @Query("SELECT COUNT(m) FROM ScheduleMetadataEntity m WHERE " +
            "m.entityType = :entityType AND m.entityName = :entityName AND m.semester = :semester")
    int countByEntityTypeAndEntityNameAndSemester(
            @Param("entityType") String entityType,
            @Param("entityName") String entityName,
            @Param("semester") String semester);

    @Modifying
    @Transactional
    @Query("UPDATE ScheduleMetadataEntity m SET m.lastUpdated = :lastUpdated, " +
            "m.lessonCount = :lessonCount WHERE m.id = :id")
    void updateMetadata(@Param("id") Long id,
                        @Param("lastUpdated") LocalDateTime lastUpdated,
                        @Param("lessonCount") Integer lessonCount);

    @Modifying
    @Transactional
    void deleteByEntityTypeAndEntityNameAndSemester(
            String entityType, String entityName, String semester);

    boolean existsByEntityTypeAndEntityNameAndSemester(
            String entityType, String entityName, String semester);

    @Modifying
    @Transactional
    @Query("DELETE FROM ScheduleMetadataEntity m WHERE m.semester != :currentSemester")
    int deleteBySemesterNot(@Param("currentSemester") String currentSemester);

    @Modifying
    @Transactional
    @Query("UPDATE ScheduleMetadataEntity m SET m.lessonCount = " +
            "(SELECT COUNT(l) FROM LessonEntity l WHERE " +
            "l.semester = m.semester AND " +
            "(EXISTS (SELECT g FROM l.groups g WHERE g.groupName = m.entityName) OR " +
            "EXISTS (SELECT t FROM l.teachers t WHERE t.fullName = m.entityName) OR " +
            "EXISTS (SELECT r FROM l.rooms r WHERE r.roomName = m.entityName))) " +
            "WHERE m.id = :id")
    void recalculateLessonCount(@Param("id") Long id);

    @Query("SELECT m FROM ScheduleMetadataEntity m WHERE m.lastUpdated < :cutoffDate")
    List<ScheduleMetadataEntity> findOutdatedMetadata(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM ScheduleMetadataEntity m WHERE m.lastUpdated < :cutoffDate AND m.semester != :currentSemester")
    int deleteOutdatedMetadata(@Param("cutoffDate") LocalDateTime cutoffDate,
                               @Param("currentSemester") String currentSemester);
}