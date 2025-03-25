package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.NauJava.domain.Users;

@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {

}
