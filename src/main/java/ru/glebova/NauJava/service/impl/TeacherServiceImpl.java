package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.repository.*;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.service.PeriodLessonService;
import ru.glebova.NauJava.service.TeacherService;
import ru.glebova.NauJava.service.UsersService;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final UsersRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ClassesRepository classRepository;
    private final UsersService usersService;

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        log.debug("Retrieving all teachers");
        List<Teacher> teachers = (List<Teacher>) teacherRepository.findAll();
        log.debug("Found {} teachers", teachers.size());
        return teachers;
    }

    @Override
    @Transactional(readOnly = true)
    public Teacher findById(Long id) {
        log.debug("Looking for teacher with ID: {}", id);
        return teacherRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Teacher not found with ID: {}", id);
                    return new EntityNotFoundException("Teacher not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Teacher findByTeacher(String username) {
        log.debug("Finding teacher by username: {}", username);
        Users users = usersService.loadUserByName(username);
        return teacherRepository.findByUsers(users)
                .orElseThrow(() -> {
                    log.error("Teacher not found for username: {}", username);
                    return new EntityNotFoundException("Teacher not found");
                });
    }

    @Override
    @Transactional
    public Teacher createTeacher(Teacher teacher, Long userId, List<Long> subjectIds, List<Long> classIds) {
        log.info("Creating new teacher for user ID: {}", userId);

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new EntityNotFoundException("User not found");
                });

        if (!user.getRole().equals(Role.TEACHER)) {
            log.error("User {} has invalid role: {}", userId, user.getRole());
            throw new IllegalArgumentException("Role not teacher");
        }

        if (teacherRepository.existsByUsers(usersService.loadUserById(userId))) {
            log.error("User {} is already a teacher", userId);
            throw new IllegalArgumentException("User is teacher");
        }

        teacher.setUsers(user);
        log.debug("Set user for new teacher");

        if (subjectIds != null && !subjectIds.isEmpty()) {
            Set<Subject> subjects = new HashSet<>((Collection) subjectRepository.findAllById(subjectIds));
            teacher.setSubjects(subjects);
            log.debug("Added {} subjects to teacher", subjects.size());
        }

        if (classIds != null && !classIds.isEmpty()) {
            Set<Classes> classes = new HashSet<>((Collection) classRepository.findAllById(classIds));
            teacher.setClasses(classes);
            log.debug("Added {} classes to teacher", classes.size());
        }

        Teacher savedTeacher = teacherRepository.save(teacher);
        log.info("Successfully created teacher with ID: {}", savedTeacher.getId());
        return savedTeacher;
    }

    @Override
    @Transactional
    public Teacher updateTeacher(Long id, Teacher teacherDetails, List<Long> subjectIds, List<Long> classIds) {
        log.info("Updating teacher with ID: {}", id);

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Teacher not found with ID: {}", id);
                    return new IllegalArgumentException("Teacher not found");
                });

        if (subjectIds != null) {
            Set<Subject> subjects = new HashSet<>((Collection) subjectRepository.findAllById(subjectIds));
            teacher.setSubjects(subjects);
            log.debug("Updated subjects for teacher, now has {} subjects", subjects.size());
        }

        if (classIds != null) {
            Set<Classes> classes = new HashSet<>((Collection) classRepository.findAllById(classIds));
            teacher.setClasses(classes);
            log.debug("Updated classes for teacher, now has {} classes", classes.size());
        }

        Teacher updatedTeacher = teacherRepository.save(teacher);
        log.info("Successfully updated teacher with ID: {}", id);
        return updatedTeacher;
    }

    @Override
    @Transactional
    public void deleteTeacher(Long id) {
        log.info("Deleting teacher with ID: {}", id);

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Teacher not found with ID: {}", id);
                    return new IllegalArgumentException("Teacher not found");
                });

        log.debug("Clearing {} subjects and {} classes associations",
                teacher.getSubjects().size(), teacher.getClasses().size());
        teacher.getSubjects().clear();
        teacher.getClasses().clear();

        teacherRepository.delete(teacher);
        log.info("Successfully deleted teacher with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {
        log.debug("Checking if teacher exists for user ID: {}", userId);
        boolean exists = teacherRepository.existsByUsers(usersService.loadUserById(userId));
        log.debug("Teacher exists for user {}: {}", userId, exists);
        return exists;
    }
}