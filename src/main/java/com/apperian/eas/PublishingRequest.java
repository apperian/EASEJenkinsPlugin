package com.apperian.eas;

import java.io.IOException;

public abstract class PublishingRequest {
    private final long id;
    private final String jsonrpc;
    private final String apiVersion;
    private final String method;

    public PublishingRequest(String method) {
        this.id = APIConstants.ID_GENERATOR.incrementAndGet();
        this.jsonrpc = APIConstants.JSON_RPC_VERSION;
        this.apiVersion = APIConstants.API_VERSION;
        this.method = method;
    }

    public long getId() {
        return id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getMethod() {
        return method;
    }

    public abstract PublishingResponse call(PublishingAPI api) throws IOException;
}
