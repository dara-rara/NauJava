package ru.glebova.NauJava.adapter.repository;

import ru.glebova.NauJava.domain.Pupil;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PupilRepository extends CrudRepository<Pupil, Long> {

}