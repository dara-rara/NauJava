package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Teacher;
import ru.glebova.NauJava.domain.Users;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface TeacherRepository extends CrudRepository<Teacher, Long> {
    Optional<Teacher> findByUsers(Users users);

    @Query("SELECT t FROM Teacher t JOIN t.subjects s WHERE s.id IN :subjectIds")
    List<Teacher> findBySubjectIds(@Param("subjectIds") List<Long> subjectIds);

    @Query("SELECT t FROM Teacher t JOIN t.classes c WHERE c.id IN :classIds")
    List<Teacher> findByClassIds(@Param("classIds") List<Long> classIds);

    boolean existsByUsers(Users users);

    List<Teacher> findAll();
}