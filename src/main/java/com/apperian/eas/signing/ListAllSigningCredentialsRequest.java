package com.apperian.eas.signing;


import com.apperian.eas.ApperianEndpoint;
import com.apperian.eas.ApperianRequest;

import java.io.IOException;

public class ListAllSigningCredentialsRequest extends ApperianRequest {
    public ListAllSigningCredentialsRequest() {
        super(Type.GET, "/credentials");
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
