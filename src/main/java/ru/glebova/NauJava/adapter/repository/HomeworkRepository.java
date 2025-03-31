package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.repository.CrudRepository;
import ru.glebova.NauJava.domain.Homework;

public interface HomeworkRepository extends CrudRepository<Homework, Long> {

}
