package com.alexr.ecommerce.exception;

public class ProductoNotFoundException extends ResourceNotFoundException {
    public ProductoNotFoundException(String message) {
        super(message);
    }
}
