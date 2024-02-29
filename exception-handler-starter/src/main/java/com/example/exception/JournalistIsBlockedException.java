package com.example.exception;

public class JournalistIsBlockedException extends ApplicationException {

    public JournalistIsBlockedException(Long  id) {
        super("Journalist with id " + id + " is blocked!!!");
    }

}
