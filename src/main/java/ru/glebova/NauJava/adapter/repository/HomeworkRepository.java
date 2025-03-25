package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.NauJava.domain.Homework;

@Repository
public interface HomeworkRepository extends CrudRepository<Homework, Long> {

}
