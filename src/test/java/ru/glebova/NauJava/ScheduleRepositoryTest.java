package ru.glebova.NauJava;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.custom.CustomScheduleRepositoryImpl;
import ru.glebova.NauJava.adapter.repository.*;
import ru.glebova.NauJava.domain.Class;
import ru.glebova.NauJava.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
@Import(ConfigurationTest.class)
class ScheduleRepositoryTest {

    private final UsersRepository usersRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final ScheduleRepository scheduleRepository;
    private final CustomScheduleRepositoryImpl customScheduleRepository;
    private final TestValue testValue;

    @Autowired
    public ScheduleRepositoryTest(UsersRepository usersRepository, ClassRepository classRepository,
                                  SubjectRepository subjectRepository, TeacherRepository teacherRepository,
                                  ScheduleRepository scheduleRepository, CustomScheduleRepositoryImpl customScheduleRepository, TestValue testValue) {
        this.usersRepository = usersRepository;
        this.classRepository = classRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.scheduleRepository = scheduleRepository;
        this.customScheduleRepository = customScheduleRepository;
        this.testValue = testValue;
    }

    @Test
    void testFindByClassNameAndSubjectName() {
        Users user1 = testValue.createUser(Role.TEACHER);
        usersRepository.save(user1);

        Subject subject = testValue.createSubject();
        subjectRepository.save(subject);

        Teacher teacher = testValue.createTeacher(subject, user1);
        teacherRepository.save(teacher);

        Class classTest = testValue.createClass(teacher);
        classRepository.save(classTest);

        Schedule schedule = testValue.createSchedule(classTest, teacher, subject);
        scheduleRepository.save(schedule);

        List<Schedule> schedules = scheduleRepository.findByClassNameAndSubjectName(classTest.getClassName(),
                subject.getSubjectName());

        assertEquals(1, schedules.size());
        assertEquals(classTest.getClassName(), schedules.get(0).getClassValue().getClassName());
        assertEquals(subject.getSubjectName(), schedules.get(0).getSubject().getSubjectName());
    }

    @Test
    void testFindByClassNameAndSubjectNameCustom() {
        Users user1 = testValue.createUser(Role.TEACHER);
        usersRepository.save(user1);

        Subject subject = testValue.createSubject();
        subjectRepository.save(subject);

        Teacher teacher = testValue.createTeacher(subject, user1);
        teacherRepository.save(teacher);

        Class classTest = testValue.createClass(teacher);
        classRepository.save(classTest);

        Schedule schedule = testValue.createSchedule(classTest, teacher, subject);
        scheduleRepository.save(schedule);

        List<Schedule> schedules = customScheduleRepository.findScheduleByClassValueAndSubject(classTest.getClassName(),
                subject.getSubjectName());

        assertEquals(1, schedules.size());
        assertEquals(classTest.getClassName(), schedules.get(0).getClassValue().getClassName());
        assertEquals(subject.getSubjectName(), schedules.get(0).getSubject().getSubjectName());
    }
}
