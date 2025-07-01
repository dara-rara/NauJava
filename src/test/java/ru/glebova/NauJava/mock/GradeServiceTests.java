package ru.glebova.NauJava.mock;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.glebova.NauJava.adapter.controller.dto.*;
import ru.glebova.NauJava.adapter.repository.GradeRepository;
import ru.glebova.NauJava.adapter.repository.PeriodScheduleRepository;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.service.*;
import ru.glebova.NauJava.service.impl.GradeServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradeServiceTests {

    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private PeriodScheduleRepository periodScheduleRepository;
    @Mock
    private PupilService pupilService;
    @Mock
    private TeacherService teacherService;
    @Mock
    private SubjectService subjectService;
    @Mock
    private ClassesService classesService;
    @Mock
    private TermPeriodService termPeriodService;
    @Mock
    private PeriodLessonService periodLessonService;

    @InjectMocks
    private GradeServiceImpl gradeService;

    private GradeSetupDTO gradeSetupDTO;
    private GradeSaveRequest gradeSaveRequest;
    private Pupil pupil;
    private TermPeriod termPeriod;
    private Subject subject;
    private Classes classes;
    private Teacher teacher;
    private PeriodLesson periodLesson;
    private Users teacherUser;
    private Users pupilUser;

    @BeforeEach
    void setUp() {
        teacherUser = new Users();
        teacherUser.setId(1L);
        teacherUser.setFirstName("Ivan");
        teacherUser.setLastName("Petrov");
        teacherUser.setRole(Role.TEACHER);

        pupilUser = new Users();
        pupilUser.setId(2L);
        pupilUser.setFirstName("Alex");
        pupilUser.setLastName("Ivanov");
        pupilUser.setRole(Role.PUPIL);

        termPeriod = new TermPeriod();
        termPeriod.setId(1L);
        termPeriod.setName("1 четверть 2023/2024");
        termPeriod.setStartDate(LocalDate.of(2023, 9, 1));
        termPeriod.setEndDate(LocalDate.of(2023, 12, 31));

        subject = new Subject();
        subject.setId(1L);
        subject.setName("Mathematics");

        classes = new Classes();
        classes.setId(1L);
        classes.setName("5A");

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setUsers(teacherUser);
        teacher.addSubject(subject);
        teacher.addClass(classes);

        pupil = new Pupil();
        pupil.setId(1L);
        pupil.setUsers(pupilUser);
        pupil.setClasses(classes);

        periodLesson = new PeriodLesson();
        periodLesson.setId(1L);
        periodLesson.setSubject(subject);
        periodLesson.setTeacher(teacher);
        periodLesson.setClasses(classes);
        periodLesson.setDate(LocalDate.of(2023, 9, 5));
        periodLesson.setWeekDay(Week.MONDAY);
        periodLesson.setLessonNumber(1);
        periodLesson.setTimeStart(LocalTime.of(9, 0));
        periodLesson.setTimeEnd(LocalTime.of(9, 45));

        gradeSetupDTO = new GradeSetupDTO();
        gradeSetupDTO.setTermPeriodId(1L);
        gradeSetupDTO.setSubjectId(1L);
        gradeSetupDTO.setClassId(1L);
        gradeSetupDTO.setTeacherId(1L);

        gradeSaveRequest = new GradeSaveRequest();
        gradeSaveRequest.setTermPeriodId(1L);
        gradeSaveRequest.setSubjectId(1L);
        gradeSaveRequest.setClassId(1L);
        gradeSaveRequest.setGrades(Map.of(
                1L, Map.of(1L, 5)
        ));
    }

    @Test
    void prepareGradeTableShouldReturnCorrectTest() {
        LessonDateDTO lessonDateDTO = new LessonDateDTO(
                LocalDate.of(2023, 9, 5),
                List.of(periodLesson)
        );

        when(termPeriodService.getById(1L)).thenReturn(termPeriod);
        when(subjectService.findById(1L)).thenReturn(subject);
        when(classesService.findById(1L)).thenReturn(classes);
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(pupilService.findByClass(classes)).thenReturn(List.of(pupil));
        when(gradeRepository.findByPupilAndSubjectAndPeriod(
                pupil, subject, termPeriod.getStartDate(), termPeriod.getEndDate()))
                .thenReturn(List.of());
        when(periodLessonService.findByDateForTeacher(gradeSetupDTO, termPeriod))
                .thenReturn(List.of(lessonDateDTO));
        when(periodLessonService.findAvailableSubjects(1L, 1L))
                .thenReturn(List.of(subject));

        GradeTableDTO result = gradeService.prepareGradeTableForTeacher(gradeSetupDTO);

        assertNotNull(result);
        assertEquals(classes, result.classInfo());
        assertEquals(teacher, result.teacher());
        assertEquals(termPeriod, result.termPeriod());
        assertEquals(subject, result.selectedSubject());

        assertEquals(1, result.lessonDates().size());
        LessonDateDTO resultLessonDate = result.lessonDates().get(0);
        assertEquals(LocalDate.of(2023, 9, 5), resultLessonDate.date());
        assertEquals(1, resultLessonDate.lessons().size());
        assertEquals(periodLesson, resultLessonDate.lessons().get(0));

        assertEquals(1, result.pupilsGrades().size());
        PupilGradeDTO pupilGradeDTO = result.pupilsGrades().get(0);
        assertEquals(pupil, pupilGradeDTO.pupil());
        assertTrue(pupilGradeDTO.grades().isEmpty());

        assertEquals(1, result.availableSubjects().size());
        assertEquals(subject, result.availableSubjects().get(0));
    }

    @Test
    void saveGradesBatchShouldUpdateExistingGradesTest() {
        Grade existingGrade = new Grade();
        existingGrade.setId(1L);
        existingGrade.setPupil(pupil);
        existingGrade.setPeriodLesson(periodLesson);
        existingGrade.setGrade(4);
        existingGrade.setSubject(subject);

        when(termPeriodService.getById(1L)).thenReturn(termPeriod);
        when(gradeRepository.findByClassAndSubjectAndPeriod(1L, 1L,
                termPeriod.getStartDate(), termPeriod.getEndDate()))
                .thenReturn(List.of(existingGrade));
        when(pupilService.findById(1L)).thenReturn(pupil);
        when(periodLessonService.findByLessonId(1L)).thenReturn(periodLesson);

        gradeService.saveGradesBatch(gradeSaveRequest);

        ArgumentCaptor<List<Grade>> gradesCaptor = ArgumentCaptor.forClass(List.class);
        verify(gradeRepository).saveAll(gradesCaptor.capture());

        List<Grade> savedGrades = gradesCaptor.getValue();
        assertEquals(1, savedGrades.size());
        Grade savedGrade = savedGrades.get(0);
        assertEquals(5, savedGrade.getGrade());
        assertEquals(pupil, savedGrade.getPupil());
        assertEquals(periodLesson, savedGrade.getPeriodLesson());
        assertEquals(subject, savedGrade.getSubject());
    }

    @Test
    void saveGradesBatchCreateNewGradesTest() {
        gradeSaveRequest.setGrades(Map.of(
                1L, Map.of(1L, 5)
        ));

        when(termPeriodService.getById(1L)).thenReturn(termPeriod);
        when(gradeRepository.findByClassAndSubjectAndPeriod(1L, 1L,
                termPeriod.getStartDate(), termPeriod.getEndDate()))
                .thenReturn(List.of());
        when(pupilService.findById(1L)).thenReturn(pupil);
        when(periodLessonService.findByLessonId(1L)).thenReturn(periodLesson);

        gradeService.saveGradesBatch(gradeSaveRequest);

        ArgumentCaptor<List<Grade>> gradesCaptor = ArgumentCaptor.forClass(List.class);
        verify(gradeRepository).saveAll(gradesCaptor.capture());

        List<Grade> savedGrades = gradesCaptor.getValue();
        assertEquals(1, savedGrades.size());
        Grade newGrade = savedGrades.get(0);
        assertEquals(5, newGrade.getGrade());
        assertEquals(pupil, newGrade.getPupil());
        assertEquals(periodLesson, newGrade.getPeriodLesson());
        assertEquals(subject, newGrade.getSubject());
    }

    @Test
    void getGradesForPupilAndWeekMapTest() {
        LocalDate startDate = LocalDate.of(2023, 9, 4);
        LocalDate endDate = LocalDate.of(2023, 9, 10);

        Grade grade = new Grade();
        grade.setId(1L);
        grade.setPupil(pupil);
        grade.setPeriodLesson(periodLesson);
        grade.setGrade(5);
        grade.setSubject(subject);

        when(gradeRepository.findByPupilAndLessonDateBetween(pupil.getId(), startDate, endDate))
                .thenReturn(List.of(grade));

        Map<Long, Grade> result = gradeService.getGradesForPupilAndWeek(pupil, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(periodLesson.getId()));
        assertEquals(grade, result.get(periodLesson.getId()));
    }

    @Test
    void getPupilGradesOverviewCorrectTest() {
        GradeSetupPupilDTO request = new GradeSetupPupilDTO();
        request.setPupil(pupil);
        request.setTermPeriodId(1L);

        LocalDate startDate = termPeriod.getStartDate();
        LocalDate endDate = termPeriod.getEndDate();

        PeriodLesson mathLesson1 = new PeriodLesson();
        mathLesson1.setId(1L);
        mathLesson1.setSubject(subject);
        mathLesson1.setDate(LocalDate.of(2023, 9, 5));

        PeriodLesson mathLesson2 = new PeriodLesson();
        mathLesson2.setId(2L);
        mathLesson2.setSubject(subject);
        mathLesson2.setDate(LocalDate.of(2023, 9, 12));

        Subject physics = new Subject();
        physics.setId(2L);
        physics.setName("Физика");

        PeriodLesson physicsLesson = new PeriodLesson();
        physicsLesson.setId(3L);
        physicsLesson.setSubject(physics);
        physicsLesson.setDate(LocalDate.of(2023, 9, 6));

        PeriodSchedule periodSchedule = new PeriodSchedule();
        periodSchedule.setLessons(List.of(mathLesson1, mathLesson2, physicsLesson));

        Grade mathGrade1 = new Grade();
        mathGrade1.setPupil(pupil);
        mathGrade1.setPeriodLesson(mathLesson1);
        mathGrade1.setGrade(5);
        mathGrade1.setSubject(subject);

        Grade physicsGrade = new Grade();
        physicsGrade.setPupil(pupil);
        physicsGrade.setPeriodLesson(physicsLesson);
        physicsGrade.setGrade(4);
        physicsGrade.setSubject(physics);

        when(termPeriodService.getById(1L)).thenReturn(termPeriod);
        when(periodScheduleRepository.findByClassesAndPeriodWithLessons(classes, termPeriod))
                .thenReturn(Optional.of(periodSchedule));
        when(gradeRepository.findByPupilAndSubjectAndPeriod(pupil, subject, startDate, endDate))
                .thenReturn(List.of(mathGrade1));
        when(gradeRepository.findByPupilAndSubjectAndPeriod(pupil, physics, startDate, endDate))
                .thenReturn(List.of(physicsGrade));

        PupilGradesOverviewDTO result = gradeService.getPupilGradesOverview(request);

        assertNotNull(result);
        assertEquals(pupil, result.getPupil());
        assertEquals(termPeriod, result.getTermPeriod());

        assertFalse(result.getWorkingDates().contains(LocalDate.of(2023, 9, 3))); // Воскресенье
        assertTrue(result.getWorkingDates().contains(LocalDate.of(2023, 9, 4))); // Понедельник

        assertEquals(2, result.getSubjectsGrades().size());

        SubjectGradesDTO mathGrades = result.getSubjectsGrades().stream()
                .filter(sg -> sg.getSubject().equals(subject))
                .findFirst()
                .orElseThrow();
        assertEquals(5, mathGrades.getGradesByDate().get(mathLesson1.getDate()).get(0));
        assertNull(mathGrades.getGradesByDate().get(mathLesson2.getDate()));
        assertEquals(5.0, mathGrades.getAverageGrade());

        SubjectGradesDTO physicsGrades = result.getSubjectsGrades().stream()
                .filter(sg -> sg.getSubject().equals(physics))
                .findFirst()
                .orElseThrow();
        assertEquals(4, physicsGrades.getGradesByDate().get(physicsLesson.getDate()).get(0));
        assertEquals(4.0, physicsGrades.getAverageGrade());
    }

    @Test
    void generateWorkingDatesCheckSundayTest() {
        LocalDate startDate = LocalDate.of(2023, 9, 1);
        LocalDate endDate = LocalDate.of(2023, 9, 10);

        List<LocalDate> result = gradeService.generateWorkingDates(startDate, endDate);

        assertEquals(8, result.size());
        assertFalse(result.contains(LocalDate.of(2023, 9, 3)));
        assertTrue(result.contains(LocalDate.of(2023, 9, 1)));
        assertTrue(result.contains(LocalDate.of(2023, 9, 4)));
    }

    @Test
    void processSubjectGradesCorrectAvgGradesTest() {
        Subject testSubject = new Subject();
        testSubject.setId(3L);
        testSubject.setName("Тестовый предмет");

        PeriodLesson lesson1 = new PeriodLesson();
        lesson1.setId(1L);
        lesson1.setSubject(testSubject);
        lesson1.setDate(LocalDate.of(2023, 9, 5));

        PeriodLesson lesson2 = new PeriodLesson();
        lesson2.setId(2L);
        lesson2.setSubject(testSubject);
        lesson2.setDate(LocalDate.of(2023, 9, 6));

        Grade grade1 = new Grade();
        grade1.setPupil(pupil);
        grade1.setPeriodLesson(lesson1);
        grade1.setGrade(5);
        grade1.setSubject(testSubject);

        Grade grade2 = new Grade();
        grade2.setPupil(pupil);
        grade2.setPeriodLesson(lesson2);
        grade2.setGrade(3);
        grade2.setSubject(testSubject);

        Map.Entry<Subject, List<PeriodLesson>> entry = Map.entry(testSubject, List.of(lesson1, lesson2));

        when(gradeRepository.findByPupilAndSubjectAndPeriod(
                pupil,
                testSubject,
                termPeriod.getStartDate(),
                termPeriod.getEndDate()))
                .thenReturn(List.of(grade1, grade2));

        SubjectGradesDTO result = gradeService.processSubjectGrades(entry, pupil, termPeriod);

        assertNotNull(result);
        assertEquals(testSubject, result.getSubject());
        assertEquals(2, result.getGradesByDate().size());
        assertEquals(5, result.getGradesByDate().get(lesson1.getDate()).get(0));
        assertEquals(3, result.getGradesByDate().get(lesson2.getDate()).get(0));
        assertEquals(4.0, result.getAverageGrade());
    }

    @Test
    void getPupilGradesOverviewExceptionNullTest() {
        GradeSetupPupilDTO request = new GradeSetupPupilDTO();
        request.setPupil(pupil);
        request.setTermPeriodId(1L);

        when(termPeriodService.getById(1L)).thenReturn(termPeriod);
        when(periodScheduleRepository.findByClassesAndPeriodWithLessons(classes, termPeriod))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> gradeService.getPupilGradesOverview(request));
    }
}