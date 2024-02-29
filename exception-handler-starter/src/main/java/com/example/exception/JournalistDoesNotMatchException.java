package com.example.exception;

public class JournalistDoesNotMatchException extends ApplicationException {

    public JournalistDoesNotMatchException() {
        super("The journalist of the comment is not the same");
    }

}
