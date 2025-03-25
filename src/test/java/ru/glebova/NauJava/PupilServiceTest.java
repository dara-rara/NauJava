package ru.glebova.NauJava;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.glebova.NauJava.adapter.repository.*;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.domain.Class;
import ru.glebova.NauJava.service.PupilService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;


@SpringBootTest
@Import(ConfigurationTest.class)
class PupilServiceTest {

    private final UsersRepository usersRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final PupilRepository pupilRepository;
    private final TestValue testValue;
    private final GradeRepository gradeRepository;
    private final PupilService pupilService;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public PupilServiceTest(UsersRepository usersRepository, ClassRepository classRepository,
                            SubjectRepository subjectRepository, TeacherRepository teacherRepository,
                            PupilRepository pupilRepository, TestValue testValue,
                            GradeRepository gradeRepository, PupilService pupilService, PlatformTransactionManager transactionManager) {
        this.usersRepository = usersRepository;
        this.classRepository = classRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.pupilRepository = pupilRepository;
        this.testValue = testValue;
        this.gradeRepository = gradeRepository;
        this.pupilService = pupilService;
        this.transactionManager = transactionManager;
    }

    @Test
    void testDeleteById() {

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
