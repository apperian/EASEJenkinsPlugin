package com.apperian.api;

import com.apperian.api.users.UserInfoRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class ApperianEndpoint extends JsonHttpEndpoint {
    public ApperianEndpoint(String url, String sessionToken) {
        super(url);
        this.sessionToken = sessionToken;
    }

    <T extends ApperianResponse> T doJsonRpc(ApperianRequest request,
                                             Class<T> responseClass) throws ConnectionException {

        try {
            HttpUriRequest httpRequest = request.buildHttpRequest(this, mapper);
            CloseableHttpResponse response = httpClient.execute(httpRequest);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 401) {
                throw new ConnectionException("No access");
            }
            if (statusCode != 200) {
                throw new RuntimeException("bad API call, http status: " + response.getStatusLine() + ", request: " + httpRequest);
            }

            return request.buildResponseObject(mapper, responseClass, response);
        } catch (IOException e) {
            throw new ConnectionException("No network", e);
        }
    }


    @Override
    public void checkSessionToken() throws ConnectionException {
        try {
            UserInfoRequest request = new UserInfoRequest();

            HttpUriRequest httpRequest = request.buildHttpRequest(this, mapper);

            CloseableHttpResponse response = httpClient.execute(httpRequest);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                throw new ConnectionException("No access");
            }

        } catch (IOException e) {
            throw new ConnectionException("No network", e);
        }
    }

}
