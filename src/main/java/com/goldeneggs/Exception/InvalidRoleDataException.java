package com.goldeneggs.Exception;

/**
 * Exception thrown when a Role has invalid data.
 */
public class InvalidRoleDataException extends RuntimeException {
    public InvalidRoleDataException(String message) {
        super(message);
    }
}