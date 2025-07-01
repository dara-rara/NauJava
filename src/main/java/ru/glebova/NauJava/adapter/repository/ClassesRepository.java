package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Classes;
import org.springframework.data.repository.CrudRepository;
import ru.glebova.NauJava.domain.Subject;

import java.util.Optional;

@RepositoryRestResource
public interface ClassesRepository extends CrudRepository<Classes, Long> {
    Optional<Classes> findByName(String name);
    boolean existsByName(String name);
}