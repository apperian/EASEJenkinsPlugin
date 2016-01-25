package com.apperian.api.application;

import java.io.IOException;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.apperian.api.ApperianResourceID;

public class GetApplicationInfoRequest extends ApperianRequest {
    public GetApplicationInfoRequest(ApperianResourceID applicationId) {
        super(Type.GET, "/applications/" + applicationId);
    }

    @Override
    public GetApplicationInfoResponse call(ApperianEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, GetApplicationInfoResponse.class);
    }
}
