package com.example.exception;

public class WrongAuthorException extends ApplicationException {

    public WrongAuthorException() {
        super("No access. Wrong author!!!");
    }

}
