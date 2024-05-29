package com.delmark.portfoilo.exceptions;

public class UsernameAlreadyExistsException extends ResponseException{
    public UsernameAlreadyExistsException() {
        message = "Такой пользователь уже существует";
        code = 400;
    }
}
