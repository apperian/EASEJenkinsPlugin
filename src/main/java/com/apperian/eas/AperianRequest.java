package com.apperian.eas;

import java.io.IOException;

public abstract class AperianRequest {
    public enum Type {
        GET, PUT, POST
    }

    private final Type type;
    private final String apiPath;
    private final String sessionToken;

    public AperianRequest(Type type, String apiPath) {
        this(type, apiPath, null);
    }

    public AperianRequest(Type type, String apiPath, String sessionToken) {
        this.type = type;
        this.apiPath = apiPath;
        this.sessionToken = sessionToken;
    }

    public Type getType() {
        return type;
    }

    public String getApiPath() {
        return apiPath;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public abstract AperianResponse call(AperianEndpoint endpoint) throws IOException;

    protected <T extends AperianResponse> T doJsonRpc(AperianEndpoint endpoint,
                                                      AperianRequest request,
                                                      Class<T> responseClass) throws IOException {
        return endpoint.doJsonRpc(request, responseClass);
    }
}
