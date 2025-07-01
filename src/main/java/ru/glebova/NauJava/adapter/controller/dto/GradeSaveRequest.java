package ru.glebova.NauJava.adapter.controller.dto;

import lombok.Data;

import java.util.Map;

@Data
public class GradeSaveRequest {
    private Long classId;
    private Long teacherId;
    private Long termPeriodId;
    private Long subjectId;
    private Map<Long, Map<Long, Integer>> grades;
}
