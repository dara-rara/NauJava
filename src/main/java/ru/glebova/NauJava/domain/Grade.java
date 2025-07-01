package ru.glebova.NauJava.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;


@FieldDefaults(level = PRIVATE)
@Setter
@Getter
@Entity
@Table(name = "grade")
@FieldNameConstants
@NoArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "pupil_id", nullable = false)
    private Pupil pupil;
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer grade;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private PeriodLesson periodLesson;
}
