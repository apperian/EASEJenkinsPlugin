package com.apperian.api.application;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.ConnectionException;
import com.apperian.api.metadata.Metadata;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class UpdateApplicationRequest extends ApperianRequest {

    public UpdateApplicationRequest(ApperianResourceID applicationId) {
        super(Type.POST, "/v1/applications/" + applicationId);
    }

    public UpdateApplicationResponse call(ApperianEndpoint endpoint, Map<String, Object> data, File appBinary) throws ConnectionException {
        return uploadFile(endpoint, "app_file", appBinary, data, UpdateApplicationResponse.class);
    }

    public UpdateApplicationResponse call(ApperianEndpoint endpoint, Map<String, Object> data) throws ConnectionException {
        return makeRequest(endpoint, data, UpdateApplicationResponse.class);
    }
}
