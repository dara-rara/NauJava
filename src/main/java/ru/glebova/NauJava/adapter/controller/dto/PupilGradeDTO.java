package ru.glebova.NauJava.adapter.controller.dto;

import ru.glebova.NauJava.domain.Pupil;

import java.util.Map;

public record PupilGradeDTO(
        Pupil pupil,
        Map<Long, Integer> grades
) {}