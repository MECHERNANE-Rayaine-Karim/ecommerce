package com.rayaine.ecommerce.exception;

public class UnavailableProductException extends RuntimeException {
    public UnavailableProductException(String message) {
        super(message);
    }
}
