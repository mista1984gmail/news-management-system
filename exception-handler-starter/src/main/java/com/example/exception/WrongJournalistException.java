package com.example.exception;

public class WrongJournalistException extends ApplicationException {

    public WrongJournalistException() {
        super("No access. Wrong journalist!!!");
    }

}
