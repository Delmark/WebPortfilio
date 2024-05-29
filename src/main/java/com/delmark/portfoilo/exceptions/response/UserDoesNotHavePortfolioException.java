package com.delmark.portfoilo.exceptions.response;

public class UserDoesNotHavePortfolioException extends ResponseException{
    public UserDoesNotHavePortfolioException() {
        message = "Пользователь не имеет портфолио";
        code = 400;
    }
}
