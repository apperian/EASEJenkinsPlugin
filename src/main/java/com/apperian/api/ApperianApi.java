package com.apperian.api;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.apperian.api.applications.Application;
import com.apperian.api.applications.ApplyPoliciesResponse;
import com.apperian.api.applications.GetApplicationResponse;
import com.apperian.api.applications.GetApplicationsResponse;
import com.apperian.api.applications.GetPoliciesResponse;
import com.apperian.api.applications.PolicyConfiguration;
import com.apperian.api.applications.UpdateApplicationResponse;
import com.apperian.api.signing.GetSigningCredentialsResponse;
import com.apperian.api.signing.SignApplicationResponse;
import com.apperian.api.signing.SigningCredential;
import com.apperian.api.users.GetUserResponse;
import com.apperian.api.users.User;

public class ApperianApi {

    private String baseUrl;
    private String sessionToken;

    public ApperianApi(String baseUrl, String sessionToken) {
        this.baseUrl = baseUrl;
        this.sessionToken = sessionToken;
    }

    public List<Application> listApplications() throws ConnectionException {
        ApiClient apiClient = new ApiClient.Builder(baseUrl, sessionToken)
            .withPath(ApiConstants.LIST_APPS_URL_PATH)
            .build();
        return apiClient.makeRequest(GetApplicationsResponse.class).getApplications();
    }

    public Application createNewVersion(String applicationId,
                                         File appBinary,
                                         String appName,
                                         String author,
                                         String shortDescription,
                                         String longDescription,
                                         String version,
                                         String versionNotes,
                                         boolean enableApp) throws ConnectionException {

        Map<String, Object> data = new HashMap<>();
        if (author != null) {
            data.put("app_name", appName);
        }
        if (author != null) {
            data.put("author", author);
        }
        if (shortDescription != null) {
            data.put("short_description", shortDescription);
        }
        if (longDescription != null) {
            data.put("long_description", longDescription);
        }
        if (version != null) {
            data.put("version_num", version);
        }
        if (versionNotes != null) {
            data.put("version_note", versionNotes);
        }
        data.put("enabled", enableApp);

        ApiClient apiClient = new ApiClient.Builder(baseUrl, sessionToken)
            .withMethod(RequestMethod.POST)
            .withPath(ApiConstants.CREATE_NEW_VERSION_URL_PATH, applicationId)
            .withData(data)
            .withFile("app_file", appBinary)
            .build();
        return apiClient.makeRequest(UpdateApplicationResponse.class).getApplication();
    }

    public Application updateApplication(String applicationId, boolean enabled) throws ConnectionException {

        Map<String, Object> data = new HashMap<>();
        data.put("enabled", enabled);

        ApiClient apiClient = new ApiClient.Builder(baseUrl, sessionToken)
            .withMethod(RequestMethod.PUT)
            .withPath(ApiConstants.UPDATE_APP_URL_PATH, applicationId)
            .withData(data)
            .build();
        return apiClient.makeRequest(UpdateApplicationResponse.class).getApplication();
    }

    public Application getApplicationInfo(String appId) throws ConnectionException {
        ApiClient apiClient = new ApiClient.Builder(baseUrl, sessionToken)
            .withPath(ApiConstants.GET_APP_URL_PATH, appId)
            .build();
        return apiClient.makeRequest(GetApplicationResponse.class).getApplication();
    }

    public List<SigningCredential> listCredentials() throws ConnectionException {
        ApiClient apiClient = new ApiClient.Builder(baseUrl, sessionToken)
            .withPath(ApiConstants.GET_CREDENTIALS_URL_PATH)
            .build();

        return apiClient.makeRequest(GetSigningCredentialsResponse.class).getCredentials();
    }

    public SignApplicationResponse signApplication(String credentialsId, String appId) throws ConnectionException {
        ApiClient apiClient = new ApiClient.Builder(baseUrl, sessionToken)
            .withMethod(RequestMethod.PUT)
            .withPath(ApiConstants.SIGN_APP_URL_PATH, appId, credentialsId)
            .build();
        return apiClient.makeRequest(SignApplicationResponse.class);
    }

    public User getUserDetails() throws ConnectionException {
        ApiClient apiClient = new ApiClient.Builder(baseUrl, sessionToken)
            .withPath(ApiConstants.GET_USER_INFO_URL_PATH)
            .build();

        return apiClient.makeRequest(GetUserResponse.class).getUser();
    }

    public GetPoliciesResponse getAppliedPolicies(String applicationId) throws ConnectionException {
        ApiClient apiClient = new ApiClient.Builder(baseUrl, sessionToken)
            .withPath(ApiConstants.GET_APPLIED_POLICIES_PATH, applicationId)
            .build();
        return apiClient.makeRequest(GetPoliciesResponse.class);
    }

    public ApplyPoliciesResponse applyPolicies(String applicationId, List<PolicyConfiguration> policyConfigurations)
                                            throws ConnectionException {
        Map<String, Object> data = new HashMap<>();
        data.put("configurations", policyConfigurations);
        ApiClient apiClient = new ApiClient.Builder(baseUrl, sessionToken)
            .withMethod(RequestMethod.POST)
            .withPath(ApiConstants.APPLY_POLICIES_PATH, applicationId)
            .withData(data)
            .build();
        return apiClient.makeRequest(ApplyPoliciesResponse.class);
    }

}
