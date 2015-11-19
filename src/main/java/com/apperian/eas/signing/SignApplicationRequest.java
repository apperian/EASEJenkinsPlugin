package com.apperian.eas.signing;

import com.apperian.eas.ApperianEndpoint;
import com.apperian.eas.ApperianRequest;
import com.apperian.eas.ApperianResourceID;

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
