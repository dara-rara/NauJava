package ru.glebova.NauJava.adapter.repository.custom;

import ru.glebova.NauJava.domain.Grade;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.Subject;

import java.util.List;

public interface CustomGradeRepository {
    List<Grade> findGradesByPupilAndSubject(Pupil pupil, Subject subject);
}
