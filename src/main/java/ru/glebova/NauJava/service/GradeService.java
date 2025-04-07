package ru.glebova.NauJava.service;

import ru.glebova.NauJava.adapter.controller.dto.GradeDTO;
import ru.glebova.NauJava.domain.Grade;

import java.util.List;

/**
 * Сервис для работы с оценками учащихся.
 * Предоставляет методы для выполнения операций с оценками в системе.
 */
public interface GradeService {

    /**
     * Получает список оценок конкретного ученика по определенному предмету.
     *
     * @param pupilId уникальный идентификатор ученика
     * @param subjectId уникальный идентификатор предмета
     * @return список объектов {@link GradeDTO}, содержащих оценки ученика по указанному предмету.
     *         Возвращает пустой список, если оценки не найдены.
     * @throws jakarta.persistence.EntityNotFoundException если ученик или предмет не найдены в системе
     */
    List<GradeDTO> getGradesPupilAndSubject(Long pupilId, Long subjectId);

    /**
     * Получает список оценок конкретного ученика по определенному предмету.
     *
     * @param pupilId уникальный идентификатор ученика
     * @return список объектов {@link Grade}, содержащих оценки ученика по указанному предмету.
     *         Возвращает пустой список, если оценки не найдены.
     * @throws jakarta.persistence.EntityNotFoundException если ученик или предмет не найдены в системе
     */

    List<Grade> getGradesPupil(Long pupilId);
}
