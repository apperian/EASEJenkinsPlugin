package com.apperian.api.application;

import java.io.IOException;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.ConnectionException;

public class ApplicationListRequest extends ApperianRequest {
    public ApplicationListRequest() {
        super(Type.GET, "/v2/applications");
    }

    Boolean enabled;

    public ApplicationListRequest(ApperianResourceID applicationId) {
        super(Type.PUT, "/application/" + applicationId);
    }

    @Override
    public ApplicationListResponse call(ApperianEndpoint endpoint) throws ConnectionException {
        return doJsonRpc(endpoint, this, ApplicationListResponse.class);
    }
}
