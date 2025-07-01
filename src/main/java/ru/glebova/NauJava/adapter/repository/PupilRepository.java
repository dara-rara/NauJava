package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Classes;
import ru.glebova.NauJava.domain.Pupil;
import org.springframework.data.repository.CrudRepository;
import ru.glebova.NauJava.domain.Users;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface PupilRepository extends CrudRepository<Pupil, Long> {
    List<Pupil> findByClasses(Classes schoolClass);
    Optional<Pupil> findByUsers(Users user);
}