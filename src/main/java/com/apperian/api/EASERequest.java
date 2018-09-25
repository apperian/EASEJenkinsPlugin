package com.apperian.api;

import java.io.IOException;

public abstract class EASERequest {
    private final long id;
    private final String jsonrpc;
    private final String apiVersion;
    private final String method;

    public EASERequest(String method) {
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

    public abstract EASEResponse call(EASEEndpoint endpoint) throws ConnectionException;

    protected <T extends EASEResponse> T doJsonRpc(EASEEndpoint endpoint,
                                                   EASERequest request,
                                                   Class<T> responseClass) throws ConnectionException {
        return endpoint.doJsonRpc(request, responseClass);
    }
}
