package com.apperian.api.application;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.ConnectionException;

import java.io.IOException;

public class UpdateApplicationMetadataRequest extends ApperianRequest {
    Boolean enabled = null;

    public UpdateApplicationMetadataRequest(ApperianResourceID applicationId) {
        super(Type.PUT, "/v2/applications/" + applicationId);
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public UpdateApplicationMetadataRequest setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }


    @Override
    public UpdateApplicationMetadataResponse call(ApperianEndpoint endpoint) throws ConnectionException {
        return doJsonRpc(endpoint, this, UpdateApplicationMetadataResponse.class);
    }
}
