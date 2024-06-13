package com.delmark.portfoilo.exceptions.response;

public class NoSuchMessageException extends ResponseException {
    public NoSuchMessageException() {
        message = "Такого сообщения не существует";
        code = 404;
    }
}
