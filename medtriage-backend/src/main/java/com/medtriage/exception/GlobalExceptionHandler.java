package com.medtriage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

// Manejador central de errores que responde con el formato RFC 7807
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Reglas de negocio incumplidas: respuesta 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Solicitud Inválida de Cita");
        problem.setType(URI.create("https://medtriage.ac.cr/errors/bad-request"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // Recursos que no existen: respuesta 404
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntime(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso No Encontrado");
        problem.setType(URI.create("https://medtriage.ac.cr/errors/not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}