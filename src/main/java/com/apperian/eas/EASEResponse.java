package com.apperian.eas;

public class EASEResponse {
    long id;
    String jsonrpc;
    String apiVersion;
    JsonRpcError error;

    public long getId() {
        return id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public String getApiVersion() {
        return apiVersion;
    }

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
