package com.delmark.portfoilo.exceptions;

public class NoSuchWorkException extends ResponseException{
    public NoSuchWorkException() {
        message = "Такой работы не существует";
        code = 404;
    }
}
