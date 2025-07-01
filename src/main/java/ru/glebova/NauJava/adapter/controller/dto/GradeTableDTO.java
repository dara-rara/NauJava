package ru.glebova.NauJava.adapter.controller.dto;

import ru.glebova.NauJava.domain.Classes;
import ru.glebova.NauJava.domain.Subject;
import ru.glebova.NauJava.domain.Teacher;
import ru.glebova.NauJava.domain.TermPeriod;

import java.util.List;

public record GradeTableDTO(
        Classes classInfo,
        Teacher teacher,
        TermPeriod termPeriod,
        Subject selectedSubject,
        List<LessonDateDTO> lessonDates,
        List<PupilGradeDTO> pupilsGrades,
        List<Subject> availableSubjects
) {}