package com.delmark.portfoilo.exceptions;

public class UserNotFoundException extends ResponseException{
    public UserNotFoundException() {
        message = "Пользователь не найден";
        code = 404;
    }
}
