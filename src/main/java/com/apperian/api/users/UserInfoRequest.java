package com.apperian.api.users;

import java.io.IOException;
import java.lang.UnsupportedOperationException;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.apperian.api.ApperianResponse;
import com.apperian.api.ConnectionException;

public class UserInfoRequest extends ApperianRequest {
    public UserInfoRequest() {
        super(Type.GET, "/v2/users/info");
    }
}
