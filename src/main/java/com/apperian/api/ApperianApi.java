package com.apperian.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

public class ApperianApi {

    public ApplicationListResponse listApplications(ApperianEndpoint endpoint) throws ConnectionException {
        ApplicationListRequest request = new ApplicationListRequest();
        return request.call(endpoint);
    }

    public UpdateApplicationResponse updateApplication(ApperianEndpoint endpoint, ApperianResourceID applicationId, File appBinary, String author, String version, String versionNotes) throws ConnectionException {
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

        return request.call(endpoint, data, appBinary);
    }

    public UpdateApplicationResponse updateApplication(ApperianEndpoint endpoint, ApperianResourceID applicationId, boolean enabled) throws ConnectionException {
        UpdateApplicationRequest request = new UpdateApplicationRequest(applicationId);
        Map<String, Object> data = new HashMap<>();
        data.put("enabled", enabled);
        return request.call(endpoint, data);
    }

    public GetApplicationInfoResponse getApplicationInfo(ApperianEndpoint endpoint, ApperianResourceID applicationId) throws ConnectionException {
        GetApplicationInfoRequest request =  new GetApplicationInfoRequest(applicationId);
        return request.call(endpoint);
    }

    public ListAllSigningCredentialsResponse listCredentials(ApperianEndpoint endpoint) throws ConnectionException {
        return new ListAllSigningCredentialsRequest().call(endpoint);
    }

    public SignApplicationResponse signApplication(ApperianEndpoint endpoint,
                                                   ApperianResourceID credentialsId,
                                                   ApperianResourceID applicationId) throws ConnectionException {
        return new SignApplicationRequest(applicationId, credentialsId).call(endpoint);
    }

}
