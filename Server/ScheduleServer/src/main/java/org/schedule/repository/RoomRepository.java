package org.schedule.repository;

import org.schedule.entity.forBD.basic.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    Optional<RoomEntity> findByRoomName(String roomName);
    boolean existsByRoomName(String roomName);
    Optional<RoomEntity> findByIdFromApi(Long idFromApi);

    @Query("SELECT r FROM RoomEntity r WHERE r.roomName IN :roomNames")
    List<RoomEntity> findByRoomNamesIn(@Param("roomNames") List<String> roomNames);
}