package ru.glebova.NauJava;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import ru.glebova.NauJava.adapter.repository.*;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.domain.Classes;
import ru.glebova.NauJava.service.PupilService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class PupilServiceTest extends BaseTest {

    private final UsersRepository usersRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final PupilRepository pupilRepository;
    private final GradeRepository gradeRepository;
    private final PupilService pupilService;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public PupilServiceTest(UsersRepository usersRepository, ClassRepository classRepository,
                            SubjectRepository subjectRepository, TeacherRepository teacherRepository,
                            PupilRepository pupilRepository, GradeRepository gradeRepository,
                            PupilService pupilService, PlatformTransactionManager transactionManager) {
        this.usersRepository = usersRepository;
        this.classRepository = classRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.pupilRepository = pupilRepository;
        this.gradeRepository = gradeRepository;
        this.pupilService = pupilService;
        this.transactionManager = transactionManager;
    }

    @Test
    void testDeleteById() {

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

        pupilService.deleteById(pupil.getId());

        Optional<Pupil> emptyPupil = pupilRepository.findById(pupil.getId());
        assertTrue(emptyPupil.isEmpty());
    }

    @Test
    void testErrorDeleteById() {
        Long nonExistId = 1L;
        assertThrows(
                Exception.class,
                () -> pupilService.deleteById(nonExistId)
        );
    }
}
