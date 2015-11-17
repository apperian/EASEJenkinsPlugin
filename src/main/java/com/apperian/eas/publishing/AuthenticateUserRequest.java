package com.apperian.eas.publishing;

import com.apperian.eas.APIConstants;
import com.apperian.eas.PublishingEndpoint;
import com.apperian.eas.PublishingRequest;

import java.io.IOException;

public class AuthenticateUserRequest extends PublishingRequest {
    public final Params params;

    public AuthenticateUserRequest(String email, String password) {
        super(APIConstants.AUTHENTICATE_USER_METHOD);
        this.params = new Params();
        this.params.email = email;
        this.params.password = password;
    }

    @Override
    public AuthenticateUserResponse call(PublishingEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, AuthenticateUserResponse.class);
    }

    static class Params {
        public String email;
        public String password;
    }

    @Override
    public String toString() {
        return "AuthenticateUserRequest{" +
                "email=" + params.email +
                ", password=" + params.password +
                '}';
    }
}
