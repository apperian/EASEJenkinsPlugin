package com.apperian.api.users;

import com.apperian.api.ApperianResponse;
import com.apperian.api.JsonRpcError;

import java.util.Collections;

public class AuthenticateUserResponse extends ApperianResponse {
    String token;
    // User user;
    // Organization organization;

    public String getToken() {
        return token;
    }

    @Override
    public String getErrorMessage() {
        if (token == null) {
            return "No access";
        }
        return super.getErrorMessage();
    }

    @Override
    public String toString() {
        return "AuthenticateUserResponse{" +
                "token='" + token + '\'' +
                (hasError() ? ", error='" + getError() + '\'' : "") +
                '}';
    }

    public static AuthenticateUserResponse buildNoAccessResponse() {
        AuthenticateUserResponse response = new AuthenticateUserResponse();
        response.error = new JsonRpcError(401, "No access", Collections.<String, Object>emptyMap());
        return response;
    }
}
