package ru.glebova.NauJava.adapter.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.glebova.NauJava.domain.Role;
import ru.glebova.NauJava.domain.Users;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface UsersRepository extends CrudRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    Boolean existsByUsername(String username);

    @Query("SELECT u.username FROM Users u")
    List<String> findAllUsernames();

    List<Users> findByRole(Role role);
}
