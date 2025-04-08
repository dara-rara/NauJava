package ru.glebova.NauJava.exception;

import jakarta.persistence.EntityNotFoundException;

public class PupilNotFoundException extends EntityNotFoundException {
    public PupilNotFoundException(Long id) {
        super("Pupil with id " + id + " not found");
    }
}
