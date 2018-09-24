package com.apperian.api.signing;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.ConnectionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.io.IOException;
import java.util.List;

public class SignApplicationRequest extends ApperianRequest {

    SignApplicationRequest(ApperianResourceID applicationId,
                           ApperianResourceID credentialId) {
        super(Type.PUT,
                "/v1/applications/" +
                applicationId + "/credentials/" +
                        credentialId);
    }

    @Override
    protected void addEntityToRequest(ObjectMapper mapper,
                                      List<Header> headers,
                                      HttpEntityEnclosingRequestBase requestWithEntity)
            throws JsonProcessingException {
    }

    @Override
    public SignApplicationResponse call(ApperianEndpoint endpoint) throws ConnectionException {
        return doJsonRpc(endpoint, this, SignApplicationResponse.class);
    }
}
