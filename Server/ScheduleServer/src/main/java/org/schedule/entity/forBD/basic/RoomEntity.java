package org.schedule.entity.forBD.basic;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name", unique = true, nullable = false)
    private String roomName;

    @Column(name = "id_from_api")
    private Long idFromApi;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Long getIdFromApi() {
        return idFromApi;
    }

    public void setIdFromApi(Long idFromApi) {
        this.idFromApi = idFromApi;
    }
}