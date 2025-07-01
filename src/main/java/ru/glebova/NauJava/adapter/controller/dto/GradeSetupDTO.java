package ru.glebova.NauJava.adapter.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeSetupDTO {
    private Long classId;
    private Long teacherId;
    private Long termPeriodId;
    private Long subjectId;
}