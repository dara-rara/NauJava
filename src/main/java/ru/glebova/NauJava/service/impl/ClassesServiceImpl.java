package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.controller.dto.ClassesDTO;
import ru.glebova.NauJava.adapter.repository.ClassesRepository;
import ru.glebova.NauJava.domain.Classes;
import ru.glebova.NauJava.domain.Teacher;
import ru.glebova.NauJava.service.ClassesService;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ClassesServiceImpl implements ClassesService {

    private final ClassesRepository classesRepository;

    @Override
    @Transactional(readOnly = true)
    public ClassesDTO getClassByName(String name) {
        log.info("Getting class by name: {}", name);
        Classes classes = classesRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Class not found with name: {}", name);
                    return new EntityNotFoundException("Classes '" + name + "' not found");
                });
        log.debug("Successfully retrieved class: {}", name);
        return mapToDTO(classes);
    }

    @Override
    @Transactional
    public void createClass(ClassesDTO classesDTO) {
        log.info("Creating new class with name: {}", classesDTO.getName());
        if (classesRepository.existsByName(classesDTO.getName())) {
            log.error("Class already exists with name: {}", classesDTO.getName());
            throw new EntityExistsException("Classes with name exist");
        }

        Classes classes = new Classes();
        classes.setName(classesDTO.getName());
        Classes savedClass = classesRepository.save(classes);
        log.info("Successfully created class with ID: {}", savedClass.getId());
    }

    @Override
    @Transactional
    public void updateClasses(String originalName, String newName) {
        log.info("Updating class from '{}' to '{}'", originalName, newName);
        Classes classes = classesRepository.findByName(originalName)
                .orElseThrow(() -> {
                    log.error("Class not found for update: {}", originalName);
                    return new EntityNotFoundException("Classes '" + originalName + "' not found");
                });

        if (!originalName.equals(newName) && classesRepository.existsByName(newName)) {
            log.error("Cannot update class - new name already exists: {}", newName);
            throw new EntityExistsException("Classes with name exist");
        }

        classes.setName(newName);
        classesRepository.save(classes);
        log.info("Successfully updated class from '{}' to '{}'", originalName, newName);
    }

    @Override
    @Transactional
    public void deleteClassesByName(String name) {
        log.info("Deleting class with name: {}", name);
        Classes classes = classesRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Class not found for deletion: {}", name);
                    return new EntityNotFoundException("Classes '" + name + "' not found");
                });

        log.debug("Removing class associations from teachers");
        for (Teacher teacher : new HashSet<>(classes.getTeachers())) {
            teacher.removeClasses(classes);
        }

        classesRepository.delete(classes);
        log.info("Successfully deleted class: {}", name);
    }

    @Override
    public ClassesDTO mapToDTO(Classes classes) {
        log.trace("Mapping Class entity to DTO for class: {}", classes.getName());
        ClassesDTO dto = new ClassesDTO();
        dto.setName(classes.getName());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Classes findByName(String name) {
        log.debug("Finding class by name: {}", name);
        return classesRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Class not found: {}", name);
                    return new ResourceNotFoundException("Classes not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Classes findById(Long id) {
        log.debug("Finding class by ID: {}", id);
        return classesRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Class not found with ID: {}", id);
                    return new ResourceNotFoundException("Classes not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Classes> getAllClasses() {
        log.info("Retrieving all classes");
        List<Classes> classes = (List<Classes>) classesRepository.findAll();
        log.debug("Found {} classes", classes.size());
        return classes;
    }
}