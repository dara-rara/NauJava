package ru.glebova.NauJava.adapter.controller.dto;

import lombok.Getter;
import lombok.Setter;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.TermPeriod;

@Getter
@Setter
public class GradeSetupPupilDTO {
    private Pupil pupil;
    private Long termPeriodId;
}