package com.apperian.eas;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public abstract class PublishingRequest {
    private static final String JSON_RPC_VERSION = "2.0";
    private static final String API_VERSION = "1.0";
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    private final long id;
    private final String jsonrpc;
    private final String apiVersion;
    private final String method;

    public PublishingRequest(String method) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.jsonrpc = JSON_RPC_VERSION;
        this.apiVersion = API_VERSION;
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
