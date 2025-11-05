package org.schedule.repository;

import org.schedule.entity.forBD.basic.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    Optional<RoomEntity> findByRoomName(String roomName);
    boolean existsByRoomName(String roomName);
    Optional<RoomEntity> findByIdFromApi(Long idFromApi);
}