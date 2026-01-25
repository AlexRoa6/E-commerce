package com.alexr.ecommerce.exception;

public class UsuarioNoRegistradoException extends RuntimeException {
    public UsuarioNoRegistradoException(String message) {
        super(message);
    }
}
