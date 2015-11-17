package com.apperian.eas;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
        throw new UnsupportedOperationException("doJsonRpc"); // TODO
//        HttpPost post = buildJsonRpcPost(request);
//        CloseableHttpResponse response = httpClient.execute(post);
//        try {
//            return buildResponseObject(responseClass, response);
//        } finally {
//            response.close();
//        }
    }

//    private <T extends PublishingResponse> T buildResponseObject(Class<T> responseClass, CloseableHttpResponse response) throws IOException {
//        HttpEntity entity = response.getEntity();
//        String responseString = EntityUtils.toString(entity);
//        return mapper.readValue(responseString, responseClass);
//    }
//
//    private HttpUriRequest buildJsonRpcPost(AperianRequest request) {
//        HttpPost post = new HttpPost(url + request.getApiPath());
//        try {
//            String requestStr = mapper.writeValueAsString(request);
//            post.setEntity(new StringEntity(requestStr, APIConstants.REQUEST_CHARSET));
//        } catch(Exception ex) {
//            throw new RuntimeException("Request marshaling error", ex);
//        }
//        return post;
//    }

    public void close() throws IOException {
        httpClient.close();
    }
}
