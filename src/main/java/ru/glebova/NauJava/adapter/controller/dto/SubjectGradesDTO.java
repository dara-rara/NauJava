package ru.glebova.NauJava.adapter.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.glebova.NauJava.domain.Subject;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class SubjectGradesDTO {
    private Subject subject;
    private Map<LocalDate, List<Integer>> gradesByDate;
    private Double averageGrade;
}
