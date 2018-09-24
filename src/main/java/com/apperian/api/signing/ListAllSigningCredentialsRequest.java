package com.apperian.api.signing;


import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;

import java.io.IOException;

public class ListAllSigningCredentialsRequest extends ApperianRequest {
    public ListAllSigningCredentialsRequest() {
        super(Type.GET, "/v1/credentials");
    }

    @Override
    public ListAllSigningCredentialsResponse call(ApperianEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, ListAllSigningCredentialsResponse.class);
    }

    @Override
    public String toString() {
        return "ListAllSigningCredentialsRequest{" +
                "type=" + getType() +
                ", apiPath='" + getApiPath() + '\'' +
                '}';
    }
}
