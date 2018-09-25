package com.apperian.api.application;

import java.io.IOException;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.ConnectionException;

public class GetApplicationInfoRequest extends ApperianRequest {
    public GetApplicationInfoRequest(ApperianResourceID applicationId) {
        super(Type.GET, "/v2/applications/" + applicationId);
    }

    @Override
    public GetApplicationInfoResponse call(ApperianEndpoint endpoint) throws ConnectionException {
        return doJsonRpc(endpoint, this, GetApplicationInfoResponse.class);
    }
}
