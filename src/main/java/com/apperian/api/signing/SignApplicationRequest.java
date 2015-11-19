package com.apperian.api.signing;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.apperian.api.ApperianResourceID;

import java.io.IOException;

public class SignApplicationRequest extends ApperianRequest {

    SignApplicationRequest(ApperianResourceID applicationId,
                           ApperianResourceID credentialId) {
        super(Type.PUT,
                "/applications/" +
                applicationId + "/credentials/" +
                        credentialId);
    }

    @Override
    public SignApplicationResponse call(ApperianEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, SignApplicationResponse.class);
    }
}
