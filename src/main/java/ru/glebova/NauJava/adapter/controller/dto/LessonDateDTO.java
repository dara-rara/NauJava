package ru.glebova.NauJava.adapter.controller.dto;

import ru.glebova.NauJava.domain.PeriodLesson;

import java.time.LocalDate;
import java.util.List;

public record LessonDateDTO(
        LocalDate date,
        List<PeriodLesson> lessons
) {}