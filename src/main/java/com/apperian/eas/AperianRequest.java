package com.apperian.eas;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public abstract class AperianRequest {
    public enum Type {
        GET, PUT, POST
    }

    private final Type type;
    private final String apiPath;
    private final String sessionToken;

    public AperianRequest(Type type, String apiPath) {
        this(type, apiPath, null);
    }

    public AperianRequest(Type type, String apiPath, String sessionToken) {
        this.type = type;
        this.apiPath = apiPath;
        this.sessionToken = sessionToken;
    }

    public Type getType() {
        return type;
    }

    public String getApiPath() {
        return apiPath;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public abstract AperianResponse call(AperianEndpoint endpoint) throws IOException;

    protected <T extends AperianResponse> T doJsonRpc(AperianEndpoint endpoint,
                                                      AperianRequest request,
                                                      Class<T> responseClass) throws IOException {
        return endpoint.doJsonRpc(request, responseClass);
    }


    protected Object takeRequestJsonObject() {
        return this;
    }

    protected Header[] takeHttpHeaders() {
        return new Header[0];
    }

    public HttpUriRequest buildHttpRequest(String endpointUrl, ObjectMapper mapper) {
        HttpEntityEnclosingRequestBase post = null;
        switch (type) {
            case POST:
                post = new HttpPost(endpointUrl + apiPath);
                break;
        }
        if (post == null) {
            throw new UnsupportedOperationException("type=" + type);
        }
        try {
            String requestAsString = mapper.writeValueAsString(takeRequestJsonObject());
            post.setEntity(new StringEntity(requestAsString, APIConstants.REQUEST_CHARSET));
            Header[] headers = takeHttpHeaders();
            if (headers != null) {
                post.setHeaders(headers);
            }
        } catch(Exception ex) {
            throw new RuntimeException("Request marshaling error", ex);
        }
        return post;
    }

    public <T extends AperianResponse> T buildResponseObject(ObjectMapper mapper, Class<T> responseClass, CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        System.out.println(responseString); // FIXME
        return mapper.readValue(responseString, responseClass);
    }
}
