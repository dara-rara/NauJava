package ru.glebova.NauJava.domain;

public enum Role {
    PUPIL, TEACHER, ADMIN, USER;
    public String getAuthority() {
        return "ROLE_" + name();
    }
}