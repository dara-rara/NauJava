package ru.glebova.NauJava.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import ru.glebova.NauJava.adapter.controller.dto.SubjectDTO;
import ru.glebova.NauJava.domain.Subject;

import java.util.List;

/**
 * Сервис для работы с учебными предметами.
 * Предоставляет методы для создания, получения, обновления и удаления предметов,
 * а также для получения предметов, связанных с определенным учителем и классом.
 */
public interface SubjectService {

    /**
     * Создает новый учебный предмет на основе предоставленных данных.
     *
     * @param subjectDTO DTO объект с данными для создания предмета
     * @return созданный объект Subject
     * @throws EntityExistsException если предмет с таким названием уже существует
     */
    Subject createSubject(SubjectDTO subjectDTO);

    /**
     * Получает список всех учебных предметов.
     *
     * @return список всех предметов
     */
    List<Subject> getAllSubjects();

    /**
     * Удаляет учебный предмет по его названию.
     *
     * @param name название удаляемого предмета
     * @throws EntityNotFoundException если предмет с указанным названием не найден
     */
    void deleteSubjectByName(String name);

    /**
     * Получает DTO учебного предмета по его названию.
     *
     * @param name название предмета
     * @return DTO объекта Subject
     * @throws EntityNotFoundException если предмет с указанным названием не найден
     */
    SubjectDTO getSubjectByName(String name);

    /**
     * Обновляет название учебного предмета.
     *
     * @param originalName текущее название предмета
     * @param newName новое название предмета
     * @throws EntityNotFoundException если предмет с originalName не найден
     * @throws EntityExistsException если предмет с newName уже существует
     */
    void updateSubject(String originalName, String newName);

    /**
     * Находит учебный предмет по его идентификатору.
     *
     * @param id идентификатор предмета
     * @return найденный объект Subject
     * @throws EntityNotFoundException если предмет с указанным id не найден
     */
    Subject findById(Long id);

    /**
     * Получает список предметов, которые учитель преподает в указанном классе.
     *
     * @param teacherId идентификатор учителя
     * @param classId идентификатор класса
     * @return список предметов
     * @throws IllegalStateException если учитель не преподает предметы в указанном классе
     */
    List<Subject> getSubjectsForClass(Long teacherId, Long classId);
}