package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Subject;

@RepositoryRestResource
public interface SubjectRepository extends CrudRepository<Subject, Long> {

}
