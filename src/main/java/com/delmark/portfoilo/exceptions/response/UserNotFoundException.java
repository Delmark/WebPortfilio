package com.delmark.portfoilo.exceptions.response;

public class UserNotFoundException extends ResponseException{
    public UserNotFoundException() {
        message = "Пользователь не найден";
        code = 404;
    }
}
