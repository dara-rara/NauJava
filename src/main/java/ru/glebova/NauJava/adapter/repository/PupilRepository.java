package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Pupil;
import org.springframework.data.repository.CrudRepository;

@RepositoryRestResource
public interface PupilRepository extends CrudRepository<Pupil, Long> {

}