package com.apperian.api;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
import org.jenkinsci.plugins.apperian.Utils;

public class ApiClient {

    private String baseUrl;

    private String sessionToken;

    private CloseableHttpClient httpClient;

    private ObjectMapper mapper;

    // By default GET
    private RequestMethod method = RequestMethod.GET;

    private String path = null;

    private Map<String, Object> data = null;

    private String fileField = null;

    private File file = null;

    private ApiClient(String baseUrl, String sessionToken) {
        this.baseUrl = baseUrl;
        this.sessionToken = sessionToken;
        httpClient = Utils.configureProxy(HttpClients.custom()).build();
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static class Builder {

        private ApiClient apiClient;

        public Builder(String baseUrl, String sessionToken) {
            apiClient = new ApiClient(baseUrl, sessionToken);
        }

        public Builder withMethod(RequestMethod method) {
            apiClient.method = method;
            return this;
        }

        public Builder withPath(String path, String... arguments) {
            if (arguments.length > 0) {
                String[] encodedArguments = new String[arguments.length];
                int i = 0;
                for (String arg : arguments) {
                    try {
                        encodedArguments[i] = URLEncoder.encode(arg, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("Error encoding parameter in path " + path);
                    }
                    i++;
                }
                apiClient.path = String.format(path, (Object[]) encodedArguments);
            } else {
                apiClient.path = path;
            }
            return this;
        }

        public Builder withData(Map<String, Object> data) {
            apiClient.data = data;
            return this;
        }

        public Builder withFile(String fileField, File file) {
            apiClient.fileField = fileField;
            apiClient.file = file;
            return this;
        }

        public ApiClient build() {
            return apiClient;
        }
    }

    public <T> T makeRequest(Class<T> responseClass) throws ConnectionException {

        try {
            HttpUriRequest httpRequest = buildHttpRequest();
            CloseableHttpResponse response = httpClient.execute(httpRequest);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 401) {
                throw new ConnectionException("No access", retrieveErrorDetails(httpRequest, response));
            }
            if (statusCode != 200 && statusCode != 202) {
                throw new ConnectionException("Error in request", retrieveErrorDetails(httpRequest, response));
            }

            return buildResponseObject(responseClass, response);
        }
        catch (IOException e) {
            throw new ConnectionException("No connection", e);
        }
    }

    private String retrieveErrorDetails(HttpUriRequest httpRequest, CloseableHttpResponse response) {
        String errorDetails = "Bad API call.\n Http status: %d.\n Request: %s.\n Response: %s";
        int httpStatus = response.getStatusLine().getStatusCode();
        String responseAsString = null;
        try {
            responseAsString = getResponseAsString(response);
        }
        catch (IOException e) {
        }
        return String.format(errorDetails, httpStatus, httpRequest, responseAsString);
    }

    private HttpUriRequest buildHttpRequest() throws ConnectionException {
        HttpRequestBase request = null;

        String url;
        try {
            URL apiURL = new URL(baseUrl);
            url = new URL(apiURL, path).toString();

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
                if (fileField == null) {
                    if (data != null) {
                        // Add the json data
                        String requestAsString = mapper.writeValueAsString(data);
                        StringEntity entity = new StringEntity(requestAsString, ApiConstants.REQUEST_CHARSET);
                        requestWithEntity.setEntity(entity);
                        BasicHeader jsonHeader = new BasicHeader(ApiConstants.CONTENT_TYPE_HEADER,
                                                                 ApiConstants.JSON_CONTENT_TYPE);
                        headers.add(jsonHeader);
                    }
                }
                else {
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
                    }
                    else if (request instanceof HttpPost){
                        ((HttpPost) request).setEntity(multipartEntity);
                    }
                    else {
                        throw new UnsupportedOperationException("Incorrect method for uploading a file");
                    }
                }
            }

            headers.add(new BasicHeader(ApiConstants.X_TOKEN_HEADER, sessionToken));

            if (!headers.isEmpty()) {
                request.setHeaders(headers.toArray(new Header[headers.size()]));
            }
        } catch(Exception ex) {
            throw new RuntimeException("Request marshaling error", ex);
        }
        return request;
    }

    private <T> T buildResponseObject(Class<T> responseClass, CloseableHttpResponse response) throws IOException {
        String responseString = getResponseAsString(response);
        return mapper.readValue(responseString, responseClass);
    }

    private String getResponseAsString(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        return responseString;
    }

}
