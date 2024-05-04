package com.delmark.portfoilo.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@ControllerAdvice
@Component
public class ExceptionsHandler {

    @ExceptionHandler(UserDoesNotHavePortfolioException.class)
    public ResponseEntity<ErrorResponse> handleException(UserDoesNotHavePortfolioException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400L,
                        "Портфолио не существует, или пользователь его не создал"
                )
        );
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleException(UsernameAlreadyExistsException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400L,
                        "Пользватель с таким логином уже существует"
                )
        );
    }

    @ExceptionHandler(UserAlreadyHavePortfolioException.class)
    public ResponseEntity<ErrorResponse> handleException(UserAlreadyHavePortfolioException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400L,
                        "Пользватель уже имеет портфолио"
                )
        );
    }

    @ExceptionHandler(NoSuchTechException.class)
    public ResponseEntity<ErrorResponse> handleException(NoSuchTechException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400L,
                        "Такой технологии не существует, указан некорректный индентификатор"
                )
        );
    }

    @ExceptionHandler(NoSuchPortfolioException.class)
    public ResponseEntity<ErrorResponse> handleException(NoSuchPortfolioException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400L,
                        "Портфолио с таким индектификатором не существует"
                )
        );
    }

    @ExceptionHandler(TechAlreadyInPortfolioException.class)
    public ResponseEntity<ErrorResponse> handleException(TechAlreadyInPortfolioException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400L,
                        "Портфолио уже имеет технологию с таким индентификатором"
                )
        );
    }

    @ExceptionHandler(NoSuchProjectException.class)
    public ResponseEntity<ErrorResponse> handleException(NoSuchProjectException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400L,
                        "Проекта с таким индентификатором не существует"
                )
        );
    }

    @ExceptionHandler(NoSuchWorkException.class)
    public ResponseEntity<ErrorResponse> handleException(NoSuchWorkException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400L,
                        "Работы с таким индентификатором не существует"
                )
        );
    }

    @ExceptionHandler(WorkplaceAlreadyExistsInPortfolioException.class)
    public ResponseEntity<ErrorResponse> handleException(WorkplaceAlreadyExistsInPortfolioException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400L,
                        "Работа уже существует в портфолио"
                )
        );
    }

    @ExceptionHandler(TechAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleException(TechAlreadyExistsException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400L,
                        "Технология уже существует"
                )
        );
    }



}
