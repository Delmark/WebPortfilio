package com.delmark.portfoilo.exceptions;

import com.delmark.portfoilo.exceptions.response.ResponseException;
import com.delmark.portfoilo.exceptions.validation.ValidationErrorResponse;
import com.delmark.portfoilo.exceptions.validation.Violation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;


@Slf4j
@ControllerAdvice
@Component
public class ExceptionsHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> onConstraintValidationExceptionHandler(ConstraintViolationException ex) {
        List<Violation> violations = ex.getConstraintViolations().stream().
                map(constraintViolation -> new Violation(
                            constraintViolation.getPropertyPath().toString(),
                            constraintViolation.getMessage()
                    )
                ).toList();
        return ResponseEntity.status(400).body(
                new ValidationErrorResponse(violations)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> onMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(400).body(
                new ValidationErrorResponse(violations)
        );
    }

    @ExceptionHandler(ResponseException.class)
    public ResponseEntity<ErrorResponse> onRestRequestExceptionHandler(ResponseException ex) {
        return ResponseEntity.status(ex.code).body(
                new ErrorResponse(
                        ex.code,
                        ex.message
                )
        );
    }
}
