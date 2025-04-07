package ru.glebova.NauJava.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.glebova.NauJava.adapter.repository.GradeRepository;
import ru.glebova.NauJava.adapter.repository.PupilRepository;
import ru.glebova.NauJava.adapter.repository.UsersRepository;
import ru.glebova.NauJava.domain.Grade;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.Users;

import java.util.List;


@Service
public class PupilServiceImpl implements PupilService {

    private final PupilRepository pupilRepository;
    private final GradeRepository gradeRepository;
    private final UsersRepository usersRepository;
    private final PlatformTransactionManager transactionManager;

    public PupilServiceImpl(PupilRepository pupilRepository, GradeRepository gradeRepository, UsersRepository usersRepository, PlatformTransactionManager transactionManager) {
        this.pupilRepository = pupilRepository;
        this.gradeRepository = gradeRepository;
        this.usersRepository = usersRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID ученика не может быть null.");
        }

        TransactionStatus status = transactionManager
                .getTransaction(new DefaultTransactionDefinition());

        try {
            Pupil pupil = pupilRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Ученик с ID " + id + " не найден."));

            Users users = usersRepository.findById(pupil.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " +
                            pupil.getUser().getId() + " не найден."));

            List<Grade> grades = gradeRepository.findByPupil(pupil);

            gradeRepository.deleteAll(grades);
            pupilRepository.delete(pupil);
            usersRepository.delete(users);

            transactionManager.commit(status);
        } catch (Exception ex) {
            transactionManager.rollback(status);
            throw ex;
        }
    }
}