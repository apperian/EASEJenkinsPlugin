package com.apperian.api.signing;


import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.apperian.api.ConnectionException;

import java.io.IOException;

public class ListAllSigningCredentialsRequest extends ApperianRequest {
    public ListAllSigningCredentialsRequest() {
        super(Type.GET, "/v1/credentials");
    }

    public ListAllSigningCredentialsResponse call(ApperianEndpoint endpoint) throws ConnectionException {
        return makeRequest(endpoint, null, ListAllSigningCredentialsResponse.class);
    }

    @Override
    public String toString() {
        return "ListAllSigningCredentialsRequest{" +
                "type=" + getType() +
                ", apiPath='" + getApiPath() + '\'' +
                '}';
    }
}
