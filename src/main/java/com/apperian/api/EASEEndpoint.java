package com.apperian.api;

import com.apperian.api.publishing.AuthenticateUserResponse;
import com.apperian.api.publishing.Publishing;
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
    public final String url;

    public EASEEndpoint(String url) {
        this.url = url;
    }

    <T extends EASEResponse> T doJsonRpc(EASERequest request,
                                         Class<T> responseClass) throws IOException {

        HttpUriRequest httpRequest = buildJsonRpcPost(request);
        try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("bad API call, http status: " + response.getStatusLine() + ", request: " + httpRequest);
            }

            return buildResponseObject(responseClass, response);
        }
    }

    public UploadResult uploadFile(String uploadUrl, File file) throws IOException {
        HttpPost post = new HttpPost(uploadUrl);

        FileBody appFileBody = new FileBody(file);

        HttpEntity multipartEntity = MultipartEntityBuilder.create()
                .addPart("LUuploadFile", appFileBody)
                .build();


        post.setEntity(multipartEntity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            String body = EntityUtils.toString(response.getEntity());
            UploadResult result;
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                result = new UploadResult();
                result.errorMessage = body;
            } else {
                result = mapper.readValue(body, UploadResult.class);
            }
            return result;
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
    public boolean tryLogin(String email, String password) {
        AuthenticateUserResponse response;
        try {
            response = Publishing.API.authenticateUser(email, password)
                    .call(this);

            lastLoginError = response.getErrorMessage();

            if (response.hasError()) {
                return false;
            }

            sessionToken = response.result.token;

            return true;
        } catch (IOException e) {
            throw new RuntimeException("no network", e);
        }
    }
}
