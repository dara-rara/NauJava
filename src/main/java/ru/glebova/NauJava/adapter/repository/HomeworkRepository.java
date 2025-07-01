package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Homework;
import ru.glebova.NauJava.domain.PeriodLesson;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface HomeworkRepository extends CrudRepository<Homework, Long> {
    @Query("SELECT h FROM Homework h JOIN FETCH h.periodLesson WHERE h.id = :id")
    Optional<Homework> findByIdWithPeriodLesson(@Param("id") Long id);

    List<Homework> findByPeriodLessonIn(List<PeriodLesson> periodLessons);
}
