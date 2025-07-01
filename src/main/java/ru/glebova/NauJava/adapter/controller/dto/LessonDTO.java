package ru.glebova.NauJava.adapter.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
public class LessonDTO {
    private Long id;
    private LocalDate date;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private Integer lessonNumber;
    private String subjectName;
    private String teacherFullName;
    private Integer gradeValue;
    private String homeworkDescription;
}