package com.apperian.eas;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jenkinsci.plugins.ease.Utils;

import java.io.Closeable;
import java.io.IOException;

public class AperianEndpoint implements Closeable {
    private CloseableHttpClient httpClient =
            Utils.configureProxy(HttpClients.custom())
                 .build();

    private ObjectMapper mapper = new ObjectMapper();
    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public final String url;

    public AperianEndpoint(String url) {
        this.url = url;
    }

    <T extends AperianResponse> T doJsonRpc(AperianRequest request,
                                            Class<T> responseClass) throws IOException {

        HttpUriRequest httpRequest = request.buildHttpRequest(url, mapper);

        try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
            return request.buildResponseObject(mapper, responseClass, response);
        }
    }

    public void close() throws IOException {
        httpClient.close();
    }
}
