package com.goldeneggs.Exception;

/**
 * Exception thrown when user data is invalid
 */
public class InvalidUserDataException extends RuntimeException {
    public InvalidUserDataException(String message) {
        super(message);
    }
}
