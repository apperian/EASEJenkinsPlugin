package com.apperian.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ApperianRequest {
    public enum Type {
        GET, PUT, POST
    }

    private final Type type;
    private final String apiPath;

    public ApperianRequest(Type type, String apiPath) {
        this.type = type;
        this.apiPath = apiPath;
    }

    public Type getType() {
        return type;
    }

    public String getApiPath() {
        return apiPath;
    }

    protected <T extends ApperianResponse> T makeRequest(ApperianEndpoint endpoint,
                                                       Map<String, Object> data,
                                                       Class<T> responseClass) throws ConnectionException {
        return endpoint.makeRequest(this, data, responseClass);
    }

    protected <T extends ApperianResponse> T uploadFile(ApperianEndpoint endpoint,
                                                        String fileField,
                                                        File file,
                                                        Map<String, Object> data,
                                                        Class<T> responseClass) throws ConnectionException {
        return endpoint.uploadFile(this, fileField, file, data, responseClass);
    }

    public HttpUriRequest buildHttpRequest(ApperianEndpoint endpoint, ObjectMapper mapper, Map<String, Object> data) {
        return buildHttpRequest(endpoint, mapper, data, null, null);
    }

    public HttpUriRequest buildHttpRequest(ApperianEndpoint endpoint,
                                           ObjectMapper mapper,
                                           Map<String, Object> data,
                                           String file_field,
                                           File file) {
        HttpRequestBase request = null;
        switch (type) {
            case POST:
                request = new HttpPost(endpoint.url + apiPath);
                break;
            case PUT:
                request = new HttpPut(endpoint.url + apiPath);
                break;
            case GET:
                request = new HttpGet(endpoint.url + apiPath);
                break;
        }
        if (request == null) {
            throw new UnsupportedOperationException("http method " + type);
        }
        try {
            List<Header> headers = new ArrayList<>();
            if (request instanceof HttpEntityEnclosingRequestBase) {
                HttpEntityEnclosingRequestBase requestWithEntity;
                requestWithEntity = (HttpEntityEnclosingRequestBase) request;
                if (file == null) {
                    if (data != null) {
                        // Add the json data
                        String requestAsString = mapper.writeValueAsString(data);
                        StringEntity entity = new StringEntity(requestAsString, APIConstants.REQUEST_CHARSET);
                        requestWithEntity.setEntity(entity);
                        headers.add(APIConstants.CONTENT_TYPE_JSON_HEADER);
                    }
                } else {
                    // Multipart upload
                    String jsonData = mapper.writeValueAsString(data);

                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                    // Add the binary
                    FileBody appFileBody = new FileBody(file);
                    multipartEntityBuilder.addPart(file_field, appFileBody);

                    if (data != null) {
                        // Add the json data
                        StringBody jsonBody = new StringBody(jsonData, ContentType.MULTIPART_FORM_DATA);
                        multipartEntityBuilder.addPart("data", jsonBody);
                    }

                    HttpEntity multipartEntity = multipartEntityBuilder.build();

                    if (request instanceof HttpPut) {
                        ((HttpPut) request).setEntity(multipartEntity);
                    } else if (request instanceof HttpPost){
                        ((HttpPost) request).setEntity(multipartEntity);
                    } else {
                        throw new UnsupportedOperationException("Incorrect method for uploading a file");
                    }
                }
            }

            headers.add(new BasicHeader(APIConstants.X_TOKEN_HEADER, endpoint.sessionToken));

            if (!headers.isEmpty()) {
                request.setHeaders(headers.toArray(new Header[headers.size()]));
            }
        } catch(Exception ex) {
            throw new RuntimeException("Request marshaling error", ex);
        }
        return request;
    }

    public <T extends ApperianResponse> T buildResponseObject(ObjectMapper mapper, Class<T> responseClass, CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        return mapper.readValue(responseString, responseClass);
    }
}
