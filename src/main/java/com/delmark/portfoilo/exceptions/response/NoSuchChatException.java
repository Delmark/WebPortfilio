package com.delmark.portfoilo.exceptions.response;

public class NoSuchChatException extends ResponseException {
    public NoSuchChatException() {
        message = "Чат с таким индетификатором не существует";
        code = 404;
    }
}
