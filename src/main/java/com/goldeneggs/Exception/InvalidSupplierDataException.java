package com.goldeneggs.Exception;

/**
 * Exception thrown when supplier data fails validation or violates uniqueness.
 */
public class InvalidSupplierDataException extends RuntimeException {
    public InvalidSupplierDataException(String message) {
        super(message);
    }
}

