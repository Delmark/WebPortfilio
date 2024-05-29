package com.delmark.portfoilo.exceptions.response;

public class NoSuchTechInPortfolio extends ResponseException{
    public NoSuchTechInPortfolio() {
        message = "Такой технологии в портфолио не существует";
        code = 400;
    }
}
