package com.example.gitservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final int statusCode;
    private final String reason;

    public ResourceNotFoundException(int statusCode, String reason) {
        this.statusCode = statusCode;
        this.reason = reason;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReason() {
        return reason;
    }
}
