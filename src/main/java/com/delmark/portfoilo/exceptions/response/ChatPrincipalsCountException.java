package com.delmark.portfoilo.exceptions.response;

public class ChatPrincipalsCountException extends ResponseException {
    public ChatPrincipalsCountException() {
        message = "В чате должно быть не менее двух участников";
        code = 400;
    }
}
