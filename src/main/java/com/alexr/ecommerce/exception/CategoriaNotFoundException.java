package com.alexr.ecommerce.exception;

public class CategoriaNotFoundException extends ResourceNotFoundException {
    public CategoriaNotFoundException(String message) {
        super(message);
    }
}
