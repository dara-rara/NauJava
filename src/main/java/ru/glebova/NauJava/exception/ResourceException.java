package ru.glebova.NauJava.exception;

import jakarta.persistence.EntityNotFoundException;

public class ResourceException extends EntityNotFoundException {
    public ResourceException(String msg) {
        super(msg);
    }
}
