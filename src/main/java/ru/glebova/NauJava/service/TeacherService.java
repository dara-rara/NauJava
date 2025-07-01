package ru.glebova.NauJava.service;

import jakarta.persistence.EntityNotFoundException;
import ru.glebova.NauJava.domain.Teacher;

import java.util.List;

/**
 * Сервис для работы с учителями.
 * Предоставляет методы для создания, поиска, обновления и удаления учителей,
 * а также управления их связями с предметами и классами.
 */
public interface TeacherService {

    /**
     * Получает список всех учителей.
     *
     * @return список всех учителей
     */
    List<Teacher> findAll();

    /**
     * Находит учителя по его идентификатору.
     *
     * @param id идентификатор учителя
     * @return найденный объект Teacher
     * @throws EntityNotFoundException если учитель с указанным id не найден
     */
    Teacher findById(Long id);

    /**
     * Создает нового учителя и связывает его с пользователем, предметами и классами.
     *
     * @param teacher объект Teacher с данными учителя
     * @param userId идентификатор пользователя, который будет связан с учителем
     * @param subjectIds список идентификаторов предметов, которые преподает учитель
     * @param classIds список идентификаторов классов, в которых преподает учитель
     * @return созданный объект Teacher
     * @throws EntityNotFoundException если пользователь с userId не найден
     * @throws IllegalArgumentException если пользователь не имеет роли TEACHER
     *         или уже является учителем
     */
    Teacher createTeacher(Teacher teacher, Long userId, List<Long> subjectIds, List<Long> classIds);

    /**
     * Обновляет данные учителя и его связи с предметами и классами.
     *
     * @param id идентификатор обновляемого учителя
     * @param teacherDetails объект Teacher с обновленными данными
     * @param subjectIds новый список идентификаторов предметов (null - не изменять)
     * @param classIds новый список идентификаторов классов (null - не изменять)
     * @return обновленный объект Teacher
     * @throws IllegalArgumentException если учитель с указанным id не найден
     */
    Teacher updateTeacher(Long id, Teacher teacherDetails, List<Long> subjectIds, List<Long> classIds);

    /**
     * Удаляет учителя по его идентификатору.
     *
     * @param id идентификатор удаляемого учителя
     * @throws IllegalArgumentException если учитель с указанным id не найден
     */
    void deleteTeacher(Long id);

    /**
     * Проверяет, существует ли учитель, связанный с указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return true если учитель существует, false в противном случае
     */
    boolean existsByUserId(Long userId);

    /**
     * Находит учителя по имени пользователя.
     *
     * @param username имя пользователя, связанного с учителем
     * @return найденный объект Teacher
     * @throws EntityNotFoundException если пользователь с указанным именем не найден
     *         или не связан с учителем
     */
    Teacher findByTeacher(String username);
}
