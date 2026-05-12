package com.adrianh.batch.exception;

/**
 * Custom exception thrown when an error occurs during order processing.
 * <p>
 * This exception is used to signal validation or transformation failures
 * during the batch processing pipeline.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
public class OrderProcessingException extends RuntimeException {

    /**
     * Constructs a new OrderProcessingException with the specified detail message.
     *
     * @param message the detail message
     */
    public OrderProcessingException(String message) {
        super(message);
    }

    /**
     * Constructs a new OrderProcessingException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
