package ru.glebova.NauJava.adapter.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Teacher;

@RepositoryRestResource
public interface TeacherRepository extends CrudRepository<Teacher, Long> {

}