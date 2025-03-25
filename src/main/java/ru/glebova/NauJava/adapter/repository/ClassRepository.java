package ru.glebova.NauJava.adapter.repository;

import ru.glebova.NauJava.domain.Class;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends CrudRepository<Class, Long> {

}