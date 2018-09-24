package com.apperian.api;

import com.apperian.api.publishing.ApplicationListRequest;
import com.apperian.api.publishing.UploadResult;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

public class EASEEndpoint extends JsonHttpEndpoint {
    public EASEEndpoint(String url, String sessionToken) {
        super(url);
        this.sessionToken = sessionToken;
    }

    <T extends EASEResponse> T doJsonRpc(EASERequest request,
                                         Class<T> responseClass) throws ConnectionException {

        try{
            HttpUriRequest httpRequest = buildJsonRpcPost(request);
            CloseableHttpResponse response = httpClient.execute(httpRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new ConnectionException("bad API call, http status: " + response.getStatusLine() +
                                              ", request: " + httpRequest);
            }

            return buildResponseObject(responseClass, response);
        } catch (IOException e) {
            throw new ConnectionException("No network", e);
        }
    }

    public UploadResult uploadFile(String uploadUrl, File file) throws ConnectionException {
        try {
            HttpPost post = new HttpPost(uploadUrl);

            FileBody appFileBody = new FileBody(file);

            HttpEntity multipartEntity = MultipartEntityBuilder.create()
                    .addPart("LUuploadFile", appFileBody)
                    .build();


            post.setEntity(multipartEntity);

            CloseableHttpResponse response = httpClient.execute(post);
            String body = EntityUtils.toString(response.getEntity());
            UploadResult result;
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new ConnectionException("Error uploading binary. Result code: " + statusCode + ". Body: " + body);
            } else {
                result = mapper.readValue(body, UploadResult.class);
            }
            return result;
        } catch (IOException e) {
            throw new ConnectionException("No network", e);
        }
    }

    private <T extends EASEResponse> T buildResponseObject(Class<T> responseClass, CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        return mapper.readValue(responseString, responseClass);
    }

    private HttpPost buildJsonRpcPost(EASERequest request) {
        HttpPost post = new HttpPost(url);
        try {
            String requestStr = mapper.writeValueAsString(request);
            post.setEntity(new StringEntity(requestStr, APIConstants.REQUEST_CHARSET));
        } catch(Exception ex) {
            throw new RuntimeException("Request marshaling error", ex);
        }
        return post;
    }

    @Override
    public void checkSessionToken() throws ConnectionException {
        try {
            ApplicationListRequest request = new ApplicationListRequest();

            HttpUriRequest httpRequest = buildJsonRpcPost(request);
            try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new ConnectionException("No access");
                }
            }
        } catch (IOException e) {
            throw new ConnectionException("No network", e);
        }
    }
}
