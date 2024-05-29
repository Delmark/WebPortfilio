package com.delmark.portfoilo.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResponseException extends RuntimeException{
    String message = "Ошибка на стороне сервера";
    int code = 500;
}
