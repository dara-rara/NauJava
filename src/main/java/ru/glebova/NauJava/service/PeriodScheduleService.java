package ru.glebova.NauJava.service;

import jakarta.persistence.EntityNotFoundException;
import ru.glebova.NauJava.domain.PeriodLesson;
import ru.glebova.NauJava.domain.PeriodSchedule;
import ru.glebova.NauJava.domain.Schedule;
import ru.glebova.NauJava.domain.TermPeriod;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис для генерации расписания уроков на учебный период.
 * Предоставляет асинхронные методы для создания расписания.
 */
public interface PeriodScheduleService {

    /**
     * Асинхронно генерирует расписание уроков для класса на указанный период.
     *
     * @param classId идентификатор класса
     * @param periodId идентификатор учебного периода
     * @param overwrite флаг перезаписи существующего расписания:
     *                  - true: удаляет существующее расписание и создает новое
     *                  - false: выбрасывает исключение, если расписание уже существует
     * @return CompletableFuture<Void>, завершающийся после генерации расписания
     * @throws EntityNotFoundException если класс или период не найдены
     * @throws IllegalStateException если расписание уже существует и overwrite=false
     * @throws RuntimeException при ошибках в процессе генерации
     */
    CompletableFuture<Void> generateForClass(Long classId, Long periodId, boolean overwrite);

    /**
     * Генерирует список уроков на основе шаблона расписания для указанного периода.
     *
     * @param periodSchedule сгенерированное расписание периода
     * @param template шаблонное расписание (обычное недельное расписание)
     * @param period учебный период (даты начала и окончания)
     * @return список сгенерированных уроков {@link PeriodLesson}:
     *         - для каждого дня периода
     *         - согласно шаблону для соответствующего дня недели
     *         - с сохранением всех атрибутов урока (время, предмет, преподаватель и т.д.)
     */
    List<PeriodLesson> generateLessonsForPeriod(PeriodSchedule periodSchedule, Schedule template, TermPeriod period);
}