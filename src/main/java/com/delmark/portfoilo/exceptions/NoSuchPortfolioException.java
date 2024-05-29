package com.delmark.portfoilo.exceptions;

public class NoSuchPortfolioException extends ResponseException{
    public NoSuchPortfolioException() {
        message = "Портфолио с таким индетификатором не существует";
        code = 404;
    }
}
