package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.*;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource
public interface PeriodLessonRepository extends JpaRepository<PeriodLesson, Long> {

    @Query("SELECT pl FROM PeriodLesson pl " +
            "LEFT JOIN FETCH pl.classes " +
            "LEFT JOIN FETCH pl.subject " +
            "LEFT JOIN FETCH pl.teacher " +
            "WHERE pl.teacher = :teacher AND pl.date BETWEEN :start AND :end")
    List<PeriodLesson> findByTeacherAndDateBetweenWithFetch(
            @Param("teacher") Teacher teacher,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT pl FROM PeriodLesson pl " +
            "LEFT JOIN FETCH pl.classes " +
            "LEFT JOIN FETCH pl.subject " +
            "LEFT JOIN FETCH pl.grades " +
            "LEFT JOIN FETCH pl.teacher " +
            "WHERE pl.classes = :classes AND pl.date BETWEEN :start AND :end")
    List<PeriodLesson> findByClassAndDateBetween(
            @Param("classes") Classes classes,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT DISTINCT pl.date FROM PeriodLesson pl " +
            "WHERE pl.teacher.id = :teacherId " +
            "AND pl.classes.id = :classId " +
            "AND pl.subject.id = :subjectId " +
            "AND pl.date BETWEEN :start AND :end " +
            "ORDER BY pl.date")
    List<LocalDate> findDistinctDatesByTeacherClassSubjectAndPeriod(
            @Param("teacherId") Long teacherId,
            @Param("classId") Long classId,
            @Param("subjectId") Long subjectId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT pl FROM PeriodLesson pl " +
            "WHERE pl.teacher.id = :teacherId " +
            "AND pl.classes.id = :classId " +
            "AND pl.subject.id = :subjectId " +
            "AND pl.date = :date")
    List<PeriodLesson> findLessonsByTeacherClassSubjectAndDate(
            @Param("teacherId") Long teacherId,
            @Param("classId") Long classId,
            @Param("subjectId") Long subjectId,
            @Param("date") LocalDate date);

    @Query("SELECT DISTINCT pl.subject FROM PeriodLesson pl " +
            "WHERE pl.teacher.id = :teacherId " +
            "AND pl.classes.id = :classId " +
            "AND pl.subject IN (SELECT ts FROM Teacher t JOIN t.subjects ts WHERE t.id = :teacherId)")
    List<Subject> findSubjectsByTeacherAndClass(
            @Param("teacherId") Long teacherId,
            @Param("classId") Long classId);

    List<PeriodLesson> findByTeacherAndDateBetween(Teacher teacher,
                                                   LocalDate startDate, LocalDate endDate);
}