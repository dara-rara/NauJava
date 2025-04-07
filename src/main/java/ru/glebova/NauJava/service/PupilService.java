package ru.glebova.NauJava.service;

/**
 * Интерфейс для управления данными об учениках.
 * Предоставляет методы для создания, поиска, обновления и удаления учеников.
 */
public interface PupilService {

    /**
     * Удаляет ученика по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор ученика
     * @throws IllegalArgumentException если ученик с указанным id не найден
     */
    void deleteById(Long id);

}