package ru.glebova.NauJava.service;

import ru.glebova.NauJava.domain.Pupil;

/**
 * Интерфейс для управления данными об учениках.
 * Предоставляет методы для создания, поиска, обновления и удаления учеников.
 */
public interface PupilService {

    /**
     * Создает нового ученика с указанными данными.
     *
     * @param id        уникальный идентификатор ученика
     * @param firstname имя ученика
     * @param lastname  фамилия ученика
     * @throws IllegalArgumentException если id уже существует
     */
    void createPupil(Long id, String firstname, String lastname);

    /**
     * Находит ученика по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор ученика
     * @return объект ученика, если найден, иначе {@code null}
     */
    Pupil findById(Long id);

    /**
     * Удаляет ученика по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор ученика
     * @throws IllegalArgumentException если ученик с указанным id не найден
     */
    void deleteById(Long id);

    /**
     * Обновляет профиль ученика (имя и фамилию) по его уникальному идентификатору.
     *
     * @param id           уникальный идентификатор ученика
     * @param newFirstname новое имя ученика
     * @param newLastname  новая фамилия ученика
     * @throws IllegalArgumentException если ученик с указанным id не найден
     */
    void updateProfile(Long id, String newFirstname, String newLastname);
}