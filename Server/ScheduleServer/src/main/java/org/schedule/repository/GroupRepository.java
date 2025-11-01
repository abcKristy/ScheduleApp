package org.schedule.repository;

import org.schedule.entity.forBD.basic.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    Optional<GroupEntity> findByGroupName(String groupName);
    boolean existsByGroupName(String groupName);
    Optional<GroupEntity> findByIdFromApi(Long idFromApi);
}