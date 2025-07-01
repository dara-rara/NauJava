package ru.glebova.NauJava.service;

import jakarta.persistence.EntityNotFoundException;
import ru.glebova.NauJava.domain.Homework;
import ru.glebova.NauJava.domain.PeriodLesson;
import ru.glebova.NauJava.domain.Teacher;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с домашними заданиями (Homework).
 * Предоставляет методы для создания, редактирования и поиска домашних заданий.
 */
public interface HomeworkService {

    /**
     * Находит домашнее задание по его идентификатору.
     *
     * @param id уникальный идентификатор домашнего задания
     * @return найденное домашнее задание {@link Homework}
     * @throws EntityNotFoundException если домашнее задание с указанным ID не найдено
     */
    Homework findByHomeworkId(Long id);

    /**
     * Создает новое домашнее задание для указанного урока.
     *
     * @param lesson урок, для которого создается домашнее задание
     * @param description текст домашнего задания
     * @return сохраненное домашнее задание {@link Homework}
     */
    Homework saveHomework(PeriodLesson lesson, String description);

    /**
     * Редактирует текст существующего домашнего задания.
     *
     * @param id идентификатор редактируемого домашнего задания
     * @param description новый текст домашнего задания
     * @return обновленное домашнее задание {@link Homework}
     * @throws EntityNotFoundException если домашнее задание с указанным ID не найдено
     */
    Homework editHomework(Long id, String description);

    /**
     * Возвращает домашние задания для списка уроков.
     *
     * @param lessons список уроков для поиска домашних заданий
     * @return карта домашних заданий (ключ - ID урока, значение - {@link Homework})
     */
    Map<Long, Homework> getHomeworksForLessons(List<PeriodLesson> lessons);
}