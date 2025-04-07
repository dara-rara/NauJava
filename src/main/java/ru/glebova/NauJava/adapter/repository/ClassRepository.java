package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Classes;
import org.springframework.data.repository.CrudRepository;

@RepositoryRestResource
public interface ClassRepository extends CrudRepository<Classes, Long> {

}