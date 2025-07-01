package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.glebova.NauJava.adapter.controller.dto.RegisterDTO;
import ru.glebova.NauJava.adapter.controller.dto.UserDTO;
import ru.glebova.NauJava.adapter.repository.PupilRepository;
import ru.glebova.NauJava.adapter.repository.TeacherRepository;
import ru.glebova.NauJava.adapter.repository.UsersRepository;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.Role;
import ru.glebova.NauJava.domain.Teacher;
import ru.glebova.NauJava.domain.Users;
import ru.glebova.NauJava.exception.ImportException;
import ru.glebova.NauJava.service.UsersService;
import ru.glebova.NauJava.exception.dto.ImportResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UsersServiceImpl implements UserDetailsService, UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final PupilRepository pupilRepository;
    private final TeacherRepository teacherRepository;

    @Override
    @Transactional
    public void registerNewUser(RegisterDTO registerDTO) {
        log.info("Registering new user: {}", registerDTO.getUsername());

        if (usersRepository.existsByUsername(registerDTO.getUsername())) {
            log.error("Username {} already taken", registerDTO.getUsername());
            throw new EntityExistsException("Login already taken");
        }

        Users user = new Users();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setFirstName(registerDTO.getFirstName());
        user.setSecondName(registerDTO.getSecondName());
        user.setLastName(registerDTO.getLastName());
        user.setRole(Role.USER);

        Users savedUser = usersRepository.save(user);
        log.info("Successfully registered new user with ID: {}", savedUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userExist(String username) {
        log.debug("Checking if user exists: {}", username);
        boolean exists = usersRepository.existsByUsername(username);
        log.debug("User {} exists: {}", username, exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.debug("Fetching all users");
        List<Users> users = (List<Users>) usersRepository.findAll();
        log.debug("Found {} users", users.size());
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new EntityNotFoundException("User not found");
                });
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public Users createUser(UserDTO userDTO) {
        log.info("Creating new user: {}", userDTO.getUsername());

        if (usersRepository.existsByUsername(userDTO.getUsername())) {
            log.error("Username {} already exists", userDTO.getUsername());
            throw new EntityExistsException("Username already exists");
        }

        Users user = new Users();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setSecondName(userDTO.getSecondName());
        user.setLastName(userDTO.getLastName());
        user.setRole(userDTO.getRole());

        Users savedUser = usersRepository.save(user);
        log.info("Successfully created user with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    @Transactional
    public Users updateUser(Long id, UserDTO userDTO) {
        log.info("Updating user with ID: {}", id);

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new EntityNotFoundException("User not found");
                });

        if (!user.getUsername().equals(userDTO.getUsername()) &&
                usersRepository.existsByUsername(userDTO.getUsername())) {
            log.error("Username {} already exists", userDTO.getUsername());
            throw new EntityExistsException("Username already exists");
        }

        user.setUsername(userDTO.getUsername());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            log.debug("Updating password for user {}", id);
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setFirstName(userDTO.getFirstName());
        user.setSecondName(userDTO.getSecondName());
        user.setLastName(userDTO.getLastName());
        user.setRole(userDTO.getRole());

        Users updatedUser = usersRepository.save(user);
        log.info("Successfully updated user with ID: {}", id);
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        usersRepository.deleteById(id);
        log.info("Successfully deleted user with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new EntityNotFoundException("User not found");
                });
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public void updateUser(String originalUsername, UserDTO userDTO) {
        log.info("Updating user: {}", originalUsername);

        Users user = usersRepository.findByUsername(originalUsername)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", originalUsername);
                    return new EntityNotFoundException("User not found");
                });

        if (!originalUsername.equals(userDTO.getUsername())) {
            if (usersRepository.existsByUsername(userDTO.getUsername())) {
                log.error("Username {} already taken", userDTO.getUsername());
                throw new EntityExistsException("Login already taken");
            }
            user.setUsername(userDTO.getUsername());
            log.debug("Updated username from {} to {}", originalUsername, userDTO.getUsername());
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            log.debug("Updating password for user {}", originalUsername);
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        user.setFirstName(userDTO.getFirstName());
        user.setSecondName(userDTO.getSecondName());
        user.setLastName(userDTO.getLastName());
        user.setRole(userDTO.getRole());

        usersRepository.save(user);
        log.info("Successfully updated user: {}", userDTO.getUsername());
    }

    @Override
    @Transactional
    public void deleteUserByUsername(String username) {
        log.info("Deleting user: {}", username);
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new EntityNotFoundException("User not found");
                });
        Optional<Pupil> pupil = pupilRepository.findByUsers(user);
        if (pupil.isPresent()) {
            pupilRepository.delete(pupil.get());
        }
        Optional<Teacher> teacher = teacherRepository.findByUsers(user);
        if (teacher.isPresent()) {
            teacherRepository.delete(teacher.get());
        }
        usersRepository.delete(user);
        log.info("Successfully deleted user: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Users> findUsersByRole(Role role) {
        log.debug("Finding users with role: {}", role);
        List<Users> users = usersRepository.findByRole(role);
        log.debug("Found {} users with role {}", users.size(), role);
        return users;
    }

    @Override
    @Transactional
    public ImportResult importUsersFromFile(MultipartFile file, Role role) throws IOException {
        log.info("Importing users from file with role: {}", role);
        ImportResult result = new ImportResult();
        List<Users> usersToSave = new ArrayList<>();
        List<String> existingUsernames = usersRepository.findAllUsernames();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine(); // Пропускаем заголовок

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;

                try {
                    String[] parts = line.split(";");
                    if (parts.length < 4) {
                        throw new ImportException("Invalid data: need 4 column", lineNumber);
                    }

                    String username = parts[0].trim();
                    if (username.isEmpty()) {
                        throw new ImportException("Empty login", lineNumber);
                    }

                    if (existingUsernames.contains(username)) {
                        throw new ImportException("Login '" + username + "' already taken", lineNumber);
                    }

                    Users user = new Users();
                    user.setUsername(username);
                    user.setPassword(passwordEncoder.encode(parts[1].trim()));
                    user.setFirstName(parts[2].trim());
                    user.setSecondName(parts[3].trim());
                    user.setLastName(parts.length > 4 ? parts[4].trim() : "");
                    user.setRole(role);

                    usersToSave.add(user);
                    existingUsernames.add(username);
                    result.setSuccessCount(result.getSuccessCount() + 1);
                    log.debug("Prepared user {} for import (line {})", username, lineNumber);
                } catch (ImportException e) {
                    result.addError("Line " + e.getLineNumber() + ": " + e.getMessage());
                    log.warn("Import error at line {}: {}", e.getLineNumber(), e.getMessage());
                }
            }
        }

        if (!usersToSave.isEmpty()) {
            usersRepository.saveAll(usersToSave);
            log.info("Successfully imported {} users", usersToSave.size());
        } else {
            log.warn("No users were imported from file");
        }

        return result;
    }

    @Override
    public void generateUserTemplate(OutputStream outputStream) throws IOException {
        log.debug("Generating user import template");
        outputStream.write(0xEF);
        outputStream.write(0xBB);
        outputStream.write(0xBF);

        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat excelFormat = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withQuote('"')
                .withHeader("Username", "Password", "First Name", "Second Name", "Last Name");

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, excelFormat)) {
            csvPrinter.printRecord("user1", "password123", "Иван", "Иванович", "Иванов");
            csvPrinter.printRecord("user2", "password123", "Мария", "Петровна", "Сидорова");
            csvPrinter.flush();
        }
        log.debug("Successfully generated user import template");
    }

    @Override
    public UserDTO convertToDTO(Users user) {
        log.trace("Converting user {} to DTO", user.getUsername());
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setSecondName(user.getSecondName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        log.debug("Loading user details by username: {}", username);
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Users loadUserById(Long id) {
        log.debug("Loading user by ID: {}", id);
        return usersRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new UsernameNotFoundException("User not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Users loadUserByName(String username) {
        log.debug("Loading user by name: {}", username);
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found");
                });
    }
}