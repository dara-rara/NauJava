package ru.glebova.NauJava.adapter.controller.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class LessonTemplateDTO {
    private Long id;
    private Integer lessonNumber;
    private Long subjectId;
    private Long teacherId;
    private String classroom;
    private LocalTime startTime;
    private LocalTime endTime;
}