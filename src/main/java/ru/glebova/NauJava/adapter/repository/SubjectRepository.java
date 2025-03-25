package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.NauJava.domain.Subject;

@Repository
public interface SubjectRepository extends CrudRepository<Subject, Long> {

}
