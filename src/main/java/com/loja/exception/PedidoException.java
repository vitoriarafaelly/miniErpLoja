package com.loja.exception;

public class PedidoException extends RuntimeException {
    public PedidoException(String message, Throwable cause) {
        super(message, cause);
    }

    public PedidoException(String message) {
        super(message);
    }

}
