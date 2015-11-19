package com.apperian.eas;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class AperianEndpoint extends JsonHttpEndpoint {

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

}
