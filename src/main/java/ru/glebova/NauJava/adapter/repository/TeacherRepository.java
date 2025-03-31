package ru.glebova.NauJava.adapter.repository;
import org.springframework.data.repository.CrudRepository;
import ru.glebova.NauJava.domain.Teacher;

public interface TeacherRepository extends CrudRepository<Teacher, Long> {

}