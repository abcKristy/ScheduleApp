package org.schedule.repository;

import org.schedule.entity.forBD.basic.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherEntity, Long> {
    Optional<TeacherEntity> findByFullName(String fullName);
    Optional<TeacherEntity> findByIdFromApi(Long idFromApi);

    @Query("SELECT t FROM TeacherEntity t WHERE t.fullName IN :fullNames")
    List<TeacherEntity> findByFullNamesIn(@Param("fullNames") List<String> fullNames);
}