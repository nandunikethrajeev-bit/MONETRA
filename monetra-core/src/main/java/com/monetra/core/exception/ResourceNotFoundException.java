package com.monetra.core.exception;

public class ResourceNotFoundException extends MonetraException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s with id '%s' was not found", resourceName, id));
    }
}

