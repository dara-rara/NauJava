package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Classes;
import ru.glebova.NauJava.domain.PeriodSchedule;
import ru.glebova.NauJava.domain.TermPeriod;

import java.util.Optional;

@RepositoryRestResource
public interface PeriodScheduleRepository extends CrudRepository<PeriodSchedule, Long> {
    boolean existsByClassesAndPeriod(Classes schoolClass, TermPeriod period);

    void deleteByClassesAndPeriod(Classes schoolClass, TermPeriod period);

    @Query("SELECT ps FROM PeriodSchedule ps " +
            "LEFT JOIN FETCH ps.lessons l " +
            "LEFT JOIN FETCH l.subject " +
            "WHERE ps.classes = :classes AND ps.period = :period")
    Optional<PeriodSchedule> findByClassesAndPeriodWithLessons(
            @Param("classes") Classes classes,
            @Param("period") TermPeriod period);

}