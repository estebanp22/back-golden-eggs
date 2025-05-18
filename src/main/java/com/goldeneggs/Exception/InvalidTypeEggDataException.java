package com.goldeneggs.Exception;

/**
 * Exception thrown when TypeEgg data is invalid or conflicts with existing records.
 */
public class InvalidTypeEggDataException extends RuntimeException {
    public InvalidTypeEggDataException(String message) {
        super(message);
    }
}

