package com.delmark.portfoilo.exceptions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorResponse {

    private final Integer codeID;

    private final String message;
}
