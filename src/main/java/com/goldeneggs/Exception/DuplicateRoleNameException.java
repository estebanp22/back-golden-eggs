package com.goldeneggs.Exception;

/**
 * Exception thrown when trying to insert or update a role with a name that already exists.
 */
public class DuplicateRoleNameException extends RuntimeException {
    public DuplicateRoleNameException(String message) {
        super(message);
    }
}