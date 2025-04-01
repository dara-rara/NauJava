package ru.glebova.NauJava.service;

import ru.glebova.NauJava.adapter.controller.dto.ScheduleDTO;
import ru.glebova.NauJava.domain.Schedule;
import java.util.List;

/**
 * Сервис для работы с расписанием занятий.
 * Предоставляет методы для получения информации о расписании.
 */
public interface ScheduleService {

    /**
     * Получает список элементов расписания для указанного класса и предмета.
     *
     * @param className название класса (например, "10А"), не может быть {@code null} или пустым
     * @param subjectName название предмета (например, "Математика"), не может быть {@code null} или пустым
     * @return список объектов {@link ScheduleDTO}, соответствующих заданным критериям.
     *         Возвращает пустой список, если расписание не найдено.
     * @throws jakarta.persistence.EntityNotFoundException если класс или предмет не существуют
     * @throws IllegalArgumentException если className или subjectName {@code null} или пустые
     */
    List<ScheduleDTO> getScheduleClassAndSubject(String className, String subjectName);
}
