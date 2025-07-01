package ru.glebova.NauJava.service;

import jakarta.persistence.EntityNotFoundException;
import ru.glebova.NauJava.adapter.controller.dto.GradeSetupDTO;
import ru.glebova.NauJava.adapter.controller.dto.LessonDTO;
import ru.glebova.NauJava.adapter.controller.dto.LessonDateDTO;
import ru.glebova.NauJava.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с расписанием уроков (PeriodLesson).
 * Предоставляет методы для поиска уроков по различным критериям.
 */
public interface PeriodLessonService {

    /**
     * Находит урок по его идентификатору.
     *
     * @param id уникальный идентификатор урока
     * @return найденный урок {@link PeriodLesson}
     * @throws EntityNotFoundException если урок с указанным ID не найден
     */
    PeriodLesson findByLessonId(Long id);

    /**
     * Возвращает расписание уроков для ученика за указанный период с оценками.
     *
     * @param pupil ученик, для которого запрашивается расписание
     * @param startOfWeek начальная дата периода
     * @param endOfWeek конечная дата периода
     * @param grades карта оценок (ключ - ID урока, значение - оценка {@link Grade})
     * @return карта уроков, сгруппированных по дате (ключ - дата, значение - список {@link LessonDTO}):
     *         - ID урока
     *         - дата и время урока
     *         - номер урока
     *         - название предмета
     *         - ФИО преподавателя
     *         - оценка (если есть)
     *         - домашнее задание (если есть)
     */
    Map<LocalDate, List<LessonDTO>> findByPupilAndDateBetween(Pupil pupil, LocalDate startOfWeek, LocalDate endOfWeek,
                                                              Map<Long, Grade> grades);

    /**
     * Возвращает даты уроков с детализацией для учителя.
     *
     * @param request DTO с параметрами запроса:
     *                - teacherId идентификатор учителя
     *                - classId идентификатор класса
     *                - subjectId идентификатор предмета
     * @param termPeriod учебный период
     * @return список {@link LessonDateDTO}, содержащий:
     *         - дату урока
     *         - список уроков на эту дату
     */
    List<LessonDateDTO> findByDateForTeacher(GradeSetupDTO request, TermPeriod termPeriod);

    /**
     * Находит уроки для учителя за указанный период.
     *
     * @param teacher учитель
     * @param startDate начальная дата периода
     * @param endDate конечная дата периода
     * @return список уроков {@link PeriodLesson}
     */
    List<PeriodLesson> findByTeacherAndDateBetween(Teacher teacher, LocalDate startDate, LocalDate endDate);

    /**
     * Возвращает список предметов, которые учитель ведет в указанном классе.
     *
     * @param teacherId идентификатор учителя
     * @param classId идентификатор класса
     * @return список доступных предметов {@link Subject}
     */
    List<Subject> findAvailableSubjects(Long teacherId, Long classId);
}