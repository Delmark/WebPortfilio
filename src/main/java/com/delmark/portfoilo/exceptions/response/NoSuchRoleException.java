package com.delmark.portfoilo.exceptions.response;

public class NoSuchRoleException extends ResponseException{
    public NoSuchRoleException() {
        message = "Такой роли не существует";
        code = 404;
    }
}
