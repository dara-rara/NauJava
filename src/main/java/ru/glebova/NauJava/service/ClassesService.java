package ru.glebova.NauJava.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import ru.glebova.NauJava.adapter.controller.dto.ClassesDTO;
import ru.glebova.NauJava.domain.Classes;

import java.util.List;

/**
 * Сервис для работы с учебными классами (Classes).
 * Предоставляет CRUD-операции и методы поиска классов.
 */
public interface ClassesService {

    /**
     * Находит класс по названию и возвращает его DTO-представление.
     *
     * @param name название класса (например, "10-A")
     * @return {@link ClassesDTO} с данными класса
     * @throws EntityNotFoundException если класс с указанным именем не найден
     */
    ClassesDTO getClassByName(String name);

    /**
     * Создает новый класс на основе данных из DTO.
     *
     * @param classesDTO DTO с данными для создания класса
     * @throws EntityExistsException если класс с таким именем уже существует
     */
    void createClass(ClassesDTO classesDTO);

    /**
     * Обновляет название существующего класса.
     *
     * @param originalName текущее название класса (например, "10-A")
     * @param newName новое название класса (например, "10-B")
     * @throws EntityNotFoundException если класс с originalName не найден
     * @throws EntityExistsException если класс с newName уже существует
     */
    void updateClasses(String originalName, String newName);

    /**
     * Удаляет класс по названию.
     *
     * @param name название удаляемого класса
     * @throws EntityNotFoundException если класс с указанным именем не найден
     */
    void deleteClassesByName(String name);

    /**
     * Преобразует сущность класса в DTO.
     *
     * @param classes сущность класса для преобразования
     * @return {@link ClassesDTO} с данными класса
     */
    ClassesDTO mapToDTO(Classes classes);

    /**
     * Находит сущность класса по названию.
     *
     * @param name название класса для поиска
     * @return найденная сущность {@link Classes}
     * @throws ResourceNotFoundException если класс не найден
     */
    Classes findByName(String name);

    /**
     * Возвращает список всех классов.
     *
     * @return список сущностей {@link Classes} (может быть пустым)
     */
    List<Classes> getAllClasses();

    /**
     * Находит сущность класса по идентификатору.
     *
     * @param id уникальный идентификатор класса
     * @return найденная сущность {@link Classes}
     * @throws ResourceNotFoundException если класс с указанным ID не найден
     */
    Classes findById(Long id);
}