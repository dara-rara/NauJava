package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.NauJava.domain.Schedule;

import java.util.List;

@Repository
public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s " +
            "JOIN s.classValue c JOIN s.subject sub " +
            "WHERE c.className = :className " +
            "AND sub.subjectName = :subjectName")
    List<Schedule> findByClassNameAndSubjectName(String className, String subjectName);
}