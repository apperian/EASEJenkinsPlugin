package com.apperian.api;

public class EASEResponse extends ResponseWithError {
    long id;
    String jsonrpc;
    String apiVersion;

    public long getId() {
        return id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public String getApiVersion() {
        return apiVersion;
    }
}
