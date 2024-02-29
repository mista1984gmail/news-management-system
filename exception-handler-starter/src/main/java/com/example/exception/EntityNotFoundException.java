package com.example.exception;

public class EntityNotFoundException extends ApplicationException {

    public EntityNotFoundException(Class<?> clazz, String username) {
        super(String.format("%s with username: %s not found", clazz.getSimpleName(), username));
    }

    public EntityNotFoundException(Class<?> clazz, Long id) {
        super(String.format("%s with id: %d not found", clazz.getSimpleName(), id));
    }

}
