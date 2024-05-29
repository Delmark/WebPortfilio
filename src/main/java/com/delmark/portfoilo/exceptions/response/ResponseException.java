package com.delmark.portfoilo.exceptions.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResponseException extends RuntimeException{
    public String message = "Ошибка на стороне сервера";
    public int code = 500;
}
