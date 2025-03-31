package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.repository.CrudRepository;
import ru.glebova.NauJava.domain.Users;

public interface UsersRepository extends CrudRepository<Users, Long> {

}
