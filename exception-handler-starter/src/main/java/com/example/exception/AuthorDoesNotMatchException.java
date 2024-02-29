package com.example.exception;

public class AuthorDoesNotMatchException extends ApplicationException {

    public AuthorDoesNotMatchException() {
        super("The author of the comment is not the same");
    }

}
