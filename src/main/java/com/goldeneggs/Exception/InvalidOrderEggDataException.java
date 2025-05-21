package com.goldeneggs.Exception;

public class InvalidOrderEggDataException extends RuntimeException {
    public InvalidOrderEggDataException(String message) {
        super(message);
    }
}
