package ru.glebova.NauJava.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.glebova.NauJava.exception.EmptyNameException;
import ru.glebova.NauJava.exception.PupilNotFoundException;

import java.util.Map;

@RestControllerAdvice
public class GlobalHandler {
    @ExceptionHandler(PupilNotFoundException.class)
    public ResponseEntity<Map<String,String>> handle(PupilNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EmptyNameException.class)
    public ResponseEntity<Map<String,String>> handle(EmptyNameException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }
}
