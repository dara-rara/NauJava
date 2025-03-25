package ru.glebova.NauJava.adapter.custom;

import ru.glebova.NauJava.domain.Schedule;

import java.util.List;

public interface CustomScheduleRepository {
    List<Schedule> findScheduleByClassValueAndSubject(String className, String subjectName);
}
