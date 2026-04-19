package com.pfm.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({
            IllegalArgumentException.class,
            DateTimeParseException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(ex.getMessage())));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleIo(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", String.valueOf(ex.getMessage())));
    }

    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<Map<String, String>> handleExecution(ExecutionException ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", String.valueOf(cause.getMessage())));
    }
}
