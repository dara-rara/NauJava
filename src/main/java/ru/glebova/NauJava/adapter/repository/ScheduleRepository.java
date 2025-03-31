package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.glebova.NauJava.domain.Schedule;

import java.util.List;

public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s " +
            "JOIN s.classes c JOIN s.subject sub " +
            "WHERE c.name = :className " +
            "AND sub.name = :subjectName")
    List<Schedule> findByClassNameAndSubjectName(String className, String subjectName);
}