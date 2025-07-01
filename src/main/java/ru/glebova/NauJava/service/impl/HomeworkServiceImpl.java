package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.repository.HomeworkRepository;
import ru.glebova.NauJava.domain.Homework;
import ru.glebova.NauJava.domain.PeriodLesson;
import ru.glebova.NauJava.service.HomeworkService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HomeworkServiceImpl implements HomeworkService {
    private final HomeworkRepository homeworkRepository;

    @Override
    public Homework findByHomeworkId(Long id) {
        log.debug("Attempting to find homework by ID: {}", id);
        return homeworkRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Homework not found with ID: {}", id);
                    return new EntityNotFoundException("Homework not found");
                });
    }

    @Override
    public Map<Long, Homework> getHomeworksForLessons(List<PeriodLesson> lessons) {
        log.info("Getting homeworks for {} lessons", lessons.size());
        List<Long> lessonIds = lessons.stream()
                .map(PeriodLesson::getId)
                .collect(Collectors.toList());
        log.debug("Lesson IDs: {}", lessonIds);

        Map<Long, Homework> homeworks = homeworkRepository.findByPeriodLessonIn(lessons)
                .stream()
                .collect(Collectors.toMap(
                        homework -> homework.getPeriodLesson().getId(),
                        Function.identity()
                ));

        log.debug("Found {} homeworks", homeworks.size());
        return homeworks;
    }

    @Override
    public Homework saveHomework(PeriodLesson lesson, String description) {
        log.info("Creating new homework for lesson ID: {}", lesson.getId());
        log.trace("Homework description: {}", description);

        Homework homework = new Homework();
        homework.setDescription(description);
        homework.setPeriodLesson(lesson);
        lesson.setHomework(homework);

        Homework savedHomework = homeworkRepository.save(homework);
        log.info("Successfully saved homework with ID: {}", savedHomework.getId());
        return savedHomework;
    }

    @Override
    public Homework editHomework(Long id, String description) {
        log.info("Editing homework with ID: {}", id);
        log.trace("New description: {}", description);

        Homework homework = findByIdWithPeriodLesson(id);
        log.debug("Found homework for lesson ID: {}", homework.getPeriodLesson().getId());

        homework.setDescription(description);
        Homework updatedHomework = homeworkRepository.save(homework);
        log.info("Successfully updated homework with ID: {}", id);
        return updatedHomework;
    }

    private Homework findByIdWithPeriodLesson(Long id) {
        log.debug("Looking for homework with ID and period lesson: {}", id);
        return homeworkRepository.findByIdWithPeriodLesson(id)
                .orElseThrow(() -> {
                    log.error("Homework not found with ID: {}", id);
                    return new EntityNotFoundException("Homework not found");
                });
    }
}