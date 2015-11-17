package com.apperian.eas.users;

import com.apperian.eas.AperianEndpoint;
import com.apperian.eas.AperianRequest;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.IOException;

public class AuthenticateUserRequest extends AperianRequest {
    private final Params params;

    AuthenticateUserRequest(String email, String password) {
        super(Type.POST, "/users/authenticate");
        params = new Params();
        params.user_id = email;
        params.password = password;
    }

    @Override
    public AuthenticateUserResponse call(AperianEndpoint endpoint) throws IOException {
        return doJsonRpc(endpoint, this, AuthenticateUserResponse.class);
    }

    static class Params {
        public String user_id;
        public String password;
    }

    @Override
    protected Header[] takeHttpHeaders() {
        return new Header[] { new BasicHeader("Content-Type", "application/json") };
    }

    @Override
    protected Object takeRequestJsonObject() {
        return params;
    }

    @Override
    public String toString() {
        return "AuthenticateUserRequest{" +
                "email=" + params.user_id +
                ", password=" + params.password +
                '}';
    }
}
