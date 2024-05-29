package com.delmark.portfoilo.exceptions;

public class UserDoesNotHaveRoleException extends ResponseException{
    public UserDoesNotHaveRoleException() {
        message = "Пользователь не имеет такой роли";
        code = 400;
    }
}
