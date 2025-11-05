package org.schedule.entity.forBD.basic;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "groups")
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", unique = true, nullable = false)
    private String groupName;

    @Column(name = "id_from_api")
    private Long idFromApi;

    public Long getIdFromApi() {
        return idFromApi;
    }

    public void setIdFromApi(Long idFromApi) {
        this.idFromApi = idFromApi;
    }

    public GroupEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupEntity that = (GroupEntity) o;
        return Objects.equals(groupName, that.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName);
    }
}
