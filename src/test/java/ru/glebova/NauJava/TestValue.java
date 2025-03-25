package ru.glebova.NauJava;

import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.domain.Class;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class TestValue {

    public Schedule createSchedule(Class classValue, Teacher teacher, Subject subject) {
        Schedule schedule = new Schedule();
        schedule.setTeacher(teacher);
        schedule.setClassValue(classValue);
        schedule.setSubject(subject);
        schedule.setTime(LocalTime.now());
        schedule.setDayOfWeek(UUID.randomUUID().toString());
        return schedule;
    }

    public Grade createGrade(Pupil pupil, Subject subject, Integer count) {
        Grade grade = new Grade();
        grade.setPupil(pupil);
        grade.setSubject(subject);
        grade.setGrade(count);
        grade.setDate(LocalDate.now());
        return grade;
    }

    public Pupil createPupil(Class classTest, Users user) {
        Pupil pupil = new Pupil();
        pupil.setUser(user);
        pupil.setClassValue(classTest);
        return pupil;
    }

    public Class createClass(Teacher teacher) {
        Class classTest = new Class();
        classTest.setClassName(UUID.randomUUID().toString());
        classTest.setTeacher(teacher);
        return classTest;
    }

    public Teacher createTeacher(Subject subject, Users user) {
        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setSubject(subject);
        return teacher;
    }

    public Subject createSubject() {
        Subject subject = new Subject();
        subject.setSubjectName(UUID.randomUUID().toString());
        return subject;
    }

    public Users createUser(Role role) {
        Users user = new Users();
        user.setUsername(UUID.randomUUID().toString());
        user.setPassword(UUID.randomUUID().toString());
        user.setFirstName(UUID.randomUUID().toString());
        user.setLastName(UUID.randomUUID().toString());
        user.setRole(role);
        return user;
    }
}
