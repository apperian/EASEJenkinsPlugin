package com.apperian.eas;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jenkinsci.plugins.ease.Utils;

import java.io.Closeable;
import java.io.IOException;

public class JsonHttpEndpoint  implements Closeable {
    CloseableHttpClient httpClient =
            Utils.configureProxy(HttpClients.custom())
                    .build();

    ObjectMapper mapper = new ObjectMapper();
    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
}
