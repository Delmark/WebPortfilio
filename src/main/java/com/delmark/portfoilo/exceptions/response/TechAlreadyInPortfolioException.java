package com.delmark.portfoilo.exceptions.response;

public class TechAlreadyInPortfolioException extends ResponseException{
    public TechAlreadyInPortfolioException() {
        message = "Эта технология уже существует в портфолио";
        code = 400;
    }
}
