package com.delmark.portfoilo.exceptions;

public class NoSuchProjectException extends ResponseException {
    public NoSuchProjectException() {
        message = "Такого проекта не существует";
        code = 404;
    }
}
