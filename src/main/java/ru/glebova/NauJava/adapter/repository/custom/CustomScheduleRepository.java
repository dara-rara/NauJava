package ru.glebova.NauJava.adapter.repository.custom;

import ru.glebova.NauJava.domain.Schedule;

import java.util.List;

public interface CustomScheduleRepository {
    List<Schedule> findScheduleByClassAndSubject(String className, String subjectName);
}
