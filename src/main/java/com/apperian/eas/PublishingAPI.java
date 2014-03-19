package com.apperian.eas;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class PublishingAPI {
    public static final String REQUEST_CHARSET = "UTF-8";
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private ObjectMapper mapper = new ObjectMapper();

    public final String url;

    public PublishingAPI(String url) {
        this.url = url;
    }

    <T extends PublishingResponse> T doJsonRpc(PublishingRequest request,
                                                              Class<T> responseClass) throws IOException {

        HttpPost post = buildJsonRpcPost(request);
        CloseableHttpResponse response = httpClient.execute(post);
        try {
            return buildResponseObject(responseClass, response);
        } finally {
            response.close();
        }
    }

    private <T extends PublishingResponse> T buildResponseObject(Class<T> responseClass, CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        return mapper.readValue(responseString, responseClass);
    }

    private HttpPost buildJsonRpcPost(PublishingRequest request) {
        HttpPost post = new HttpPost(url);
        try {
            String requestStr = mapper.writeValueAsString(request);
            post.setEntity(new StringEntity(requestStr, REQUEST_CHARSET));
        } catch(Exception ex) {
            throw new RuntimeException("Request marshaling error", ex);
        }
        return post;
    }
}
