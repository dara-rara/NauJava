package ru.glebova.NauJava.adapter.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import ru.glebova.NauJava.domain.Schedule;

import java.util.List;

@Repository
public class CustomScheduleRepositoryImpl implements CustomScheduleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Schedule> findScheduleByClassValueAndSubject(String className, String subjectName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Schedule> query = cb.createQuery(Schedule.class);
        Root<Schedule> schedule = query.from(Schedule.class);

        Join<Object, Object> classJoin = schedule.join("classValue");
        Join<Object, Object> subjectJoin = schedule.join("subject");

        Predicate classPredicate = cb.equal(classJoin.get("className"), className);
        Predicate subjectPredicate = cb.equal(subjectJoin.get("subjectName"), subjectName);
        query.where(cb.and(classPredicate, subjectPredicate));

        return entityManager.createQuery(query).getResultList();
    }
}
