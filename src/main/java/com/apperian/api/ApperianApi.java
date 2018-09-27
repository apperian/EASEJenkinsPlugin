package com.apperian.api;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.apperian.api.application.Application;
import com.apperian.api.application.ApplicationListRequest;
import com.apperian.api.application.ApplicationListResponse;
import com.apperian.api.application.GetApplicationInfoRequest;
import com.apperian.api.application.GetApplicationInfoResponse;
import com.apperian.api.application.UpdateApplicationRequest;
import com.apperian.api.application.UpdateApplicationResponse;
import com.apperian.api.signing.ListAllSigningCredentialsRequest;
import com.apperian.api.signing.ListAllSigningCredentialsResponse;
import com.apperian.api.signing.SignApplicationRequest;
import com.apperian.api.signing.SignApplicationResponse;
import com.apperian.api.signing.SigningCredential;

public class ApperianApi {

    static final Logger logger = Logger.getLogger(ApperianApi.class.getName());

    private ApperianEndpoint endpoint;

    public ApperianApi(String baseUrl, String sessionToken) {
        this.endpoint = new ApperianEndpoint(baseUrl, sessionToken);
    }

    public List<Application> listApplications() throws ConnectionException {
        ApplicationListRequest request = new ApplicationListRequest();
        return request.call(endpoint).getApplications();
    }

    public Application updateApplication(ApperianResourceID applicationId, File appBinary, String author, String version, String versionNotes) throws ConnectionException {
        UpdateApplicationRequest request = new UpdateApplicationRequest(applicationId);

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

        return request.call(endpoint, data, appBinary).getApplication();
    }

    public Application updateApplication(ApperianResourceID applicationId, boolean enabled) throws ConnectionException {
        UpdateApplicationRequest request = new UpdateApplicationRequest(applicationId);
        Map<String, Object> data = new HashMap<>();
        data.put("enabled", enabled);
        return request.call(endpoint, data).getApplication();
    }

    public Application getApplicationInfo(ApperianResourceID applicationId) throws ConnectionException {
        GetApplicationInfoRequest request =  new GetApplicationInfoRequest(applicationId);
        return request.call(endpoint).getApplication();
    }

    public List<SigningCredential> listCredentials() throws ConnectionException {
        return new ListAllSigningCredentialsRequest().call(endpoint).getCredentials();
    }

    // TODO JJJ this should not return a response (try to isolate the network from the api consumer)
    public SignApplicationResponse signApplication(ApperianResourceID credentialsId,
                                                   ApperianResourceID applicationId) throws ConnectionException {
        return new SignApplicationRequest(applicationId, credentialsId).call(endpoint);
    }

    public boolean isConnectionSuccessful() {
        try {
            endpoint.checkSessionToken();
            return true;
        } catch (Exception e) {
            String message = "Could not authenticate to '" + endpoint.getUrl() + "', error:  " + e.getMessage();
            logger.log(Level.WARNING, message, e);
            endpoint.setLastLoginError(e.getMessage());
        }
        return false;
    }

}
