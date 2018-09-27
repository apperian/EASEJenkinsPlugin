package com.apperian.api;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.apperian.api.application.Application;
import com.apperian.api.application.ApplicationListResponse;
import com.apperian.api.application.GetApplicationInfoResponse;
import com.apperian.api.application.UpdateApplicationResponse;
import com.apperian.api.signing.ListAllSigningCredentialsResponse;
import com.apperian.api.signing.SignApplicationResponse;
import com.apperian.api.signing.SigningCredential;
import com.apperian.api.users.UserInfoResponse;

public class ApperianApi {

    private ApiClient apiClient;

    public ApperianApi(String baseUrl, String sessionToken) {
        this.apiClient = new ApiClient(baseUrl, sessionToken);
    }

    public List<Application> listApplications() throws ConnectionException {
        RequestDetails requestDetails = new RequestDetails.Builder()
            .withPath(APIConstants.LIST_APPS_URL_PATH)
            .build();
        return apiClient.makeRequest(requestDetails, ApplicationListResponse.class).getApplications();
    }

    public Application updateApplication(String applicationId,
                                         File appBinary,
                                         String author,
                                         String version,
                                         String versionNotes) throws ConnectionException {

        Map<String, Object> data = new HashMap<>();
        if (author != null) {
            data.put("author", author);
        }
        if (version != null) {
            data.put("version_num", version);
        }
        if (versionNotes != null) {
            data.put("version_note", versionNotes);
        }

        RequestDetails requestDetails = new RequestDetails.Builder()
            .withMethod(RequestMethod.POST)
            .withPath(APIConstants.UPDATE_APPS_URL_PATH)
            .withData(data)
            .withFile("app_file", appBinary)
            .build();
        return apiClient.makeRequest(requestDetails, UpdateApplicationResponse.class).getApplication();
    }

    public Application updateApplication(String applicationId, boolean enabled) throws ConnectionException {

        Map<String, Object> data = new HashMap<>();
        data.put("enabled", enabled);

        RequestDetails requestDetails = new RequestDetails.Builder()
            .withMethod(RequestMethod.POST)
            .withPath(APIConstants.UPDATE_APPS_URL_PATH)
            .withData(data)
            .build();
        return apiClient.makeRequest(requestDetails, UpdateApplicationResponse.class).getApplication();
    }

    public Application getApplicationInfo(String appId) throws ConnectionException {
        RequestDetails requestDetails = new RequestDetails.Builder()
            .withPath(APIConstants.GET_APP_URL_PATH, appId)
            .build();
        return apiClient.makeRequest(requestDetails, GetApplicationInfoResponse.class).getApplication();
    }

    public List<SigningCredential> listCredentials() throws ConnectionException {
        RequestDetails requestDetails = new RequestDetails.Builder()
            .withPath(APIConstants.GET_CREDENTIALS_URL_PATH)
            .build();

        return apiClient.makeRequest(requestDetails, ListAllSigningCredentialsResponse.class).getCredentials();
    }

    public SignApplicationResponse signApplication(String credentialsId, String appId) throws ConnectionException {
        RequestDetails requestDetails = new RequestDetails.Builder()
            .withMethod(RequestMethod.PUT)
            .withPath(APIConstants.SIGN_APP_URL_PATH, appId, credentialsId)
            .build();
        return apiClient.makeRequest(requestDetails, SignApplicationResponse.class);
    }

    public UserInfoResponse getUserDetails() throws ConnectionException {
        RequestDetails requestDetails = new RequestDetails.Builder()
            .withPath(APIConstants.GET_USER_INFO_URL_PATH)
            .build();

        return apiClient.makeRequest(requestDetails, UserInfoResponse.class);
    }

}
