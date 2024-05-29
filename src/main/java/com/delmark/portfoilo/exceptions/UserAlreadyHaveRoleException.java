package com.delmark.portfoilo.exceptions;

public class UserAlreadyHaveRoleException extends ResponseException{
    public UserAlreadyHaveRoleException() {
        message = "У пользователя уже есть такая роль";
        code = 400;
    }
}
