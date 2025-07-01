package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import ru.glebova.NauJava.adapter.repository.PeriodLessonRepository;
import ru.glebova.NauJava.adapter.repository.PeriodScheduleRepository;
import ru.glebova.NauJava.adapter.repository.TermPeriodRepository;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.service.ClassesService;
import ru.glebova.NauJava.service.PeriodScheduleService;
import ru.glebova.NauJava.service.ScheduleService;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PeriodScheduleServiceImpl implements PeriodScheduleService {
    private final PeriodScheduleRepository periodScheduleRepository;
    private final PeriodLessonRepository periodLessonRepository;
    private final ScheduleService scheduleService;
    private final TermPeriodRepository termPeriodRepository;
    private final ClassesService classesService;
    private final Executor taskExecutor;
    private final PlatformTransactionManager transactionManager;
    private final EntityManager entityManager;

    @Override
    @Async
    public CompletableFuture<Void> generateForClass(Long classId, Long periodId, boolean overwrite) {
        log.info("Starting async schedule generation for class {} and period {} (overwrite: {})",
                classId, periodId, overwrite);

        return CompletableFuture.runAsync(() -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            transactionTemplate.executeWithoutResult(status -> {
                try {
                    log.debug("Fetching class and period data");
                    Classes schoolClass = classesService.findById(classId);
                    TermPeriod period = termPeriodRepository.findById(periodId)
                            .orElseThrow(() -> {
                                log.error("Period not found with ID: {}", periodId);
                                return new EntityNotFoundException("Period not found");
                            });

                    log.debug("Fetching template schedule for class {}", classId);
                    Schedule template = scheduleService.findByClass(schoolClass);

                    if (!overwrite && periodScheduleRepository.existsByClassesAndPeriod(schoolClass, period)) {
                        log.warn("Schedule already exists for class {} and period {} (overwrite=false)",
                                classId, periodId);
                        throw new IllegalStateException("Schedule already exists for this period");
                    }

                    if (overwrite) {
                        log.debug("Deleting existing schedule (overwrite=true)");
                        periodScheduleRepository.deleteByClassesAndPeriod(schoolClass, period);
                        entityManager.flush();
                    }

                    log.debug("Creating new period schedule");
                    PeriodSchedule periodSchedule = new PeriodSchedule();
                    periodSchedule.setClasses(schoolClass);
                    periodSchedule.setPeriod(period);
                    periodSchedule.setTemplateSchedule(template);

                    PeriodSchedule savedSchedule = periodScheduleRepository.save(periodSchedule);
                    entityManager.flush();
                    log.info("Created new period schedule with ID: {}", savedSchedule.getId());

                    log.debug("Generating lessons for period");
                    List<PeriodLesson> lessons = generateLessonsForPeriod(savedSchedule, template, period);
                    log.info("Generated {} lessons for period", lessons.size());

                    int totalLessons = lessons.size();
                    log.debug("Saving lessons in batches of 50");
                    for (int i = 0; i < totalLessons; i += 50) {
                        List<PeriodLesson> batch = lessons.subList(i, Math.min(i + 50, totalLessons));
                        periodLessonRepository.saveAll(batch);
                        entityManager.flush();
                        entityManager.clear();

                        if (i % 500 == 0) {
                            log.debug("Saved {} of {} lessons", Math.min(i + 50, totalLessons), totalLessons);
                        }
                    }

                    log.info("Successfully generated schedule for class {} and period {}", classId, periodId);
                } catch (Exception e) {
                    log.error("Failed to generate schedule for class {} and period {}", classId, periodId, e);
                    status.setRollbackOnly();
                    throw new RuntimeException("Failed to generate schedule", e);
                }
            });
        }, taskExecutor);
    }

    @Override
    public List<PeriodLesson> generateLessonsForPeriod(PeriodSchedule periodSchedule, Schedule template, TermPeriod period) {
        log.debug("Generating lessons for period {} to {}",
                period.getStartDate(), period.getEndDate());

        return period.getStartDate().datesUntil(period.getEndDate().plusDays(1))
                .flatMap(date -> {
                    Week weekDay = Week.fromDayOfWeek(date.getDayOfWeek());
                    if (weekDay == null) {
                        log.trace("Skipping date {} - not a weekday", date);
                        return Stream.empty();
                    }

                    return template.getLessons().stream()
                            .filter(lesson -> lesson.getWeek() == weekDay)
                            .sorted(Comparator.comparingInt(Lesson::getLessonNumber))
                            .map(lesson -> {
                                log.trace("Creating lesson for date {}, subject {}",
                                        date, lesson.getSubject().getName());

                                PeriodLesson periodLesson = new PeriodLesson();
                                periodLesson.setPeriodSchedule(periodSchedule);
                                periodLesson.setDate(date);
                                periodLesson.setWeekDay(weekDay);
                                periodLesson.setClasses(lesson.getClasses());
                                periodLesson.setTeacher(lesson.getTeacher());
                                periodLesson.setLessonNumber(lesson.getLessonNumber());
                                periodLesson.setTimeStart(lesson.getTimeStart());
                                periodLesson.setTimeEnd(lesson.getTimeEnd());
                                periodLesson.setSubject(lesson.getSubject());
                                return periodLesson;
                            });
                })
                .collect(Collectors.toList());
    }
}