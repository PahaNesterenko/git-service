package com.example.gitservice.exception;

public class GitServiceCommonException extends RuntimeException {

    private final int statusCode;
    private final String reason;

    public GitServiceCommonException(int statusCode, String reason) {
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
