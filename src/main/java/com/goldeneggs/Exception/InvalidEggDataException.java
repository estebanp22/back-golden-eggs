package com.goldeneggs.Exception;

public class InvalidEggDataException extends RuntimeException{
    public InvalidEggDataException(String message) {
        super(message);
    }
}
