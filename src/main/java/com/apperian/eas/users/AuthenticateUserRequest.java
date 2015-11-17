package com.apperian.eas.users;

import com.apperian.eas.AperianEndpoint;
import com.apperian.eas.AperianRequest;

import java.io.IOException;

public class AuthenticateUserRequest extends AperianRequest {
    private final Params params;

    AuthenticateUserRequest(String email, String password) {
        super(Type.POST, "/users/authenticate");
        params = new Params();
        params.email = email;
        params.password = password;
    }

    @Override
    public AuthenticateUserResponse call(AperianEndpoint endpoint) throws IOException {
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
