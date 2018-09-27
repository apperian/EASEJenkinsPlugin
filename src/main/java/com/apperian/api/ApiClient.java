package com.apperian.api;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jenkinsci.plugins.ease.Utils;

public class ApiClient {

    private String baseUrl;

    private String sessionToken;

    private CloseableHttpClient httpClient;

    private ObjectMapper mapper;

    public ApiClient(String baseUrl, String sessionToken) {
        this.baseUrl = baseUrl;
        this.sessionToken = sessionToken;
        httpClient = Utils.configureProxy(HttpClients.custom()).build();
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> T makeRequest(RequestDetails requestDetails, Class<T> responseClass) throws ConnectionException {

        try {
            HttpUriRequest httpRequest = buildHttpRequest(requestDetails);
            CloseableHttpResponse response = httpClient.execute(httpRequest);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 401) {
                throw new ConnectionException("No access");
            }
            if (statusCode != 200) {
                throw new RuntimeException("bad API call, http status: " + response.getStatusLine() + ", request: " + httpRequest);
            }

            return buildResponseObject(responseClass, response);
        } catch (IOException e) {
            throw new ConnectionException("No connection", e);
        }
    }

    private HttpUriRequest buildHttpRequest(RequestDetails requestDetails) throws ConnectionException {
        HttpRequestBase request = null;
        RequestMethod method = requestDetails.getMethod();
        String urlPath = requestDetails.getPath();
        Map<String, Object> data = requestDetails.getData();
        String fileField = requestDetails.getFileField();
        File file = requestDetails.getFile();

        String url;
        try {
            URL apiURL = new URL(baseUrl);
            url = new URL(apiURL, urlPath).toString();

        } catch (MalformedURLException e) {
            throw new ConnectionException("Malformed URL for the API: " + baseUrl);
        }

        switch (method) {
            case GET:
                request = new HttpGet(url);
                break;
            case POST:
                request = new HttpPost(url);
                break;
            case PUT:
                request = new HttpPut(url);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported operation: " + method);
        }

        try {
            List<Header> headers = new ArrayList<>();
            if (request instanceof HttpEntityEnclosingRequestBase) {
                HttpEntityEnclosingRequestBase requestWithEntity;
                requestWithEntity = (HttpEntityEnclosingRequestBase) request;
                if (file == null) {
                    if (data != null) {
                        // Add the json data
                        String requestAsString = mapper.writeValueAsString(data);
                        StringEntity entity = new StringEntity(requestAsString, APIConstants.REQUEST_CHARSET);
                        requestWithEntity.setEntity(entity);
                        BasicHeader jsonHeader = new BasicHeader(APIConstants.CONTENT_TYPE_HEADER,
                                                                 APIConstants.JSON_CONTENT_TYPE);
                        headers.add(jsonHeader);
                    }
                } else {
                    // Multipart upload
                    String jsonData = mapper.writeValueAsString(data);

                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                    // Add the binary
                    FileBody appFileBody = new FileBody(file);
                    multipartEntityBuilder.addPart(fileField, appFileBody);

                    if (data != null) {
                        // Add the json data
                        StringBody jsonBody = new StringBody(jsonData, ContentType.MULTIPART_FORM_DATA);
                        multipartEntityBuilder.addPart("data", jsonBody);
                    }

                    HttpEntity multipartEntity = multipartEntityBuilder.build();

                    if (request instanceof HttpPut) {
                        ((HttpPut) request).setEntity(multipartEntity);
                    } else if (request instanceof HttpPost){
                        ((HttpPost) request).setEntity(multipartEntity);
                    } else {
                        throw new UnsupportedOperationException("Incorrect method for uploading a file");
                    }
                }
            }

            headers.add(new BasicHeader(APIConstants.X_TOKEN_HEADER, sessionToken));

            if (!headers.isEmpty()) {
                request.setHeaders(headers.toArray(new Header[headers.size()]));
            }
        } catch(Exception ex) {
            throw new RuntimeException("Request marshaling error", ex);
        }
        return request;
    }

    private <T> T buildResponseObject(Class<T> responseClass, CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        return mapper.readValue(responseString, responseClass);
    }

}
