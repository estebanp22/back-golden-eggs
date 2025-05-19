package com.goldeneggs.Exception;

public class InvalidInventoryMovementDataException extends RuntimeException {
    public InvalidInventoryMovementDataException(String message) {
        super(message);
    }
}
