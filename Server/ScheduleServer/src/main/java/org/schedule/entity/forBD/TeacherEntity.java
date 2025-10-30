package org.schedule.entity.forBD;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeacherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name",nullable = false)
    private String fullName;

    @Column(name = "email",nullable = false)
    private String email;

    @OneToMany(mappedBy = "teacher")
    private List<LessonEntity> lessons = new ArrayList<>();

}
