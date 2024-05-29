package com.delmark.portfoilo.exceptions;

import com.delmark.portfoilo.exceptions.response.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


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
