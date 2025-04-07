package ru.glebova.NauJava;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.repository.custom.CustomGradeRepositoryImpl;
import ru.glebova.NauJava.adapter.repository.*;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.domain.Classes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
class GradeRepositoryTest extends BaseTest {

    private final UsersRepository usersRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final PupilRepository pupilRepository;
    private final GradeRepository gradeRepository;
    private final CustomGradeRepositoryImpl customGradeRepository;

    @Autowired
    public GradeRepositoryTest(UsersRepository usersRepository, ClassRepository classRepository,
                               SubjectRepository subjectRepository, TeacherRepository teacherRepository,
                               PupilRepository pupilRepository, GradeRepository gradeRepository,
                               CustomGradeRepositoryImpl customGradeRepository) {
        this.usersRepository = usersRepository;
        this.classRepository = classRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.pupilRepository = pupilRepository;
        this.gradeRepository = gradeRepository;
        this.customGradeRepository = customGradeRepository;
    }

    @Test
    void testFindByPupilAndSubject() {
        Users user1 = TestValue.createUser(Role.TEACHER);
        usersRepository.save(user1);

        Subject subject = TestValue.createSubject();
        subjectRepository.save(subject);

        Classes classes = TestValue.createClass();
        classRepository.save(classes);

        Teacher teacher = TestValue.createTeacher(subject, user1, classes);
        teacherRepository.save(teacher);

        Users user2 = TestValue.createUser(Role.PUPIL);
        usersRepository.save(user2);

        Pupil pupil = TestValue.createPupil(classes, user2);
        pupilRepository.save(pupil);

        Grade grade = TestValue.createGrade(pupil, subject, teacher, 5);
        gradeRepository.save(grade);

        List<Grade> grades = gradeRepository.findByPupilAndSubject(pupil, subject);

        assertEquals(1, grades.size());
        assertEquals(5, grades.get(0).getGrade());
    }

    @Test
    void testFindByPupilAndSubjectCustom() {
        Users user1 = TestValue.createUser(Role.TEACHER);
        usersRepository.save(user1);

        Subject subject = TestValue.createSubject();
        subjectRepository.save(subject);

        Classes classes = TestValue.createClass();
        classRepository.save(classes);

        Teacher teacher = TestValue.createTeacher(subject, user1, classes);
        teacherRepository.save(teacher);

        Users user2 = TestValue.createUser(Role.PUPIL);
        usersRepository.save(user2);

        Pupil pupil = TestValue.createPupil(classes, user2);
        pupilRepository.save(pupil);

        Grade grade = TestValue.createGrade(pupil, subject, teacher, 4);
        gradeRepository.save(grade);

        List<Grade> grades = customGradeRepository.findGradesByPupilAndSubject(pupil, subject);

        assertEquals(1, grades.size());
        assertEquals(4, grades.get(0).getGrade());
    }
}
