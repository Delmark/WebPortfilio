package com.delmark.portfoilo.exceptions.response;

public class NoSuchProjectException extends ResponseException {
    public NoSuchProjectException() {
        message = "Такого проекта не существует";
        code = 404;
    }
}
