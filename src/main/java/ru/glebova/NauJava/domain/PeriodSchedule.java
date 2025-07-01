package ru.glebova.NauJava.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Setter
@Getter
@FieldNameConstants
@NoArgsConstructor
@Entity
@Table(name = "schedule_period")
public class PeriodSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private TermPeriod period;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Classes classes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Schedule templateSchedule;
    @OneToMany(mappedBy = "periodSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PeriodLesson> lessons = new ArrayList<>();
}
