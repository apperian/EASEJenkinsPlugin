package com.apperian.api;

import com.apperian.api.users.AuthenticateUserResponse;
import com.apperian.api.users.Users;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class ApperianEndpoint extends JsonHttpEndpoint {

    public final String url;

    public ApperianEndpoint(String url) {
        this.url = url;
    }

    <T extends ApperianResponse> T doJsonRpc(ApperianRequest request,
                                             Class<T> responseClass) throws IOException {

        HttpUriRequest httpRequest = request.buildHttpRequest(this, mapper);

        try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
            return request.buildResponseObject(mapper, responseClass, response);
        }
    }

    @Override
    public boolean tryLogin(String email, String password) {
        AuthenticateUserResponse response;
        try {
            response = Users.API.authenticateUser(email, password)
                    .call(this);

            lastLoginError = response.getErrorMessage();

            if (response.hasError()) {
                return false;
            }

            sessionToken = response.getToken();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("no network", e);
        }
    }
}
