package com.goldeneggs.Exception;

public class InvalidBillDataException extends RuntimeException {
    public InvalidBillDataException(String message) {
        super(message);
    }
}
