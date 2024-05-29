package com.delmark.portfoilo.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;


@Slf4j
@ControllerAdvice
@Component
public class ExceptionsHandler {

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
