package com.apperian.api;

public class ConnectionException extends Exception{
    private static final long serialVersionUID = 1L;

    private String errorDetails;

    public ConnectionException(String message) {
        super(message);
        this.errorDetails = null;
    }

    public ConnectionException(String message, String errorDetails) {
        super(message);
        this.errorDetails = errorDetails;
    }

    public ConnectionException(String message, Throwable e) {
        super(message, e);
        this.errorDetails = null;
    }

    public ConnectionException(String message, String errorDetails, Throwable e) {
        super(message, e);
        this.errorDetails = errorDetails;
    }

    public String getErrorDetails() {
        return errorDetails;
    }
}
