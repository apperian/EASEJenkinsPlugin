package com.apperian.api.users;

import java.io.IOException;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.apperian.api.ApperianResourceID;
import com.apperian.api.ApperianResponse;

public class UserInfoRequest extends ApperianRequest {
    public UserInfoRequest() {
        super(Type.GET, "/users/info");
    }

    @Override
    public ApperianResponse call(ApperianEndpoint endpoint) throws IOException {
        // For now we just use this request to check the API tokens, so we are only interested in the response code of
        // this request, so we do not implement this method for now as it won't be used.
        throw UnsupportedOperationException();
    }
}
