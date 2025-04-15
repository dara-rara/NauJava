package ru.glebova.NauJava.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.glebova.NauJava.exception.PupilNotFoundException;

import java.util.Map;

public class GlobalHandler {
    @ExceptionHandler(PupilNotFoundException.class)
    public ResponseEntity<Map<String,String>> handle(PupilNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
}
