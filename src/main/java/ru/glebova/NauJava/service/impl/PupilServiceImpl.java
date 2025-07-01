package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.repository.ClassesRepository;
import ru.glebova.NauJava.adapter.repository.PupilRepository;
import ru.glebova.NauJava.adapter.repository.UsersRepository;
import ru.glebova.NauJava.domain.Classes;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.Role;
import ru.glebova.NauJava.domain.Users;
import ru.glebova.NauJava.service.PupilService;
import ru.glebova.NauJava.service.UsersService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PupilServiceImpl implements PupilService {

    private final PupilRepository pupilRepository;
    private final UsersRepository usersRepository;
    private final ClassesRepository classesRepository;
    private final UsersService usersService;

    @Override
    @Transactional(readOnly = true)
    public List<Pupil> findByClass(Classes schoolClass) {
        log.debug("Finding pupils for class: {}", schoolClass.getName());
        List<Pupil> pupils = pupilRepository.findByClasses(schoolClass);
        log.debug("Found {} pupils for class {}", pupils.size(), schoolClass.getName());
        return pupils;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pupil> findByUser(Users user) {
        log.debug("Finding pupil by user: {}", user.getUsername());
        Optional<Pupil> pupil = pupilRepository.findByUsers(user);
        pupil.ifPresentOrElse(
                p -> log.debug("Found pupil with ID: {}", p.getId()),
                () -> log.debug("No pupil found for user {}", user.getUsername())
        );
        return pupil;
    }

    @Override
    @Transactional
    public void addPupilToClass(Long userId, String className) {
        log.info("Adding pupil with ID {} to class {}", userId, className);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        if (!user.getRole().equals(Role.PUPIL)) {
            log.error("User {} has invalid role: {}", user.getUsername(), user.getRole());
            throw new IllegalArgumentException("Role not pupil");
        }

        if (pupilRepository.findByUsers(user).isPresent()) {
            log.warn("User {} already has a class assigned", user.getUsername());
            throw new IllegalStateException("Pupil have classes");
        }

        Classes schoolClass = classesRepository.findByName(className)
                .orElseThrow(() -> {
                    log.error("Class not found with name: {}", className);
                    return new ResourceNotFoundException("Classes not found");
                });

        Pupil pupil = new Pupil();
        pupil.setUsers(user);
        pupil.setClasses(schoolClass);

        pupilRepository.save(pupil);
        log.info("Successfully added pupil {} to class {}",
                user.getUsername(), schoolClass.getName());
    }

    @Override
    @Transactional
    public void removePupilFromClass(Long pupilId) {
        log.info("Removing pupil with ID {} from class", pupilId);
        Pupil pupil = pupilRepository.findById(pupilId)
                .orElseThrow(() -> {
                    log.error("Pupil not found with ID: {}", pupilId);
                    return new ResourceNotFoundException("Pupil not found");
                });

        pupilRepository.delete(pupil);
        log.info("Successfully removed pupil {} from class",
                pupil.getUsers().getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public Pupil findById(Long id) {
        log.debug("Finding pupil by ID: {}", id);
        return pupilRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Pupil not found with ID: {}", id);
                    return new EntityNotFoundException("Pupil not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Pupil findByPupil(String username) {
        log.debug("Finding pupil by username: {}", username);
        Users users = usersService.loadUserByName(username);
        return pupilRepository.findByUsers(users)
                .orElseThrow(() -> {
                    log.error("Pupil not found for username: {}", username);
                    return new EntityNotFoundException("Pupil not found");
                });
    }
}