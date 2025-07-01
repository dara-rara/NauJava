package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Grade;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.Subject;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource
public interface GradeRepository extends CrudRepository<Grade, Long> {
    List<Grade> findByPupilAndSubject(Pupil pupil, Subject subject);

    List<Grade> findByPupil(Pupil pupil);

    @Query("SELECT g FROM Grade g WHERE " +
            "g.pupil = :pupil AND " +
            "g.subject = :subject AND " +
            "g.periodLesson.date BETWEEN :startDate AND :endDate")
    List<Grade> findByPupilAndSubjectAndPeriod(
            @Param("pupil") Pupil pupil,
            @Param("subject") Subject subject,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT g FROM Grade g " +
            "JOIN g.pupil p " +
            "JOIN g.periodLesson pl " +
            "WHERE p.classes.id = :classId " +
            "AND g.subject.id = :subjectId " +
            "AND pl.date BETWEEN :startDate AND :endDate")
    List<Grade> findByClassAndSubjectAndPeriod(
            @Param("classId") Long classId,
            @Param("subjectId") Long subjectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT g FROM Grade g " +
            "JOIN FETCH g.periodLesson pl " +
            "JOIN FETCH g.pupil p " +  // явное соединение с учеником
            "WHERE p.id = :pupilId " +
            "AND pl.date BETWEEN :startDate AND :endDate")
    List<Grade> findByPupilAndLessonDateBetween(
            @Param("pupilId") Long pupilId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
