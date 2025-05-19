package com.goldeneggs.Exception;

public class InvalidPayDataException extends RuntimeException {
    public InvalidPayDataException(String message) {
        super(message);
    }
}
