package com.apperian.api.application;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;

import java.io.IOException;

public class ApplicationListRequest extends ApperianRequest {
    public ApplicationListRequest() {
        super(Type.GET, "/applications");
    }

    @Override
    public ApplicationListResponse call(ApperianEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, ApplicationListResponse.class);
    }
}
