package com.delmark.portfoilo.exceptions;
public class UserAlreadyHavePortfolioException extends ResponseException {
    public UserAlreadyHavePortfolioException() {
        message = "Пользователь уже имеет портфолио";
        code = 400;
    }
}
