package ru.glebova.NauJava.adapter.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.NauJava.domain.Teacher;

@Repository
public interface TeacherRepository extends CrudRepository<Teacher, Long> {

}