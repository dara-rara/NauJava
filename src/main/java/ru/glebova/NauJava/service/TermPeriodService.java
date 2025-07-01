package ru.glebova.NauJava.service;

import jakarta.persistence.EntityNotFoundException;
import ru.glebova.NauJava.domain.TermPeriod;

import java.util.List;

/**
 * Сервис для работы с учебными периодами (семестрами, четвертями и т.д.).
 * Предоставляет методы для создания, обновления и получения информации об учебных периодах.
 */
public interface TermPeriodService {

    /**
     * Создает новый учебный период.
     *
     * @param dto объект TermPeriod с данными для создания периода
     * @return созданный объект TermPeriod
     */
    TermPeriod createTermPeriod(TermPeriod dto);

    /**
     * Обновляет существующий учебный период.
     *
     * @param dto объект TermPeriod с обновленными данными периода
     * @return обновленный объект TermPeriod
     * @throws EntityNotFoundException если период с указанным ID не найден
     */
    TermPeriod updateTermPeriod(TermPeriod dto);

    /**
     * Получает учебный период по его идентификатору.
     *
     * @param id идентификатор учебного периода
     * @return найденный объект TermPeriod
     * @throws EntityNotFoundException если период с указанным ID не найден
     */
    TermPeriod getById(Long id);

    /**
     * Получает список всех учебных периодов.
     *
     * @return список всех учебных периодов
     */
    List<TermPeriod> getAllPeriods();
}