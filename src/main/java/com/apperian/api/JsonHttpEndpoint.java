package com.apperian.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jenkinsci.plugins.ease.Utils;

import java.io.Closeable;
import java.io.IOException;

public abstract class JsonHttpEndpoint  implements Closeable {
    public final String url;
    CloseableHttpClient httpClient =
            Utils.configureProxy(HttpClients.custom())
                    .build();

    ObjectMapper mapper = new ObjectMapper();
    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected String sessionToken;
    protected String lastLoginError;

    public JsonHttpEndpoint(String url) {
        this.url = url;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getLastLoginError() {
        return lastLoginError;
    }

    public void setLastLoginError(String lastLoginError) {
        this.lastLoginError = lastLoginError;
    }

    protected CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    protected ObjectMapper getMapper() {
        return mapper;
    }

    public void close() throws IOException {
        httpClient.close();
    }

    public boolean isLoggedIn() {
        return sessionToken != null;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return url.toString();
    }
}
