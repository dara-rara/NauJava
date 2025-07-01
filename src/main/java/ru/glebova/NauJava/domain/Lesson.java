package ru.glebova.NauJava.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.time.LocalTime;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Setter
@Getter
@Entity
@Table(name = "lesson")
@FieldNameConstants
@NoArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Classes classes;
    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Schedule schedule;
    @ManyToOne(fetch = FetchType.LAZY)
    private Teacher teacher;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Week week;
    @Column(nullable = false, length = 20)
    private int lessonNumber;      // Порядковый номер урока в день
    @Column(name = "time_start", nullable = false)
    @Temporal(TemporalType.TIME)
    private LocalTime timeStart;
    @Column(name = "time_end", nullable = false)
    @Temporal(TemporalType.TIME)
    private LocalTime timeEnd;
}
