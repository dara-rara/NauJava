package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.controller.dto.SubjectDTO;
import ru.glebova.NauJava.adapter.repository.SubjectRepository;
import ru.glebova.NauJava.domain.Subject;
import ru.glebova.NauJava.domain.Teacher;
import ru.glebova.NauJava.service.PeriodLessonService;
import ru.glebova.NauJava.service.SubjectService;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final PeriodLessonService periodLessonService;

    @Override
    @Transactional
    public Subject createSubject(SubjectDTO subjectDTO) {
        log.info("Creating new subject with name: {}", subjectDTO.getName());

        subjectRepository.findByName(subjectDTO.getName()).ifPresent(s -> {
            log.error("Subject with name '{}' already exists", subjectDTO.getName());
            throw new EntityExistsException("Subject with this name already exists");
        });

        Subject subject = new Subject();
        subject.setName(subjectDTO.getName());
        Subject savedSubject = subjectRepository.save(subject);

        log.info("Successfully created subject with ID: {}", savedSubject.getId());
        return savedSubject;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        log.debug("Retrieving all subjects");
        List<Subject> subjects = (List<Subject>) subjectRepository.findAll();
        log.debug("Found {} subjects", subjects.size());
        return subjects;
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectDTO getSubjectByName(String name) {
        log.debug("Getting subject by name: {}", name);
        Subject subject = subjectRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Subject not found with name: {}", name);
                    return new EntityNotFoundException("Subject '" + name + "' not found");
                });

        SubjectDTO dto = new SubjectDTO();
        dto.setName(subject.getName());

        log.debug("Successfully mapped subject '{}' to DTO", name);
        return dto;
    }

    @Override
    @Transactional
    public void updateSubject(String originalName, String newName) {
        log.info("Updating subject from '{}' to '{}'", originalName, newName);

        Subject subject = subjectRepository.findByName(originalName)
                .orElseThrow(() -> {
                    log.error("Subject not found for update: {}", originalName);
                    return new EntityNotFoundException("Subject '" + originalName + "' not found");
                });

        if (!originalName.equals(newName) && subjectRepository.existsByName(newName)) {
            log.error("Cannot update subject - new name '{}' already exists", newName);
            throw new EntityExistsException("Subject with name '" + newName + "' exist");
        }

        subject.setName(newName);
        subjectRepository.save(subject);
        log.info("Successfully updated subject from '{}' to '{}'", originalName, newName);
    }

    @Override
    @Transactional
    public void deleteSubjectByName(String name) {
        log.info("Deleting subject with name: {}", name);
        Subject subject = subjectRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Subject not found for deletion: {}", name);
                    return new EntityNotFoundException("Subject '" + name + "' not found");
                });

        log.debug("Removing subject associations from {} teachers", subject.getTeachers().size());
        for (Teacher teacher : new HashSet<>(subject.getTeachers())) {
            teacher.removeSubject(subject);
        }

        subjectRepository.delete(subject);
        log.info("Successfully deleted subject: {}", name);
    }

    @Override
    @Transactional(readOnly = true)
    public Subject findById(Long id) {
        log.debug("Finding subject by ID: {}", id);
        return subjectRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Subject not found with ID: {}", id);
                    return new EntityNotFoundException("Subject '" + id + "' not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> getSubjectsForClass(Long teacherId, Long classId) {
        log.debug("Getting subjects for teacher {} and class {}", teacherId, classId);
        List<Subject> subjects = periodLessonService.findAvailableSubjects(teacherId, classId);

        if (subjects.isEmpty()) {
            log.warn("No subjects found for teacher {} and class {}", teacherId, classId);
            throw new IllegalStateException("Teacher doesn't teach any subjects for this class");
        }

        log.debug("Found {} subjects for teacher {} and class {}",
                subjects.size(), teacherId, classId);
        return subjects;
    }
}