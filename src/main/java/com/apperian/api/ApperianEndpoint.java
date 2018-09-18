package com.apperian.api;

import com.apperian.api.users.UserInfoRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class ApperianEndpoint extends JsonHttpEndpoint {
    public ApperianEndpoint(String url) {
        super(url);
    }

    <T extends ApperianResponse> T doJsonRpc(ApperianRequest request,
                                             Class<T> responseClass) throws IOException {

        HttpUriRequest httpRequest = request.buildHttpRequest(this, mapper);

        try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 401) {
                throw new RuntimeException("No access");
            }
            if (statusCode != 200) {
                throw new RuntimeException("bad API call, http status: " + response.getStatusLine() + ", request: " + httpRequest);
            }

            return request.buildResponseObject(mapper, responseClass, response);
        }
    }


    @Override
    public void checkSessionToken(String sessionToken) {
        try {
            UserInfoRequest request = new UserInfoRequest();

            HttpUriRequest httpRequest = request.buildHttpRequest(this, mapper);

            CloseableHttpResponse response = httpClient.execute(httpRequest);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                throw new RuntimeException("No access");
            }

            // Set the token as it is valid
            this.sessionToken = sessionToken;

        } catch (IOException e) {
            throw new RuntimeException("no network", e);
        }
    }

}
