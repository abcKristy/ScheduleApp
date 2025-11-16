package org.schedule.repository;

import org.schedule.entity.forBD.basic.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {
    List<LessonEntity> findByGroups_GroupName(String groupName);
    List<LessonEntity> findByTeachers_FullName(String teacherName);
    List<LessonEntity> findByRooms_RoomName(String roomName);

    List<LessonEntity> findByDisciplineAndStartTimeAndEndTime(
            String discipline,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}
