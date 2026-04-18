package org.schedule.repository;

import org.schedule.entity.forBD.basic.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, String> {

    List<LessonEntity> findByGroups_GroupName(String groupName);
    List<LessonEntity> findByTeachers_FullName(String teacherName);
    List<LessonEntity> findByRooms_RoomName(String roomName);

    List<LessonEntity> findByDisciplineAndStartTimeAndEndTime(
            String discipline,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    @Query("SELECT l FROM LessonEntity l WHERE l.discipline = :discipline AND l.startTime = :startTime AND l.endTime = :endTime AND l.semester = :semester")
    List<LessonEntity> findByDisciplineAndStartTimeAndEndTimeAndSemester(
            @Param("discipline") String discipline,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("semester") String semester
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM LessonEntity l WHERE l.semester != :currentSemester")
    int deleteBySemesterNot(@Param("currentSemester") String currentSemester);


    @Modifying
    @Transactional
    @Query("DELETE FROM LessonEntity l WHERE l.endTime < :cutoffDate")
    int deleteByEndTimeBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT DISTINCT l.semester FROM LessonEntity l")
    List<String> findDistinctSemesters();

    @Query("SELECT l.semester, COUNT(l) FROM LessonEntity l GROUP BY l.semester")
    List<Object[]> countLessonsBySemester();

    @Modifying
    @Transactional
    @Query("DELETE FROM LessonEntity l WHERE l.semester = :semester")
    int deleteBySemester(@Param("semester") String semester);
}