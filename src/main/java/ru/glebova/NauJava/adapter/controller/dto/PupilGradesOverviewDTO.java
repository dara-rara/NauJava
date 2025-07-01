package ru.glebova.NauJava.adapter.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.TermPeriod;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PupilGradesOverviewDTO {
    private Pupil pupil;
    private TermPeriod termPeriod;
    private List<SubjectGradesDTO> subjectsGrades;
    private List<LocalDate> workingDates;
}
