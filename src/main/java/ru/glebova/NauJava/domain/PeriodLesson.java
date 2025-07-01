package ru.glebova.NauJava.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Setter
@Getter
@FieldNameConstants
@NoArgsConstructor
@Entity
@Table(name = "lesson_period")
public class PeriodLesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private PeriodSchedule periodSchedule;
    @ManyToOne(fetch = FetchType.LAZY)
    private Teacher teacher;
    @ManyToOne(fetch = FetchType.LAZY)
    private Classes classes;
    @Column(nullable = false)
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Week weekDay;
    @Column(nullable = false, length = 20)
    private int lessonNumber;
    @Column(name = "time_start", nullable = false)
    @Temporal(TemporalType.TIME)
    private LocalTime timeStart;
    @Column(name = "time_end", nullable = false)
    @Temporal(TemporalType.TIME)
    private LocalTime timeEnd;
    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;
    @OneToOne(mappedBy = "periodLesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private Homework homework;
    @OneToMany(mappedBy = "periodLesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();
}
