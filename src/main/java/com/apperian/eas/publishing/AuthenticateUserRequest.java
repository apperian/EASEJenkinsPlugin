package com.apperian.eas.publishing;

import com.apperian.eas.APIConstants;
import com.apperian.eas.EASEEndpoint;
import com.apperian.eas.EASERequest;

import java.io.IOException;

public class AuthenticateUserRequest extends EASERequest {
    public final Params params;

    public AuthenticateUserRequest(String userId, String password) {
        super(APIConstants.AUTHENTICATE_USER_METHOD);
        this.params = new Params();
        this.params.userId = userId;
        this.params.password = password;
    }

    @Override
    public AuthenticateUserResponse call(EASEEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, AuthenticateUserResponse.class);
    }

    static class Params {
        public String userId;
        public String password;
    }

    @Override
    public String toString() {
        return "AuthenticateUserRequest{" +
                "email=" + params.userId +
                ", password=" + params.password +
                '}';
    }
}
