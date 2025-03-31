package ru.glebova.NauJava;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.custom.CustomScheduleRepositoryImpl;
import ru.glebova.NauJava.adapter.repository.*;
import ru.glebova.NauJava.domain.Classes;
import ru.glebova.NauJava.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
class ScheduleRepositoryTest extends BaseTest {

    private final UsersRepository usersRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final ScheduleRepository scheduleRepository;
    private final CustomScheduleRepositoryImpl customScheduleRepository;

    @Autowired
    public ScheduleRepositoryTest(UsersRepository usersRepository, ClassRepository classRepository,
                                  SubjectRepository subjectRepository, TeacherRepository teacherRepository,
                                  ScheduleRepository scheduleRepository, CustomScheduleRepositoryImpl customScheduleRepository) {
        this.usersRepository = usersRepository;
        this.classRepository = classRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.scheduleRepository = scheduleRepository;
        this.customScheduleRepository = customScheduleRepository;
    }

    @Test
    void testFindByClassNameAndSubjectName() {
        Users user1 = TestValue.createUser(Role.TEACHER);
        usersRepository.save(user1);

        Subject subject = TestValue.createSubject();
        subjectRepository.save(subject);

        Classes classes = TestValue.createClass();
        classRepository.save(classes);

        Teacher teacher = TestValue.createTeacher(subject, user1, classes);
        teacherRepository.save(teacher);

        Schedule schedule = TestValue.createSchedule(classes, teacher, subject);
        scheduleRepository.save(schedule);

        List<Schedule> schedules = scheduleRepository.findByClassNameAndSubjectName(classes.getName(),
                subject.getName());

        assertEquals(1, schedules.size());
        assertEquals(classes.getName(), schedules.get(0).getClasses().getName());
        assertEquals(subject.getName(), schedules.get(0).getSubject().getName());
    }

    @Test
    void testFindByClassNameAndSubjectNameCustom() {
        Users user1 = TestValue.createUser(Role.TEACHER);
        usersRepository.save(user1);

        Subject subject = TestValue.createSubject();
        subjectRepository.save(subject);

        Classes classes = TestValue.createClass();
        classRepository.save(classes);

        Teacher teacher = TestValue.createTeacher(subject, user1, classes);
        teacherRepository.save(teacher);

        Schedule schedule = TestValue.createSchedule(classes, teacher, subject);
        scheduleRepository.save(schedule);

        List<Schedule> schedules = customScheduleRepository.findScheduleByClassValueAndSubject(classes.getName(),
                subject.getName());

        assertEquals(1, schedules.size());
        assertEquals(classes.getName(), schedules.get(0).getClasses().getName());
        assertEquals(subject.getName(), schedules.get(0).getSubject().getName());
    }
}
