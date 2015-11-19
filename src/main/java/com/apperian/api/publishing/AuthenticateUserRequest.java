package com.apperian.api.publishing;

import com.apperian.api.APIConstants;
import com.apperian.api.EASEEndpoint;
import com.apperian.api.EASERequest;

import java.io.IOException;

public class AuthenticateUserRequest extends EASERequest {
    public final Params params;

    public AuthenticateUserRequest(String email, String password) {
        super(APIConstants.AUTHENTICATE_USER_METHOD);
        this.params = new Params();
        this.params.email = email;
        this.params.password = password;
    }

    @Override
    public AuthenticateUserResponse call(EASEEndpoint endpoint) throws IOException {
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
