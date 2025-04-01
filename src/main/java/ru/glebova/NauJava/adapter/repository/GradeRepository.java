package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Grade;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.Subject;

import java.util.List;

@RepositoryRestResource
public interface GradeRepository extends CrudRepository<Grade, Long> {
    List<Grade> findByPupilAndSubject(Pupil pupil, Subject subject);

    List<Grade> findByPupil(Pupil pupil);
}
