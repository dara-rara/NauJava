package ru.glebova.NauJava.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.domain.Classes;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.Users;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с учениками (Pupil).
 * Предоставляет методы для поиска, добавления и удаления учеников из классов.
 */
public interface PupilService {
    /**
     * Находит всех учеников указанного класса.
     *
     * @param schoolClass класс, для которого нужно найти учеников
     * @return список учеников данного класса
     */
    List<Pupil> findByClass(Classes schoolClass);

    /**
     * Находит ученика по связанному пользователю.
     *
     * @param user пользователь, для которого нужно найти ученика
     * @return Optional с найденным учеником или пустой, если ученик не найден
     */
    Optional<Pupil> findByUser(Users user);

    /**
     * Добавляет ученика в указанный класс.
     *
     * @param userId идентификатор пользователя, которого нужно добавить в класс
     * @param className название класса, в который нужно добавить ученика
     * @throws ResourceNotFoundException если пользователь или класс не найдены
     * @throws IllegalArgumentException если пользователь не имеет роли PUPIL
     * @throws IllegalStateException если ученик уже привязан к другому классу
     */
    void addPupilToClass(Long userId, String className);

    /**
     * Удаляет ученика из класса.
     *
     * @param pupilId идентификатор ученика, которого нужно удалить из класса
     * @throws ResourceNotFoundException если ученик не найден
     */
    void removePupilFromClass(Long pupilId);

    /**
     * Находит ученика по идентификатору.
     *
     * @param id идентификатор ученика
     * @return найденный ученик
     * @throws EntityNotFoundException если ученик не найден
     */
    Pupil findById(Long id);

    /**
     * Находит ученика по имени пользователя.
     *
     * @param username имя пользователя, для которого нужно найти ученика
     * @return найденный ученик
     * @throws EntityNotFoundException если пользователь или ученик не найдены
     */
    Pupil findByPupil(String username);
}