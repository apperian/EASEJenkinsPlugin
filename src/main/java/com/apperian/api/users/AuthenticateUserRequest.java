package com.apperian.api.users;

import com.apperian.api.ApperianEndpoint;
import com.apperian.api.ApperianRequest;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;

public class AuthenticateUserRequest extends ApperianRequest {
    private final Params params;

    AuthenticateUserRequest(String email, String password) {
        super(Type.POST, "/users/authenticate");
        params = new Params();
        params.userId = email;
        params.password = password;
    }

    @Override
    public AuthenticateUserResponse call(ApperianEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, AuthenticateUserResponse.class);
    }

    static class Params {
        @JsonProperty("user_id")
        public String userId;

        public String password;
    }

    @Override
    protected Object takeRequestJsonObject() {
        return params;
    }

    @Override
    public String toString() {
        return "AuthenticateUserRequest{" +
                "email=" + params.userId +
                ", password=" + params.password +
                '}';
    }
}
