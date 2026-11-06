package com.monetra.core.exception;

public class MonetraException extends RuntimeException {
    public MonetraException(String message) {
        super(message);
    }

    public MonetraException(String message, Throwable cause) {
        super(message, cause);
    }
}
