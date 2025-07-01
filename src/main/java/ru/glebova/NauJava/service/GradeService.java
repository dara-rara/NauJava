package ru.glebova.NauJava.service;

import jakarta.persistence.EntityNotFoundException;
import ru.glebova.NauJava.adapter.controller.dto.*;
import ru.glebova.NauJava.domain.Grade;
import ru.glebova.NauJava.domain.PeriodLesson;
import ru.glebova.NauJava.domain.Pupil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с оценками учащихся.
 * Предоставляет функционал для формирования таблицы оценок,
 * пакетного сохранения оценок и получения оценок за период.
 */
public interface GradeService {

    /**
     * Формирует таблицу оценок для указанного класса, предмета и учебного периода.
     *
     * @param request DTO с параметрами запроса:
     *                - termPeriodId идентификатор учебного периода
     *                - subjectId идентификатор предмета
     *                - classId идентификатор класса
     *                - teacherId идентификатор учителя
     * @return {@link GradeTableDTO} содержащий:
     *         - информацию о классе, учителе и периоде
     *         - выбранный предмет
     *         - даты уроков
     *         - список учеников с их оценками
     *         - доступные для выбора предметы
     * @throws EntityNotFoundException если не найден период, предмет, класс или учитель
     */
    GradeTableDTO prepareGradeTableForTeacher(GradeSetupDTO request);

    /**
     * Сохраняет или обновляет оценки пакетно.
     *
     * @param request DTO с данными для сохранения:
     *                - termPeriodId идентификатор учебного периода
     *                - subjectId идентификатор предмета
     *                - classId идентификатор класса
     *                - grades карта оценок (ключ - ID ученика, значение - карта (ID урока - оценка))
     * @throws EntityNotFoundException если не найден период, предмет, класс или ученик
     */
    void saveGradesBatch(GradeSaveRequest request);

    /**
     * Возвращает оценки ученика за указанный период.
     *
     * @param pupil ученик
     * @param startDate начальная дата периода
     * @param endDate конечная дата периода
     * @return карта оценок (ключ - ID урока, значение - сущность {@link Grade})
     */
    Map<Long, Grade> getGradesForPupilAndWeek(Pupil pupil, LocalDate startDate, LocalDate endDate);

    /**
     * Получает обзор оценок ученика за указанный учебный период.
     *
     * @param request DTO-объект с параметрами запроса, содержащий:
     *                - ученика (Pupil)
     *                - идентификатор учебного периода (termPeriodId)
     * @return DTO-объект {@link PupilGradesOverviewDTO}, содержащий:
     *         - данные ученика
     *         - информацию об учебном периоде
     *         - список предметов с оценками по датам
     *         - средние баллы по каждому предмету
     *         - список рабочих дат периода (без выходных)
     * @throws EntityNotFoundException если не найдено расписание для указанного класса и периода
     * @throws IllegalArgumentException если передан некорректный запрос
     * @see PupilGradesOverviewDTO
     * @see GradeSetupPupilDTO
     */
    PupilGradesOverviewDTO getPupilGradesOverview(GradeSetupPupilDTO request);
}