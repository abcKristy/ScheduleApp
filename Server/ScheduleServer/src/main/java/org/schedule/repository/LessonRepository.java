package org.schedule.repository;

import org.schedule.entity.forBD.basic.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {
    Optional<LessonEntity> findByUid(String uid);
    boolean existsByUid(String uid);
    List<LessonEntity> findByGroups_GroupName(String groupName);
    List<LessonEntity> findByTeachers_FullName(String teacherName);
    List<LessonEntity> findByRooms_RoomName(String roomName);
}
