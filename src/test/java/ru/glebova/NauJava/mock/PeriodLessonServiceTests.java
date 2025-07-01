package ru.glebova.NauJava.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.glebova.NauJava.adapter.controller.dto.GradeSetupDTO;
import ru.glebova.NauJava.adapter.controller.dto.LessonDTO;
import ru.glebova.NauJava.adapter.controller.dto.LessonDateDTO;
import ru.glebova.NauJava.adapter.repository.PeriodLessonRepository;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.service.HomeworkService;
import ru.glebova.NauJava.service.impl.PeriodLessonServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodLessonServiceTests {

    @Mock
    private PeriodLessonRepository periodLessonRepository;

    @Mock
    private HomeworkService homeworkService;

    @InjectMocks
    private PeriodLessonServiceImpl periodLessonService;

    @Test
    void findByPupilAndDateBetweenTest() {
        Users teacherUser = new Users();
        teacherUser.setId(1L);
        teacherUser.setFirstName("Ivan");
        teacherUser.setLastName("Petrov");
        teacherUser.setRole(Role.TEACHER);

        Users pupilUser = new Users();
        pupilUser.setId(2L);
        pupilUser.setFirstName("Alex");
        pupilUser.setLastName("Ivanov");
        pupilUser.setRole(Role.PUPIL);

        Subject subject = new Subject();
        subject.setId(1L);
        subject.setName("Mathematics");

        Classes classes = new Classes();
        classes.setId(1L);
        classes.setName("5A");

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setUsers(teacherUser);
        teacher.addSubject(subject);
        teacher.addClass(classes);

        Pupil pupil = new Pupil();
        pupil.setId(1L);
        pupil.setUsers(pupilUser);
        pupil.setClasses(classes);

        LocalDate startOfWeek = LocalDate.of(2023, 9, 4);
        LocalDate endOfWeek = LocalDate.of(2023, 9, 10);

        Map<Long, Grade> grades = new HashMap<>();
        Grade grade = new Grade();
        grade.setGrade(5);
        grades.put(1L, grade);

        PeriodLesson lesson1 = new PeriodLesson();
        lesson1.setId(1L);
        lesson1.setDate(LocalDate.of(2023, 9, 4));
        lesson1.setTimeStart(LocalTime.of(9, 0));
        lesson1.setTimeEnd(LocalTime.of(9, 45));
        lesson1.setLessonNumber(1);
        lesson1.setSubject(subject);
        lesson1.setTeacher(teacher);

        List<PeriodLesson> lessons = Arrays.asList(lesson1);

        when(periodLessonRepository.findByClassAndDateBetween(classes, startOfWeek, endOfWeek))
                .thenReturn(lessons);

        Map<Long, Homework> homeworks = new HashMap<>();
        Homework homework = new Homework();
        homework.setDescription("Do exercises");
        homeworks.put(1L, homework);

        when(homeworkService.getHomeworksForLessons(lessons)).thenReturn(homeworks);

        Map<LocalDate, List<LessonDTO>> result = periodLessonService.findByPupilAndDateBetween(
                pupil, startOfWeek, endOfWeek, grades);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(LocalDate.of(2023, 9, 4)));

        LessonDTO dto1 = result.get(LocalDate.of(2023, 9, 4)).get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("Mathematics", dto1.getSubjectName());
        assertEquals("Petrov Ivan", dto1.getTeacherFullName());
        assertEquals(5, dto1.getGradeValue());
        assertEquals("Do exercises", dto1.getHomeworkDescription());
    }

    @Test
    void findByDateForTeacherTest() {
        GradeSetupDTO request = new GradeSetupDTO();
        request.setTeacherId(1L);
        request.setClassId(1L);
        request.setSubjectId(1L);

        TermPeriod termPeriod = new TermPeriod();
        termPeriod.setStartDate(LocalDate.of(2023, 9, 1));
        termPeriod.setEndDate(LocalDate.of(2023, 12, 31));

        List<LocalDate> dates = Arrays.asList(
                LocalDate.of(2023, 9, 4),
                LocalDate.of(2023, 9, 5)
        );

        when(periodLessonRepository.findDistinctDatesByTeacherClassSubjectAndPeriod(
                1L, 1L, 1L, termPeriod.getStartDate(), termPeriod.getEndDate()))
                .thenReturn(dates);

        PeriodLesson lesson1 = new PeriodLesson();
        PeriodLesson lesson2 = new PeriodLesson();

        when(periodLessonRepository.findLessonsByTeacherClassSubjectAndDate(1L, 1L, 1L, dates.get(0)))
                .thenReturn(List.of(lesson1));
        when(periodLessonRepository.findLessonsByTeacherClassSubjectAndDate(1L, 1L, 1L, dates.get(1)))
                .thenReturn(List.of(lesson2));

        List<LessonDateDTO> result = periodLessonService.findByDateForTeacher(request, termPeriod);

        assertEquals(2, result.size());
        assertEquals(dates.get(0), result.get(0).date());
        assertEquals(1, result.get(0).lessons().size());
        assertEquals(dates.get(1), result.get(1).date());
        assertEquals(1, result.get(1).lessons().size());
    }
}