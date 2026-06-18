package com.moviebooking.shared.exception;

/**
 * Custom exception for business logic violations
 */
public class BusinessException extends RuntimeException {
    private String errorCode;
    private String message;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
