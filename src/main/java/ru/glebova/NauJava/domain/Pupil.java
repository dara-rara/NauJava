package ru.glebova.NauJava.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "Pupil")
public class Pupil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users users;

    @ManyToOne
    @JoinColumn(name = "classId", nullable = false)
    private Class classValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return users;
    }

    public void setUser(Users users) {
        this.users = users;
    }

    public Class getClassValue() {
        return classValue;
    }

    public void setClassValue(Class classValue) {
        this.classValue = classValue;
    }
}