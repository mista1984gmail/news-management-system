package com.example.exception;

public class TokenIsNotValidException extends ApplicationException {

    public TokenIsNotValidException() {
        super("Token is not valid!!!");
    }

}
