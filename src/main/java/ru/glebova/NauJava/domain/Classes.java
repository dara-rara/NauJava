package ru.glebova.NauJava.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;


@FieldDefaults(level = PRIVATE)
@Setter
@Getter
@Entity
@Table(name = "classes")
@FieldNameConstants
@NoArgsConstructor
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String name;
    @ManyToMany(mappedBy = "classes")
    private Set<Teacher> teachers = new HashSet<>();
    @OneToMany(mappedBy = "classes")
    private List<Lesson> lessons = new ArrayList<>();
    @OneToMany(mappedBy = "classes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();
    @OneToMany(mappedBy = "classes")
    private List<Lesson> lessonsPeriod = new ArrayList<>();
    @OneToMany(mappedBy = "classes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PeriodSchedule> periodSchedules = new ArrayList<>();
    @OneToMany(mappedBy = "classes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pupil> pupils = new ArrayList<>();

    public void removePupil(Pupil pupil) {
        this.pupils.remove(pupil);
    }
}