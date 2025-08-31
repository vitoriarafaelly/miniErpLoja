package com.loja.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CepFormatoInvalidoException extends RuntimeException {

    public CepFormatoInvalidoException(String message) {
        super(message);
    }
}
