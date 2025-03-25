package ru.glebova.NauJava;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.custom.CustomGradeRepositoryImpl;
import ru.glebova.NauJava.adapter.repository.*;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.domain.Class;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
@Import(ConfigurationTest.class)
class GradeRepositoryTest {

    private final UsersRepository usersRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final PupilRepository pupilRepository;
    private final GradeRepository gradeRepository;
    private final CustomGradeRepositoryImpl customGradeRepository;
    private final TestValue testValue;

    @Autowired
    public GradeRepositoryTest(UsersRepository usersRepository, ClassRepository classRepository,
                               SubjectRepository subjectRepository, TeacherRepository teacherRepository,
                               PupilRepository pupilRepository, GradeRepository gradeRepository,
                               CustomGradeRepositoryImpl customGradeRepository, TestValue testValue) {
        this.usersRepository = usersRepository;
        this.classRepository = classRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.pupilRepository = pupilRepository;
        this.gradeRepository = gradeRepository;
        this.customGradeRepository = customGradeRepository;
        this.testValue = testValue;
    }

    @Test
    void testFindByPupilAndSubject() {
        Users user1 = testValue.createUser(Role.TEACHER);
        usersRepository.save(user1);

        Subject subject = testValue.createSubject();
        subjectRepository.save(subject);

        Teacher teacher = testValue.createTeacher(subject, user1);
        teacherRepository.save(teacher);

        Class classTest = testValue.createClass(teacher);
        classRepository.save(classTest);

        Users user2 = testValue.createUser(Role.PUPIL);
        usersRepository.save(user2);

        Pupil pupil = testValue.createPupil(classTest, user2);
        pupilRepository.save(pupil);

        Grade grade = testValue.createGrade(pupil, subject, 5);
        gradeRepository.save(grade);

        List<Grade> grades = gradeRepository.findByPupilAndSubject(pupil, subject);

        assertEquals(1, grades.size());
        assertEquals(5, grades.get(0).getGrade());
    }

    @Test
    void testFindByPupilAndSubjectCustom() {
        Users user1 = testValue.createUser(Role.TEACHER);
        usersRepository.save(user1);

        Subject subject = testValue.createSubject();
        subjectRepository.save(subject);

        Teacher teacher = testValue.createTeacher(subject, user1);
        teacherRepository.save(teacher);

        Class classTest = testValue.createClass(teacher);
        classRepository.save(classTest);

        Users user2 = testValue.createUser(Role.PUPIL);
        usersRepository.save(user2);

        Pupil pupil = testValue.createPupil(classTest, user2);
        pupilRepository.save(pupil);

        Grade grade = testValue.createGrade(pupil, subject, 4);
        gradeRepository.save(grade);

        List<Grade> grades = customGradeRepository.findGradesByPupilAndSubject(pupil, subject);

        assertEquals(1, grades.size());
        assertEquals(4, grades.get(0).getGrade());
    }
}
