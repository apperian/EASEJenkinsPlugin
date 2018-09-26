package com.apperian.api;

import com.apperian.api.users.UserInfoRequest;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.lang.UnsupportedOperationException;
import java.util.Map;

public class ApperianEndpoint extends JsonHttpEndpoint {
    public ApperianEndpoint(String url, String sessionToken) {
        super(url);
        this.sessionToken = sessionToken;
    }

    protected <T extends ApperianResponse> T makeRequest(ApperianRequest request,
                                               Map<String, Object> data,
                                               Class<T> responseClass) throws ConnectionException {

        try {
            HttpUriRequest httpRequest = request.buildHttpRequest(this, mapper, data);
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
            throw new ConnectionException("No connection", e);
        }
    }

    protected <T extends ApperianResponse> T uploadFile(ApperianRequest request,
                                                     String file_field,
                                                     File file,
                                                     Map<String, Object> data,
                                                     Class<T> responseClass) throws ConnectionException {
        try {

            HttpUriRequest httpRequest = request.buildHttpRequest(this, mapper, data, file_field, file);

            CloseableHttpResponse response = httpClient.execute(httpRequest);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                String body = EntityUtils.toString(response.getEntity());
                throw new ConnectionException("Error uploading binary. Result code: " + statusCode + ". Body: " + body);
            }
            return request.buildResponseObject(mapper, responseClass, response);
        } catch (IOException e) {
            throw new ConnectionException("No connection", e);
        }
    }

    public void checkSessionToken() throws ConnectionException {
        try {
            UserInfoRequest request = new UserInfoRequest();

            HttpUriRequest httpRequest = request.buildHttpRequest(this, mapper, null);

            CloseableHttpResponse response = httpClient.execute(httpRequest);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                throw new ConnectionException("No access");
            }

        } catch (IOException e) {
            throw new ConnectionException("No connection", e);
        }
    }

}
