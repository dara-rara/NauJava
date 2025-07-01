package ru.glebova.NauJava.service;

import jakarta.persistence.EntityNotFoundException;
import ru.glebova.NauJava.domain.Classes;
import ru.glebova.NauJava.domain.Lesson;
import ru.glebova.NauJava.domain.Schedule;
import ru.glebova.NauJava.domain.Week;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис для работы с расписанием занятий.
 * Предоставляет методы для управления расписанием классов, уроками и их организацией по дням недели.
 */
public interface ScheduleService {
    /**
     * Находит расписание для указанного класса.
     *
     * @param cls класс, для которого нужно найти расписание
     * @return найденное расписание
     * @throws EntityNotFoundException если расписание для указанного класса не найдено
     */
    Schedule findByClass(Classes cls);

    /**
     * Сохраняет или обновляет расписание для указанного класса.
     * Если расписание для класса не существует, создает новое.
     *
     * @param cls класс, для которого нужно сохранить расписание
     * @return сохраненное расписание
     */
    Schedule save(Classes cls);

    /**
     * Удаляет урок из расписания.
     *
     * @param lessonId идентификатор урока для удаления
     * @return идентификатор класса, к которому принадлежал удаленный урок
     * @throws EntityNotFoundException если урок с указанным идентификатором не найден
     */
    Long removeLesson(Long lessonId);

    /**
     * Находит расписание по идентификатору.
     *
     * @param id идентификатор расписания
     * @return найденное расписание
     * @throws EntityNotFoundException если расписание с указанным идентификатором не найдено
     */
    Schedule findById(Long id);

    /**
     * Создает новый урок в расписании.
     *
     * @param scheduleId идентификатор расписания
     * @param weekDay день недели для урока
     * @param lessonNumber номер урока в расписании
     * @param subjectId идентификатор предмета
     * @param teacherId идентификатор преподавателя
     * @param timeStart время начала урока
     * @param timeEnd время окончания урока
     * @return обновленное расписание с добавленным уроком
     * @throws EntityNotFoundException если расписание, предмет или преподаватель не найдены
     */
    Schedule createLesson(Long scheduleId, Week weekDay, int lessonNumber, Long subjectId, Long teacherId,
                          LocalTime timeStart, LocalTime timeEnd);

    /**
     * Получает карту уроков расписания, сгруппированных по дням недели.
     *
     * @param schedule расписание, для которого нужно получить уроки
     * @return карта, где ключ - день недели, значение - список уроков в этот день
     */
    Map<Week, List<Lesson>> getLesson(Schedule schedule);
}