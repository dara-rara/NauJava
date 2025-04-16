package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Report;

import java.util.Optional;

@RepositoryRestResource
public interface ReportRepository extends CrudRepository<Report, Long> {
    @Query("SELECT r FROM Report r WHERE r.status <> 'CREATED' ORDER BY r.dateTime DESC LIMIT 1")
    Optional<Report> findTopByOrderByCreatedDateDesc();
}
