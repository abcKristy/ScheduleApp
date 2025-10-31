package org.schedule.repository;

import org.schedule.entity.forBD.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {
    Optional<LessonEntity> findByUid(String uid);
    boolean existsByUid(String uid);
}
