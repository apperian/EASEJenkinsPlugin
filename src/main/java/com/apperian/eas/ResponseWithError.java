package com.apperian.eas;

public class ResponseWithError {
    JsonRpcError error;

    public JsonRpcError getError() {
        return error;
    }

    public boolean hasError() {
        return getErrorMessage() != null;
    }

    public String getErrorMessage() {
        if (error == null) {
            return null;
        }

        return error.getErrorMessage();
    }

    public void appendError(String message) {
        if (error == null) {
            return;
        }
        error.appendDetailedMessage(message);
    }
}
