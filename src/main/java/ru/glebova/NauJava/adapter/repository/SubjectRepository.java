package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.repository.CrudRepository;
import ru.glebova.NauJava.domain.Subject;

public interface SubjectRepository extends CrudRepository<Subject, Long> {

}
