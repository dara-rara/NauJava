package ru.glebova.NauJava.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.glebova.NauJava.exception.ImportException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandler {
    @ExceptionHandler(ImportException.class)
    public ResponseEntity<Map<String, Object>> handleImportException(ImportException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("errorCode", ex.getErrorCode());

        if (ex.getId() != null) {
            response.put("id", ex.getId());
        }
        if (ex.getLineNumber() != null) {
            response.put("lineNumber", ex.getLineNumber());
        }

        HttpStatus status = determineHttpStatus(ex.getErrorCode());
        return new ResponseEntity<>(response, status);
    }

    private HttpStatus determineHttpStatus(String errorCode) {
        return switch (errorCode) {
            case "IMPORT_ERROR" -> HttpStatus.BAD_REQUEST;
            case "INVALID_FORMAT" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "FILE_IMPORT_ERROR" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
