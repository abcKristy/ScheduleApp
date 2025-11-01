package org.schedule.entity.forBD.basic;


import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeacherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "id_from_api")
    private Long idFromApi;

    // Связь Many-to-Many с уроками
    @ManyToMany(mappedBy = "teachers")
    @ToString.Exclude
    private List<LessonEntity> lessons = new ArrayList<>();

    public Long getIdFromApi() {
        return idFromApi;
    }

    public void setIdFromApi(Long idFromApi) {
        this.idFromApi = idFromApi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public List<LessonEntity> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonEntity> lessons) {
        this.lessons = lessons;
    }
}
