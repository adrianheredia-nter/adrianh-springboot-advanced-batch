package com.adrianh.batch.exception;

/**
 * Custom exception thrown when an order fails validation.
 * <p>
 * This exception is raised when an order has invalid data such as
 * a non-positive total amount, missing fields, or malformed dates.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
public class InvalidOrderException extends RuntimeException {

    /**
     * Constructs a new InvalidOrderException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidOrderException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidOrderException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public InvalidOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
