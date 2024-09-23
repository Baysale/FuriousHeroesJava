package com.furiousheroes.exception;

public class InsufficientResourcesException extends RuntimeException {
    public InsufficientResourcesException(String message) {
        super(message);
    }
}
