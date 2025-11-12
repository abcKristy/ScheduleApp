package org.schedule.repository;

import org.schedule.entity.forBD.basic.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    Optional<GroupEntity> findByGroupName(String groupName);
    boolean existsByGroupName(String groupName);
    Optional<GroupEntity> findByIdFromApi(Long idFromApi);

    @Query("SELECT g FROM GroupEntity g WHERE g.groupName IN :groupNames")
    List<GroupEntity> findByGroupNamesIn(@Param("groupNames") List<String> groupNames);

    // Для массового поиска с возвратом Map
    @Query("SELECT g FROM GroupEntity g WHERE g.groupName IN :groupNames")
    List<GroupEntity> findByGroupNameIn(@Param("groupNames") Collection<String> groupNames);
}