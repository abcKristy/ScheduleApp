package org.schedule.entity.forBD;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "academic_weeks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AcademicWeek {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "week_number",nullable = false)
    private Integer weekNumber;

    @Column(name = "start_date",nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date",nullable = false)
    private LocalDateTime endDate;

    @Column(name = "description")
    private String description;
}
