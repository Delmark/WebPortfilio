package com.delmark.portfoilo.exceptions.response;

public class NoSuchTechException extends ResponseException{
    public NoSuchTechException() {
        message = "Такой технологии не существует";
        code = 404;
    }
}
