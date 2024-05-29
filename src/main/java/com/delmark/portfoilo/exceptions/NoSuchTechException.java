package com.delmark.portfoilo.exceptions;

public class NoSuchTechException extends ResponseException{
    public NoSuchTechException() {
        message = "Такой технологии не существует";
        code = 404;
    }
}
