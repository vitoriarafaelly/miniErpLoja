package com.loja.exception;

public class ClienteException extends RuntimeException {
    public ClienteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClienteException(String message) {
        super(message);
    }
}
