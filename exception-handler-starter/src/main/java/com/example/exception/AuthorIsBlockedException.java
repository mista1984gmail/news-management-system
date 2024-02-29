package com.example.exception;

public class AuthorIsBlockedException extends ApplicationException {

    public AuthorIsBlockedException(Long id) {
        super("Author with id " + id + " is blocked!!!");
    }

}
