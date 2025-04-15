package ru.glebova.NauJava.service;

import jakarta.persistence.EntityExistsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.controller.dto.RegisterDTO;
import ru.glebova.NauJava.adapter.repository.UsersRepository;
import ru.glebova.NauJava.domain.Role;
import ru.glebova.NauJava.domain.Users;

@Service
@Transactional
public class UsersService implements UserDetailsService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerNewUser(RegisterDTO registerDTO) {
        if (usersRepository.existsByUsername(registerDTO.getUsername())) {
            throw new EntityExistsException("Такой логин уже существует");
        }

        Users user = new Users();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setRole(Role.USER);
        usersRepository.save(user);
    }

    public boolean userExist(String username) {
        return usersRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
