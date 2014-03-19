package com.apperian.eas;

import java.io.IOException;

public class AuthenticateUserRequest extends PublishingRequest {
    public static final String METHOD = "com.apperian.eas.user.authenticateuser";

    public final Params params;

    public AuthenticateUserRequest(String email, String password) {
        super(METHOD);
        this.params = new Params();
        this.params.email = email;
        this.params.password = password;
    }

    public Params getParams() {
        return params;
    }

    @Override
    public AuthenticateUserResponse call(PublishingAPI api) throws IOException {
        return api.doJsonRpc(this, AuthenticateUserResponse.class);
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
