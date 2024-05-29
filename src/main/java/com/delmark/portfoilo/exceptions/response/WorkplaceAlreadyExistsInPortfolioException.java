package com.delmark.portfoilo.exceptions.response;

public class WorkplaceAlreadyExistsInPortfolioException extends ResponseException{
    public WorkplaceAlreadyExistsInPortfolioException() {
        message = "Место работы уже существует в портфолио";
        code = 400;
    }
}
