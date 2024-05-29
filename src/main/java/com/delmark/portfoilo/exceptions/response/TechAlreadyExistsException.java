package com.delmark.portfoilo.exceptions.response;

public class TechAlreadyExistsException extends ResponseException {
    public TechAlreadyExistsException() {
        message = "Такая технология уже существует";
        code = 400;
    }
}
