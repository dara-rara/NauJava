package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.repository.LessonRepository;
import ru.glebova.NauJava.adapter.repository.ScheduleRepository;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.service.ScheduleService;
import ru.glebova.NauJava.service.SubjectService;
import ru.glebova.NauJava.service.TeacherService;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScheduleServiceImpl implements ScheduleService {
    private final LessonRepository lessonRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubjectService subjectService;
    private final TeacherService teacherService;

    @Override
    @Transactional(readOnly = true)
    public Schedule findByClass(Classes cls) {
        log.debug("Finding schedule for class: {}", cls.getName());
        return scheduleRepository.findByClasses(cls)
                .orElseThrow(() -> {
                    log.error("Schedule not found for class: {}", cls.getName());
                    return new EntityNotFoundException("Schedule not found");
                });
    }

    @Override
    @Transactional
    public Schedule save(Classes cls) {
        log.info("Saving schedule for class: {}", cls.getName());

        Schedule schedule = scheduleRepository.findByClasses(cls).orElseGet(() -> {
            log.debug("Creating new schedule for class: {}", cls.getName());
            Schedule newSchedule = new Schedule();
            newSchedule.setClasses(cls);
            return scheduleRepository.save(newSchedule);
        });

        Hibernate.initialize(schedule.getLessons());
        log.debug("Initialized lessons for schedule");

        if (schedule.getLessons() != null) {
            log.trace("Initializing subjects for {} lessons", schedule.getLessons().size());
            schedule.getLessons().forEach(lesson -> {
                Hibernate.initialize(lesson.getSubject());
            });
        }

        if (schedule.getLessons() != null) {
            log.trace("Initializing teachers for {} lessons", schedule.getLessons().size());
            schedule.getLessons().forEach(lesson -> {
                Hibernate.initialize(lesson.getTeacher());
            });
        }

        log.info("Successfully saved schedule for class: {}", cls.getName());
        return schedule;
    }

    @Override
    @Transactional
    public Schedule createLesson(Long scheduleId, Week weekDay, int lessonNumber, Long subjectId, Long teacherId, LocalTime timeStart, LocalTime timeEnd) {
        log.info("Creating new lesson for schedule: {}, day: {}, number: {}",
                scheduleId, weekDay, lessonNumber);

        Schedule schedule = findById(scheduleId);
        Teacher teacher = teacherService.findById(teacherId);
        Subject subject = subjectService.findById(subjectId);
        Classes classes = schedule.getClasses();

        if (!teacher.getClasses().contains(classes)) {
            log.error("Teacher {} doesn't teach classes {}", classes.getId(), classes.getId());
            throw new IllegalArgumentException("Teacher has not this classes");
        }

        if (!teacher.getSubjects().contains(subject)) {
            log.error("Teacher {} doesn't teach subject {}", teacher.getId(), subject.getId());
            throw new IllegalArgumentException("Teacher has not this subject");
        }

        Lesson lesson = new Lesson();
        lesson.setWeek(weekDay);
        lesson.setLessonNumber(lessonNumber);
        lesson.setClasses(schedule.getClasses());
        lesson.setSubject(subject);
        lesson.setTeacher(teacher);
        lesson.setTimeStart(timeStart);
        lesson.setTimeEnd(timeEnd);

        schedule.addLesson(lesson);
        Schedule savedSchedule = scheduleRepository.save(schedule);

        log.info("Successfully created lesson with ID: {} in schedule: {}",
                lesson.getId(), scheduleId);
        return savedSchedule;
    }

    @Override
    @Transactional
    public Long removeLesson(Long lessonId) {
        log.info("Removing lesson with ID: {}", lessonId);

        return lessonRepository.findById(lessonId).map(lesson -> {
            Schedule schedule = lesson.getSchedule();
            schedule.removeLesson(lesson);
            scheduleRepository.save(schedule);

            Long classId = schedule.getClasses().getId();
            log.info("Successfully removed lesson {} from class {}", lessonId, classId);
            return classId;
        }).orElseThrow(() -> {
            log.error("Lesson not found with ID: {}", lessonId);
            return new EntityNotFoundException("Lesson not found");
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Schedule findById(Long id) {
        log.debug("Finding schedule by ID: {}", id);
        return scheduleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Schedule not found with ID: {}", id);
                    return new EntityNotFoundException("Schedule not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Week, List<Lesson>> getLesson(Schedule schedule) {
        log.debug("Grouping lessons by week for schedule: {}", schedule.getId());
        Map<Week, List<Lesson>> lessonsByWeek = schedule.getLessons()
                .stream()
                .collect(Collectors.groupingBy(Lesson::getWeek));

        log.debug("Found lessons for {} weekdays", lessonsByWeek.size());
        return lessonsByWeek;
    }
}