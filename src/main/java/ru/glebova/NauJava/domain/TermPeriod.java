package ru.glebova.NauJava.domain;

import jakarta.persistence.*;
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
@Table(name = "period")
@FieldNameConstants
@NoArgsConstructor
public class TermPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 250, unique = true)
    private String name;
    @Column(name = "date_start", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;
    @Column(name = "date_end", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate endDate;
}
