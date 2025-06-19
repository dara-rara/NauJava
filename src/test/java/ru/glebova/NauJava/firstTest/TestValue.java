package ru.glebova.NauJava.firstTest;

import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.domain.Classes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public final class TestValue {

    public static Schedule createSchedule(Classes classes, Teacher teacher, Subject subject) {
        Schedule schedule = new Schedule();
        schedule.setTeacher(teacher);
        schedule.setClasses(classes);
        schedule.setSubject(subject);
        schedule.setTime(LocalTime.now());
        schedule.setDayOfWeek(UUID.randomUUID().toString());
        return schedule;
    }

    public static Grade createGrade(Pupil pupil, Subject subject, Teacher teacher, Integer count) {
        Grade grade = new Grade();
        grade.setPupil(pupil);
        grade.setSubject(subject);
        grade.setTeacher(teacher);
        grade.setGrade(count);
        grade.setDate(LocalDate.now());
        return grade;
    }

    public static Pupil createPupil(Classes classes, Users user) {
        Pupil pupil = new Pupil();
        pupil.setUser(user);
        pupil.setClasses(classes);
        return pupil;
    }

    public static Classes createClass() {
        Classes classes = new Classes();
        classes.setName(UUID.randomUUID().toString());
        return classes;
    }

    public static Teacher createTeacher(Subject subject, Users user, Classes classes) {
        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setSubject(subject);
        teacher.addClass(classes);
        return teacher;
    }

    public static Subject createSubject() {
        Subject subject = new Subject();
        subject.setName(UUID.randomUUID().toString());
        return subject;
    }

    public static Users createUser(Role role) {
        Users user = new Users();
        user.setUsername(UUID.randomUUID().toString());
        user.setPassword(UUID.randomUUID().toString());
        user.setFirstName(UUID.randomUUID().toString());
        user.setLastName(UUID.randomUUID().toString());
        user.setRole(role);
        return user;
    }
}
