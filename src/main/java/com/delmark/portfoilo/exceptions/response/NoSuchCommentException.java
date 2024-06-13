package com.delmark.portfoilo.exceptions.response;

public class NoSuchCommentException extends ResponseException {
    public NoSuchCommentException() {
        message = "Такого комментария не существует";
        code = 404;
    }
}
