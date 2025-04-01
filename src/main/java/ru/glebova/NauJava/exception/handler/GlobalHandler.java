package ru.glebova.NauJava.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.glebova.NauJava.exception.ResourceException;

public class GlobalHandler {
    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<MessageResponse> handle(ResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(ex.getMessage()));
    }
}
