package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Classes;
import ru.glebova.NauJava.domain.Schedule;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
    Optional<Schedule> findByClasses(Classes classes);
}