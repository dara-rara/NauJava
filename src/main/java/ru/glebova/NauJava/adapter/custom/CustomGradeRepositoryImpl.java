package ru.glebova.NauJava.adapter.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import ru.glebova.NauJava.domain.Grade;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.Subject;

import java.util.List;

@Repository
public class CustomGradeRepositoryImpl implements CustomGradeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Grade> findGradesByPupilAndSubject(Pupil pupil, Subject subject) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Grade> query = cb.createQuery(Grade.class);
        Root<Grade> grade = query.from(Grade.class);

        Predicate pupilPredicate = cb.equal(grade.get("pupil"), pupil);
        Predicate subjectPredicate = cb.equal(grade.get("subject"), subject);
        query.where(cb.and(pupilPredicate, subjectPredicate));

        return entityManager.createQuery(query).getResultList();
    }
}
