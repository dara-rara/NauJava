package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.controller.dto.GradeSetupDTO;
import ru.glebova.NauJava.adapter.controller.dto.LessonDTO;
import ru.glebova.NauJava.adapter.controller.dto.LessonDateDTO;
import ru.glebova.NauJava.adapter.repository.PeriodLessonRepository;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.service.HomeworkService;
import ru.glebova.NauJava.service.PeriodLessonService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PeriodLessonServiceImpl implements PeriodLessonService {

    private final PeriodLessonRepository periodLessonRepository;
    private final HomeworkService homeworkService;

    @Override
    @Transactional
    public PeriodLesson findByLessonId(Long id) {
        log.debug("Searching for lesson by ID: {}", id);
        return periodLessonRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Lesson not found with ID: {}", id);
                    return new EntityNotFoundException("Lesson not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodLesson> findByTeacherAndDateBetween(Teacher teacher, LocalDate startDate, LocalDate endDate) {
        log.info("Finding lessons for teacher {} between {} and {}",
                teacher.getId(), startDate, endDate);
        List<PeriodLesson> lessons = periodLessonRepository.findByTeacherAndDateBetweenWithFetch(teacher, startDate, endDate);
        log.debug("Found {} lessons for teacher {}", lessons.size(), teacher.getId());
        return lessons;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDate, List<LessonDTO>> findByPupilAndDateBetween(Pupil pupil, LocalDate startOfWeek, LocalDate endOfWeek,
                                                                     Map<Long, Grade> grades) {
        log.info("Building schedule for pupil {} from {} to {}",
                pupil.getId(), startOfWeek, endOfWeek);

        List<PeriodLesson> lessons = periodLessonRepository.findByClassAndDateBetween(
                pupil.getClasses(), startOfWeek, endOfWeek);
        log.debug("Found {} lessons for pupil {}", lessons.size(), pupil.getId());

        Map<Long, String> homeworks = homeworkService.getHomeworksForLessons(lessons)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() != null ? e.getValue().getDescription() : null
                ));
        log.trace("Retrieved {} homework assignments", homeworks.size());

        Map<LocalDate, List<LessonDTO>> schedule = lessons.stream()
                .map(lesson -> {
                    Integer gradeValue = null;
                    if (grades.containsKey(lesson.getId())) {
                        Grade grade = grades.get(lesson.getId());
                        gradeValue = grade.getGrade();
                        log.trace("Found grade {} for lesson {}", gradeValue, lesson.getId());
                    }

                    return new LessonDTO(
                            lesson.getId(),
                            lesson.getDate(),
                            lesson.getTimeStart(),
                            lesson.getTimeEnd(),
                            lesson.getLessonNumber(),
                            lesson.getSubject() != null ? lesson.getSubject().getName() : "Не указано",
                            lesson.getTeacher() != null ?
                                    lesson.getTeacher().getUsers().getLastName() + " " +
                                            lesson.getTeacher().getUsers().getFirstName() :
                                    "Преподаватель не назначен",
                            gradeValue,
                            homeworks.get(lesson.getId())
                    );
                })
                .collect(Collectors.groupingBy(LessonDTO::getDate));

        log.info("Successfully built schedule with {} days for pupil {}", schedule.size(), pupil.getId());
        return schedule;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonDateDTO> findByDateForTeacher(GradeSetupDTO request, TermPeriod termPeriod) {
        log.info("Finding lesson dates for teacher {}, class {}, subject {}",
                request.getTeacherId(), request.getClassId(), request.getSubjectId());

        List<LocalDate> dates = periodLessonRepository.findDistinctDatesByTeacherClassSubjectAndPeriod(
                request.getTeacherId(),
                request.getClassId(),
                request.getSubjectId(),
                termPeriod.getStartDate(),
                termPeriod.getEndDate());
        log.debug("Found {} unique dates for teacher", dates.size());

        List<LessonDateDTO> result = dates.stream()
                .map(date -> {
                    List<PeriodLesson> lessons = periodLessonRepository.findLessonsByTeacherClassSubjectAndDate(
                            request.getTeacherId(),
                            request.getClassId(),
                            request.getSubjectId(),
                            date);
                    log.trace("Found {} lessons for date {}", lessons.size(), date);
                    return new LessonDateDTO(date, lessons);
                })
                .toList();

        log.info("Successfully prepared {} lesson date entries", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> findAvailableSubjects(Long teacherId, Long classId) {
        log.info("Finding available subjects for teacher {} and class {}", teacherId, classId);
        List<Subject> subjects = periodLessonRepository.findSubjectsByTeacherAndClass(teacherId, classId);
        log.debug("Found {} available subjects", subjects.size());
        return subjects;
    }
}