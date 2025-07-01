package ru.glebova.NauJava.mock;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import ru.glebova.NauJava.adapter.repository.PeriodLessonRepository;
import ru.glebova.NauJava.adapter.repository.PeriodScheduleRepository;
import ru.glebova.NauJava.adapter.repository.TermPeriodRepository;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.service.ClassesService;
import ru.glebova.NauJava.service.ScheduleService;
import ru.glebova.NauJava.service.impl.PeriodScheduleServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeriodScheduleServiceTests {

    @Mock
    private PeriodScheduleRepository periodScheduleRepository;

    @Mock
    private PeriodLessonRepository periodLessonRepository;

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private TermPeriodRepository termPeriodRepository;

    @Mock
    private ClassesService classesService;

    @Mock
    private Executor taskExecutor;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PeriodScheduleServiceImpl periodScheduleService;

    @Test
    void generateForClassExistingScheduleTest() throws Exception {
        Long classId = 1L;
        Long periodId = 1L;
        boolean overwrite = true;

        Classes schoolClass = new Classes();
        schoolClass.setId(classId);

        TermPeriod period = new TermPeriod();
        period.setId(periodId);
        period.setStartDate(LocalDate.of(2023, 9, 1));
        period.setEndDate(LocalDate.of(2023, 12, 31));

        Schedule template = new Schedule();
        template.setLessons(List.of(createTestLesson(1, Week.MONDAY)));

        PeriodSchedule periodSchedule = new PeriodSchedule();
        periodSchedule.setId(1L);
        periodSchedule.setClasses(schoolClass);
        periodSchedule.setPeriod(period);

        when(classesService.findById(classId)).thenReturn(schoolClass);
        when(termPeriodRepository.findById(periodId)).thenReturn(Optional.of(period));
        when(scheduleService.findByClass(schoolClass)).thenReturn(template);
        when(transactionManager.getTransaction(any())).thenReturn(mock());
        when(periodScheduleRepository.save(any(PeriodSchedule.class))).thenReturn(periodSchedule);
        when(transactionManager.getTransaction(any())).thenReturn(mock(TransactionStatus.class));

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        CompletableFuture<Void> future = periodScheduleService.generateForClass(classId, periodId, overwrite);
        future.get();

        InOrder inOrder = inOrder(periodScheduleRepository, entityManager, periodLessonRepository);

        inOrder.verify(periodScheduleRepository).deleteByClassesAndPeriod(schoolClass, period);
        inOrder.verify(entityManager).flush();
        inOrder.verify(periodScheduleRepository).save(any(PeriodSchedule.class));
        inOrder.verify(periodLessonRepository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void generateForClassPeriodNotFoundTest() {
        Long classId = 1L;
        Long periodId = 1L;
        boolean overwrite = false;

        when(classesService.findById(classId)).thenReturn(new Classes());
        when(termPeriodRepository.findById(periodId)).thenReturn(Optional.empty());

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        CompletableFuture<Void> future = periodScheduleService.generateForClass(classId, periodId, overwrite);
        assertThrows(Exception.class, future::get);
    }

    @Test
    void generateLessonsForPeriodCorrectLessonsTest() {
        PeriodSchedule periodSchedule = new PeriodSchedule();
        TermPeriod period = new TermPeriod();
        period.setStartDate(LocalDate.of(2023, 9, 4));
        period.setEndDate(LocalDate.of(2023, 9, 6));

        Schedule template = new Schedule();
        Lesson mondayLesson = createTestLesson(1, Week.MONDAY);
        Lesson tuesdayLesson = createTestLesson(2, Week.TUESDAY);
        Lesson wednesdayLesson = createTestLesson(3, Week.WEDNESDAY);
        template.setLessons(List.of(mondayLesson, tuesdayLesson, wednesdayLesson));

        List<PeriodLesson> lessons = periodScheduleService.generateLessonsForPeriod(periodSchedule, template, period);
        assertEquals(3, lessons.size());

        PeriodLesson mondayPeriodLesson = lessons.get(0);
        assertEquals(LocalDate.of(2023, 9, 4), mondayPeriodLesson.getDate());
        assertEquals(Week.MONDAY, mondayPeriodLesson.getWeekDay());
        assertEquals(1, mondayPeriodLesson.getLessonNumber());

        PeriodLesson tuesdayPeriodLesson = lessons.get(1);
        assertEquals(LocalDate.of(2023, 9, 5), tuesdayPeriodLesson.getDate());
        assertEquals(Week.TUESDAY, tuesdayPeriodLesson.getWeekDay());
        assertEquals(2, tuesdayPeriodLesson.getLessonNumber());

        PeriodLesson wednesdayPeriodLesson = lessons.get(2);
        assertEquals(LocalDate.of(2023, 9, 6), wednesdayPeriodLesson.getDate());
        assertEquals(Week.WEDNESDAY, wednesdayPeriodLesson.getWeekDay());
        assertEquals(3, wednesdayPeriodLesson.getLessonNumber());
    }

    @Test
    void generateLessonsForPeriodSkipDaysWithoutLessonsTest() {
        PeriodSchedule periodSchedule = new PeriodSchedule();
        TermPeriod period = new TermPeriod();
        period.setStartDate(LocalDate.of(2023, 9, 4));
        period.setEndDate(LocalDate.of(2023, 9, 6));

        Schedule template = new Schedule();
        Lesson tuesdayLesson = createTestLesson(1, Week.TUESDAY);
        template.setLessons(List.of(tuesdayLesson));

        List<PeriodLesson> lessons = periodScheduleService.generateLessonsForPeriod(periodSchedule, template, period);

        assertEquals(1, lessons.size());
        assertEquals(LocalDate.of(2023, 9, 5), lessons.get(0).getDate());
        assertEquals(Week.TUESDAY, lessons.get(0).getWeekDay());
    }

    private Lesson createTestLesson(int lessonNumber, Week weekDay) {
        Lesson lesson = new Lesson();
        lesson.setLessonNumber(lessonNumber);
        lesson.setWeek(weekDay);
        lesson.setTimeStart(LocalTime.of(9, 0));
        lesson.setTimeEnd(LocalTime.of(9, 45));

        Classes schoolClass = new Classes();
        schoolClass.setId(1L);
        lesson.setClasses(schoolClass);

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        lesson.setTeacher(teacher);

        Subject subject = new Subject();
        subject.setId(1L);
        subject.setName("Math");
        lesson.setSubject(subject);

        return lesson;
    }
}