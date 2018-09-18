package com.apperian.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
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

    public abstract ApperianResponse call(ApperianEndpoint endpoint) throws IOException;

    protected <T extends ApperianResponse> T doJsonRpc(ApperianEndpoint endpoint,
                                                       ApperianRequest request,
                                                       Class<T> responseClass) throws IOException {
        return endpoint.doJsonRpc(request, responseClass);
    }


    protected Object takeRequestJsonObject() {
        return this;
    }

    public HttpUriRequest buildHttpRequest(ApperianEndpoint endpoint, ObjectMapper mapper) {
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
                addEntityToRequest(mapper, headers, requestWithEntity);
            }

            if (endpoint.sessionToken == null) {
                throw new RuntimeException("bad session token");
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

    protected void addEntityToRequest(ObjectMapper mapper,
                                      List<Header> headers,
                                      HttpEntityEnclosingRequestBase requestWithEntity) throws JsonProcessingException {

        String requestAsString = mapper.writeValueAsString(takeRequestJsonObject());
        StringEntity entity = new StringEntity(requestAsString, APIConstants.REQUEST_CHARSET);
        headers.add(APIConstants.CONTENT_TYPE_JSON_HEADER);
        requestWithEntity.setEntity(entity);
    }

    public <T extends ApperianResponse> T buildResponseObject(ObjectMapper mapper, Class<T> responseClass, CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        return mapper.readValue(responseString, responseClass);
    }
}
